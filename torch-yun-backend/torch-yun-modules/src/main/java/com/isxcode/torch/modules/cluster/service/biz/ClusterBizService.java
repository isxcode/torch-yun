package com.isxcode.torch.modules.cluster.service.biz;

import com.isxcode.torch.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.torch.api.cluster.req.*;
import com.isxcode.torch.api.cluster.res.PageClusterRes;
import com.isxcode.torch.api.cluster.res.QueryAllClusterRes;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.common.utils.aes.AesUtils;
import com.isxcode.torch.modules.cluster.entity.ClusterEntity;
import com.isxcode.torch.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.torch.modules.cluster.mapper.ClusterMapper;
import com.isxcode.torch.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.torch.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.torch.modules.cluster.repository.ClusterRepository;
import com.isxcode.torch.modules.cluster.run.RunAgentCheckService;
import com.isxcode.torch.modules.cluster.service.ClusterService;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ClusterBizService {

    private final ClusterRepository clusterRepository;

    private final ClusterNodeRepository clusterNodeRepository;

    private final ClusterMapper clusterMapper;

    private final RunAgentCheckService runAgentCheckService;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    private final ClusterService clusterService;

    public void addCluster(AddClusterReq addClusterReq) {

        // 集群名字不能重复
        Optional<ClusterEntity> clusterByName = clusterRepository.findByName(addClusterReq.getName());
        if (clusterByName.isPresent()) {
            throw new IsxAppException("集群名称重复");
        }

        // 转换对象
        ClusterEntity cluster = clusterMapper.addEngineReqToClusterEntity(addClusterReq);

        // 第一个集群设置为默认集群
        long count = clusterRepository.count();
        cluster.setDefaultCluster(count == 0);

        clusterRepository.save(cluster);
    }

    public void updateCluster(UpdateClusterReq updateClusterReq) {

        ClusterEntity cluster = clusterService.getCluster(updateClusterReq.getClusterId());
        cluster = clusterMapper.updateEngineReqToClusterEntity(updateClusterReq, cluster);
        clusterRepository.save(cluster);
    }

    public Page<PageClusterRes> pageCluster(PageClusterReq pageClusterReq) {

        Page<ClusterEntity> clusterPage = clusterRepository.pageCluster(pageClusterReq.getSearchKeyWord(),
            PageRequest.of(pageClusterReq.getPage(), pageClusterReq.getPageSize()));

        return clusterPage.map(clusterMapper::clusterEntityToPageClusterRes);
    }

    public void deleteCluster(DeleteClusterReq deleteClusterReq) {

        // 所有的节点都卸载了，才能删除集群
        List<ClusterNodeEntity> allNode = clusterNodeRepository.findAllByClusterId(deleteClusterReq.getEngineId());
        boolean canNoteDelete = allNode.stream().anyMatch(e -> ClusterNodeStatus.RUNNING.equals(e.getStatus()));
        if (canNoteDelete) {
            throw new IsxAppException("存在节点未卸载");
        }

        clusterRepository.deleteById(deleteClusterReq.getEngineId());
    }

    public void checkCluster(CheckClusterReq checkClusterReq) {

        clusterService.checkCluster(checkClusterReq.getEngineId());
    }

    public void setDefaultCluster(SetDefaultClusterReq setDefaultClusterReq) {

        // 检查集群是否存在
        ClusterEntity cluster = clusterService.getCluster(setDefaultClusterReq.getClusterId());

        // 将租户下的所有其他集群的默认集群变为false
        List<ClusterEntity> clusterEntities = clusterRepository.findAll();
        clusterEntities.forEach(e -> e.setDefaultCluster(false));
        clusterRepository.saveAll(clusterEntities);

        // 将指定的集群的默认集群变为true
        cluster.setDefaultCluster(true);
        clusterRepository.save(cluster);
    }

    public List<QueryAllClusterRes> queryAllCluster() {

        List<ClusterEntity> all = clusterRepository.findAll();

        return clusterMapper.clusterEntityListToQueryAllClusterResList(all);
    }
}
