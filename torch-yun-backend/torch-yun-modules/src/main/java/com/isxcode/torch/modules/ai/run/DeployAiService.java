package com.isxcode.torch.modules.ai.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.agent.constants.AgentUrl;
import com.isxcode.torch.api.agent.req.DeployAiReq;
import com.isxcode.torch.api.agent.res.DeployAiRes;
import com.isxcode.torch.api.ai.constant.AiStatus;
import com.isxcode.torch.api.app.constants.AppStatus;
import com.isxcode.torch.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.torch.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.torch.api.work.constants.WorkLog;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.backend.api.base.pojos.BaseResponse;
import com.isxcode.torch.backend.api.base.properties.IsxAppProperties;
import com.isxcode.torch.common.utils.aes.AesUtils;
import com.isxcode.torch.common.utils.http.HttpUrlUtils;
import com.isxcode.torch.common.utils.http.HttpUtils;
import com.isxcode.torch.common.utils.path.PathUtils;
import com.isxcode.torch.modules.ai.entity.AiEntity;
import com.isxcode.torch.modules.ai.repository.AiRepository;
import com.isxcode.torch.modules.ai.service.AiService;
import com.isxcode.torch.modules.app.entity.AppEntity;
import com.isxcode.torch.modules.app.repository.AppRepository;
import com.isxcode.torch.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.torch.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.torch.modules.cluster.repository.ClusterNodeRepository;
import com.isxcode.torch.modules.file.entity.FileEntity;
import com.isxcode.torch.modules.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployAiService {

    private final AiService aiService;

    private final AiRepository aiRepository;

    private final ClusterNodeRepository clusterNodeRepository;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    private final ScpFileService scpFileService;

    private final IsxAppProperties isxAppProperties;

    private final HttpUrlUtils httpUrlUtils;

    private final AppRepository appRepository;

    private final FileService fileService;

    @Async
    public void deployAi(DeployAiContext deployAiContext) {

        AiEntity ai = aiService.getAi(deployAiContext.getAiId());

        // 随机一个集群id
        List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository
            .findAllByClusterIdAndStatus(deployAiContext.getClusterConfig().getClusterId(), ClusterNodeStatus.RUNNING);
        if (allEngineNodes.isEmpty()) {
            throw new IsxAppException("申请资源失败 : 集群不存在可用节点，请切换一个集群  \n");
        }
        ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));

        // 翻译节点信息
        ScpFileEngineNodeDto scpFileEngineNodeDto =
            clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
        scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

        try {

            FileEntity file = fileService.getFile(deployAiContext.getModelFileId());

            // 非系统模型需要上传
            String srcPath = PathUtils.parseProjectPath(isxAppProperties.getResourcesPath()) + File.separator + "file"
                + File.separator + engineNode.getTenantId() + File.separator + deployAiContext.getModelFileId();

            // 如果docker部署，使用指定目录获取系统驱动
            if (isxAppProperties.isDockerMode() && "zhishuyun".equals(file.getTenantId())) {
                srcPath = "/var/lib/zhishuyun-system" + File.separator + deployAiContext.getModelFileId();
            }

            String distPath =
                engineNode.getAgentHomePath() + "/zhishuyun-agent/file/" + deployAiContext.getModelFileId();

            // 看看模型是否存在
            boolean fileIsUpload = scpFileService.modelFileIsUpload(scpFileEngineNodeDto, srcPath, distPath);
            if (!fileIsUpload) {
                // 异步上传安装包
                scpFileService.scpFile(scpFileEngineNodeDto, srcPath, distPath);

                // 添加日志
                ai = aiService.getAi(deployAiContext.getAiId());
                ai.setAiLog(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "开始上传模型");
                ai = aiRepository.saveAndFlush(ai);

                // 同步监听进度
                scpFileService.listenScpPercent(scpFileEngineNodeDto, srcPath, distPath, ai);
            } else {
                // 添加日志
                ai.setAiLog(LocalDateTime.now() + WorkLog.SUCCESS_INFO + "模型已经上传，开始部署模型");
                ai = aiRepository.saveAndFlush(ai);
            }

            // 调用模型部署接口
            DeployAiReq deployAiReq = DeployAiReq.builder().agentHomePath(engineNode.getAgentHomePath())
                .deployScript(deployAiContext.getDeployScript()).modelCode(deployAiContext.getModelCode())
                .modelFileId(deployAiContext.getModelFileId()).aiId(ai.getId()).build();
            BaseResponse<?> baseResponse = HttpUtils.doPost(
                httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), AgentUrl.DEPLOY_AI_URL),
                deployAiReq, BaseResponse.class);
            if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                throw new IsxAppException(baseResponse.getMsg());
            }
            // 解析返回状态，并保存
            DeployAiRes deployAiRes = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), DeployAiRes.class);

            // 运行返回端口号
            ai = aiService.getAi(deployAiContext.getAiId());
            ai.setAiLog(ai.getAiLog() + "\n" + LocalDateTime.now() + WorkLog.SUCCESS_INFO + "运行中");
            ai.setStatus(AiStatus.ENABLE);
            ai.setAiPid(deployAiRes.getAiPid());
            ai.setAiPort(deployAiRes.getAiPort());
            aiRepository.save(ai);

            // 部署成功，将初始化应用改成可用模式
            List<AppEntity> byStatusAndAiId = appRepository.findByStatusAndAiId(AppStatus.INIT, ai.getId());
            byStatusAndAiId.forEach(appEntity -> appEntity.setStatus(AppStatus.ENABLE));
            appRepository.saveAll(byStatusAndAiId);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ai = aiService.getAi(deployAiContext.getAiId());
            ai.setStatus(AiStatus.DEPLOY_FAIL);
            ai.setAiLog("部署失败：" + e.getMessage());
            aiRepository.save(ai);
        }

    }
}
