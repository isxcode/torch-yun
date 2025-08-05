package com.isxcode.torch.modules.ai.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.agent.constants.AgentUrl;
import com.isxcode.torch.api.agent.req.GetAgentAiLogReq;
import com.isxcode.torch.api.agent.req.StopAgentAiReq;
import com.isxcode.torch.api.agent.res.GetAgentAiLogRes;
import com.isxcode.torch.api.ai.constant.AiStatus;
import com.isxcode.torch.api.ai.dto.ClusterConfig;
import com.isxcode.torch.api.ai.req.*;
import com.isxcode.torch.api.ai.res.GetAiLogRes;
import com.isxcode.torch.api.ai.res.PageAiRes;

import javax.transaction.Transactional;

import com.isxcode.torch.api.app.constants.AppStatus;
import com.isxcode.torch.api.app.constants.DefaultAppStatus;
import com.isxcode.torch.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.torch.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.torch.api.model.constant.ModelType;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.backend.api.base.pojos.BaseResponse;
import com.isxcode.torch.common.utils.aes.AesUtils;
import com.isxcode.torch.common.utils.http.HttpUrlUtils;
import com.isxcode.torch.common.utils.http.HttpUtils;
import com.isxcode.torch.modules.ai.entity.AiEntity;
import com.isxcode.torch.modules.ai.mapper.AiMapper;
import com.isxcode.torch.modules.ai.repository.AiRepository;
import com.isxcode.torch.modules.ai.run.DeployAiContext;
import com.isxcode.torch.modules.ai.run.DeployAiService;
import com.isxcode.torch.modules.app.entity.AppEntity;
import com.isxcode.torch.modules.app.repository.AppRepository;
import com.isxcode.torch.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.torch.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.torch.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.torch.modules.cluster.service.ClusterService;
import com.isxcode.torch.modules.model.entity.ModelEntity;
import com.isxcode.torch.modules.model.service.ModelService;
import com.isxcode.torch.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.ap.internal.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.isxcode.torch.common.config.CommonConfig.JPA_TENANT_MODE;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AiBizService {

    private final AiRepository aiRepository;

    private final AiMapper aiMapper;

    private final AiService aiService;

    private final UserService userService;

    private final ClusterService clusterService;

    private final ModelService modelService;

    private final AppRepository appRepository;

    private final DeployAiService deployAiService;

    private final HttpUrlUtils httpUrlUtils;

    private final ClusterNodeRepository clusterNodeRepository;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    public void addAi(AddAiReq addAiReq) {

        // 检测数据源名称重复
        Optional<AiEntity> aiEntityByName = aiRepository.findByName(addAiReq.getName());
        if (aiEntityByName.isPresent()) {
            throw new IsxAppException("Ai名称重复");
        }

        // 封装智能体对象
        AiEntity aiEntity = aiMapper.addAiReqToAiEntity(addAiReq);

        // 通过模型id判断当前所需参数
        JPA_TENANT_MODE.set(false);
        ModelEntity model = modelService.getModel(addAiReq.getModelId());
        JPA_TENANT_MODE.set(true);

        // 自动创建对应的应用
        AppEntity appEntity = new AppEntity();
        if (ModelType.API.equals(model.getModelType())) {
            if (addAiReq.getAuthConfig() == null) {
                throw new IsxAppException("验证信息缺失");
            }
            aiEntity.setAuthConfig(JSON.toJSONString(addAiReq.getAuthConfig()));
            aiEntity.setStatus(AiStatus.ENABLE);
            appEntity.setStatus(AiStatus.ENABLE);
        } else if (ModelType.MANUAL.equals(model.getModelType())) {
            if (addAiReq.getClusterConfig() == null) {
                throw new IsxAppException("集群配置缺失");
            }
            aiEntity.setClusterConfig(JSON.toJSONString(addAiReq.getClusterConfig()));
            aiEntity.setStatus(AiStatus.DISABLE);
            appEntity.setStatus(AppStatus.INIT);
        } else {
            throw new IsxAppException("当前模型不支持");
        }

        aiEntity.setCheckDateTime(LocalDateTime.now());
        aiEntity = aiRepository.save(aiEntity);
        appEntity.setName(addAiReq.getName());
        appEntity.setAiId(aiEntity.getId());
        appEntity.setCheckDateTime(LocalDateTime.now());
        appEntity.setLogoId("");
        appEntity.setRemark("默认自建");

        // 判断是否需要制定默认app应用
        List<AppEntity> allApp = appRepository.findAll();
        if (allApp.isEmpty()) {
            appEntity.setDefaultApp(DefaultAppStatus.ENABLE);
        } else {
            appEntity.setDefaultApp(DefaultAppStatus.DISABLE);
        }
        appRepository.save(appEntity);
    }

    public void updateAi(UpdateAiReq updateAiReq) {

        // 检测数据源名称重复
        Optional<AiEntity> aiEntityByName = aiRepository.findByName(updateAiReq.getName());
        if (aiEntityByName.isPresent() && !aiEntityByName.get().getId().equals(updateAiReq.getId())) {
            throw new IsxAppException("ai名称重复");
        }

        AiEntity ai = aiService.getAi(updateAiReq.getId());
        if (AiStatus.DEPLOYING.equals(ai.getStatus())) {
            throw new IsxAppException("部署中，不可编辑");
        }

        AiEntity aiEntity = aiMapper.updateAiReqToAiEntity(updateAiReq, ai);

        JPA_TENANT_MODE.set(true);
        ModelEntity model = modelService.getModel(updateAiReq.getId());
        JPA_TENANT_MODE.set(false);

        if (ModelType.API.equals(model.getModelType())) {
            if (updateAiReq.getAuthConfig() == null) {
                throw new IsxAppException("验证信息缺失");
            }
            aiEntity.setAuthConfig(JSON.toJSONString(updateAiReq.getAuthConfig()));
        } else if (ModelType.MANUAL.equals(model.getModelType())) {
            if (updateAiReq.getClusterConfig() == null) {
                throw new IsxAppException("集群配置缺失");
            }
            aiEntity.setClusterConfig(JSON.toJSONString(updateAiReq.getClusterConfig()));
            aiEntity.setStatus(AiStatus.DISABLE);
        } else {
            throw new IsxAppException("当前模型不支持");
        }

        aiRepository.save(aiEntity);
    }

    public Page<PageAiRes> pageAi(PageAiReq pageAiReq) {

        Page<AiEntity> aiEntityPage = aiRepository.searchAll(pageAiReq.getSearchKeyWord(),
            PageRequest.of(pageAiReq.getPage(), pageAiReq.getPageSize()));

        Page<PageAiRes> result = aiEntityPage.map(aiMapper::aiEntityToPageAiRes);
        result.forEach(aiEntity -> {
            if (aiEntity.getClusterConfig() != null) {
                aiEntity.setClusterName(clusterService
                    .getClusterName(JSON.parseObject(aiEntity.getClusterConfig(), ClusterConfig.class).getClusterId()));
            }
            aiEntity.setCreateByUsername(userService.getUserName(aiEntity.getCreateBy()));
            JPA_TENANT_MODE.set(false);
            aiEntity.setModelName(modelService.getModelName(aiEntity.getModelId()));
            JPA_TENANT_MODE.set(true);
            ModelEntity model = modelService.getModel(aiEntity.getModelId());
            aiEntity.setAiType(model.getModelType());
        });

        return result;
    }

    public void deployAi(DeployAiReq deployAiReq) {

        // 判断智能体是否存在
        AiEntity ai = aiService.getAi(deployAiReq.getId());

        // 状态是否可以部署
        if (AiStatus.ENABLE.equals(ai.getStatus())) {
            throw new IsxAppException("当前状态不可部署");
        }

        // 获取模型仓库
        ModelEntity model = modelService.getModel(ai.getModelId());

        // 封装请求体
        DeployAiContext deployAiContext = DeployAiContext.builder().aiId(ai.getId())
            .clusterConfig(JSON.parseObject(ai.getClusterConfig(), ClusterConfig.class)).modelCode(model.getCode())
            .modelFileId(model.getModelFile()).build();
        deployAiService.deployAi(deployAiContext);

        // 修改状态
        ai.setStatus(AiStatus.DEPLOYING);
        aiRepository.save(ai);
    }

    public void stopAi(StopAiReq stopAiReq) {

        // 判断ai是否存在
        AiEntity ai = aiService.getAi(stopAiReq.getId());

        // 判断pid值
        if (Strings.isEmpty(ai.getAiPid())) {
            throw new IsxAppException("智能体启动异常");
        }
        // 随机一个集群id
        List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository.findAllByClusterIdAndStatus(
            JSON.parseObject(ai.getClusterConfig(), ClusterConfig.class).getClusterId(), ClusterNodeStatus.RUNNING);
        if (allEngineNodes.isEmpty()) {
            throw new IsxAppException("申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
        }
        ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));

        // 翻译节点信息
        ScpFileEngineNodeDto scpFileEngineNodeDto =
            clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
        scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

        // 获取pid
        StopAgentAiReq stopAgentAiReq = StopAgentAiReq.builder().pid(ai.getAiPid()).build();

        // 调用代理停止
        BaseResponse<?> baseResponse = HttpUtils.doPost(
            httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), AgentUrl.STOP_AI_URL),
            stopAgentAiReq, BaseResponse.class);
        if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
            throw new IsxAppException(baseResponse.getMsg());
        }

        // 修改智能体状态
        ai.setStatus(AiStatus.DISABLE);
        ai.setAiLog(ai.getAiLog() + "\n已经停止");
        aiRepository.save(ai);
    }

    public GetAiLogRes getAiLog(GetAiLogReq getAiLogReq) {

        // 判断ai是否存在
        AiEntity ai = aiService.getAi(getAiLogReq.getId());

        // 随机一个集群id
        List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository.findAllByClusterIdAndStatus(
            JSON.parseObject(ai.getClusterConfig(), ClusterConfig.class).getClusterId(), ClusterNodeStatus.RUNNING);
        if (allEngineNodes.isEmpty()) {
            throw new IsxAppException("申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
        }
        ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));

        // 翻译节点信息
        ScpFileEngineNodeDto scpFileEngineNodeDto =
            clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
        scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

        // 封装请求
        GetAgentAiLogReq getAgentAiLogReq =
            GetAgentAiLogReq.builder().agentHomePath(engineNode.getAgentHomePath()).aiId(ai.getId()).build();

        // 调用代理停止
        BaseResponse<?> baseResponse = HttpUtils.doPost(
            httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), AgentUrl.GET_AI_LOG_URL),
            getAgentAiLogReq, BaseResponse.class);
        if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
            throw new IsxAppException(baseResponse.getMsg());
        }

        // 修改智能体状态
        GetAgentAiLogRes getAgentAiLogRes =
            JSON.parseObject(JSON.toJSONString(baseResponse.getData()), GetAgentAiLogRes.class);

        return GetAiLogRes.builder().log(getAgentAiLogRes.getLog()).build();
    }
}
