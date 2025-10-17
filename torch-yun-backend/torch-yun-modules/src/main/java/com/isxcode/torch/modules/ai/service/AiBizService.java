package com.isxcode.torch.modules.ai.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.agent.constants.AgentUrl;
import com.isxcode.torch.api.agent.req.CheckAgentAiReq;
import com.isxcode.torch.api.agent.req.DeleteAgentAiReq;
import com.isxcode.torch.api.agent.req.GetAgentAiLogReq;
import com.isxcode.torch.api.agent.req.StopAgentAiReq;
import com.isxcode.torch.api.agent.res.CheckAgentAiRes;
import com.isxcode.torch.api.agent.res.GetAgentAiLogRes;
import com.isxcode.torch.api.ai.constant.AiStatus;
import com.isxcode.torch.api.ai.constant.AiType;
import com.isxcode.torch.api.ai.dto.ClusterConfig;
import com.isxcode.torch.api.ai.req.*;
import com.isxcode.torch.api.ai.res.CheckAiRes;
import com.isxcode.torch.api.ai.res.GetAiLogRes;
import com.isxcode.torch.api.ai.res.PageAiRes;

import javax.transaction.Transactional;

import com.isxcode.torch.api.app.constants.AppStatus;
import com.isxcode.torch.api.app.constants.AppType;
import com.isxcode.torch.api.app.constants.DefaultAppStatus;
import com.isxcode.torch.api.app.dto.BaseConfig;
import com.isxcode.torch.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.torch.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.torch.api.model.constant.ModelType;
import com.isxcode.torch.api.work.constants.WorkLog;
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

import java.util.List;

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
        appEntity.setAppType(AppType.TEXT_APP);

        // 应用添加默认配置
        BaseConfig baseConfig = BaseConfig.builder().topK(50).topP(0.9).maxTokens(512).repetitionPenalty(1.2f)
            .enableSearch(false).temperature(0.8f).build();
        appEntity.setBaseConfig(JSON.toJSONString(baseConfig));
        appEntity.setPrompt("");

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

        JPA_TENANT_MODE.set(false);
        ModelEntity model = modelService.getModel(ai.getModelId());
        JPA_TENANT_MODE.set(true);

        if (ModelType.API.equals(model.getModelType())) {
            if (updateAiReq.getAuthConfig() == null) {
                throw new IsxAppException("验证信息缺失");
            }
            aiEntity.setAuthConfig(JSON.toJSONString(updateAiReq.getAuthConfig()));
        } else if (ModelType.MANUAL.equals(model.getModelType())) {
            if (updateAiReq.getClusterConfig() == null) {
                throw new IsxAppException("集群配置缺失");
            }
            aiEntity.setClusterConfig(updateAiReq.getClusterConfig().getClusterId());
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

            aiEntity.setAiType(modelService.getModelType(aiEntity.getModelId()));
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
        ModelEntity model;
        if ("Qwen2.5-0.5B".equals(ai.getModelId())) {
            model = new ModelEntity();
            model.setCode("Qwen2.5-0.5B");
            model.setModelFile("Qwen2.5-0.5B.zip");
            model.setModelType(ModelType.MANUAL);
        } else {
            JPA_TENANT_MODE.set(false);
            model = modelService.getModel(ai.getModelId());
            JPA_TENANT_MODE.set(true);
        }

        // 修改状态
        ai.setStatus(AiStatus.DEPLOYING);
        if (ModelType.API.equals(model.getModelType())) {
            ai.setStatus(AiStatus.ENABLE);
        }
        aiRepository.saveAndFlush(ai);

        // 本地部署需要启动服务、
        if (ModelType.MANUAL.equals(model.getModelType())) {
            // 封装请求体
            DeployAiContext deployAiContext = DeployAiContext.builder().aiId(ai.getId())
                .clusterConfig(JSON.parseObject(ai.getClusterConfig(), ClusterConfig.class)).modelCode(model.getCode())
                .modelFileId(model.getModelFile()).build();
            deployAiService.deployAi(deployAiContext);
        }
    }

    public void stopAi(StopAiReq stopAiReq) {

        // 判断ai是否存在
        AiEntity ai = aiService.getAi(stopAiReq.getId());

        // 本地模型，需要停止服务
        JPA_TENANT_MODE.set(false);
        ModelEntity model = modelService.getModel(ai.getModelId());
        JPA_TENANT_MODE.set(true);
        if (ModelType.MANUAL.equals(model.getModelType())) {
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
        }

        // 修改智能体状态
        ai.setStatus(AiStatus.DISABLE);
        ai.setAiLog(ai.getAiLog() + "\n" + LocalDateTime.now() + WorkLog.SUCCESS_INFO + " 已停止");
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
            return GetAiLogRes.builder().log(baseResponse.getMsg()).build();
        }

        // 修改智能体状态
        GetAgentAiLogRes getAgentAiLogRes =
            JSON.parseObject(JSON.toJSONString(baseResponse.getData()), GetAgentAiLogRes.class);

        return GetAiLogRes.builder().log(getAgentAiLogRes.getLog()).build();
    }

    public GetAiLogRes getAiDeployLog(GetAiLogReq getAiLogReq) {

        // 判断ai是否存在
        AiEntity ai = aiService.getAi(getAiLogReq.getId());
        return GetAiLogRes.builder().log(ai.getAiLog()).build();
    }

    public CheckAiRes checkAi(CheckAiReq checkAiReq) {

        // 判断ai是否存在
        AiEntity ai = aiService.getAi(checkAiReq.getId());

        JPA_TENANT_MODE.set(false);
        ModelEntity model = modelService.getModel(ai.getModelId());
        JPA_TENANT_MODE.set(true);

        // 根据智能体类型选择不同的检测方式
        if (AiType.API.equals(model.getModelType())) {
            // API类型智能体检测
            return checkApiAi(ai);
        } else {
            // 本地部署智能体检测
            return checkLocalAi(ai);
        }
    }

    private CheckAiRes checkApiAi(AiEntity ai) {
        try {
            // API类型智能体的检测逻辑
            // 这里可以根据具体的API智能体实现检测逻辑
            // 暂时返回在线状态
            ai.setCheckDateTime(LocalDateTime.now());
            aiRepository.save(ai);

            return CheckAiRes.builder().status("ONLINE").message("API智能体运行正常").build();
        } catch (Exception e) {
            return CheckAiRes.builder().status("OFFLINE").message("API智能体检测失败: " + e.getMessage()).build();
        }
    }

    private CheckAiRes checkLocalAi(AiEntity ai) {
        // 如果没有端口信息，说明智能体未正常启动
        if (Strings.isEmpty(ai.getAiPort())) {
            return CheckAiRes.builder().status("OFFLINE").message("智能体端口信息缺失").build();
        }

        try {
            // 随机一个集群节点
            List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository.findAllByClusterIdAndStatus(
                JSON.parseObject(ai.getClusterConfig(), ClusterConfig.class).getClusterId(), ClusterNodeStatus.RUNNING);
            if (allEngineNodes.isEmpty()) {
                return CheckAiRes.builder().status("OFFLINE").message("集群不存在可用节点").build();
            }
            ClusterNodeEntity engineNode = allEngineNodes.get(new java.util.Random().nextInt(allEngineNodes.size()));

            // 翻译节点信息
            ScpFileEngineNodeDto scpFileEngineNodeDto =
                clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
            scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

            // 封装请求
            CheckAgentAiReq checkAgentAiReq = CheckAgentAiReq.builder().agentHomePath(engineNode.getAgentHomePath())
                .aiId(ai.getId()).aiPort(ai.getAiPort()).build();

            // 调用Agent检测接口
            BaseResponse<?> baseResponse = HttpUtils.doPost(
                httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), AgentUrl.CHECK_AI_URL),
                checkAgentAiReq, BaseResponse.class);

            if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                throw new IsxAppException(baseResponse.getMsg());
            }

            // 解析响应
            CheckAgentAiRes checkAgentAiRes =
                JSON.parseObject(JSON.toJSONString(baseResponse.getData()), CheckAgentAiRes.class);

            // 更新检测时间
            ai.setCheckDateTime(LocalDateTime.now());
            ai.setStatus(AiStatus.ENABLE);
            aiRepository.save(ai);

            return CheckAiRes.builder().status(checkAgentAiRes.getStatus()).message(checkAgentAiRes.getMessage())
                .build();

        } catch (Exception e) {

            // 更新检测时间
            ai.setStatus(AiStatus.DISABLE);
            ai.setCheckDateTime(LocalDateTime.now());
            aiRepository.save(ai);

            // 检测失败，可能智能体已停止
            return CheckAiRes.builder().status("OFFLINE").message("智能体检测失败: " + e.getMessage()).build();
        }
    }

    public void deleteAi(DeleteAiReq deleteAiReq) {

        // 判断智能体是否存在
        AiEntity ai = aiService.getAi(deleteAiReq.getId());

        // 检查智能体状态，如果正在运行或部署中，不允许删除
        if (AiStatus.ENABLE.equals(ai.getStatus())) {
            throw new IsxAppException("智能体正在运行中，请先停止后再删除");
        }
        if (AiStatus.DEPLOYING.equals(ai.getStatus())) {
            throw new IsxAppException("智能体正在部署中，请等待部署完成后再删除");
        }

        JPA_TENANT_MODE.set(false);
        ModelEntity model = modelService.getModel(ai.getModelId());
        JPA_TENANT_MODE.set(true);

        if (ModelType.MANUAL.equals(model.getModelType())) {

            // 随机一个集群节点
            List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository.findAllByClusterIdAndStatus(
                JSON.parseObject(ai.getClusterConfig(), ClusterConfig.class).getClusterId(), ClusterNodeStatus.RUNNING);
            allEngineNodes.forEach(clusterNode -> {
                // 翻译节点信息
                ScpFileEngineNodeDto scpFileEngineNodeDto =
                    clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(clusterNode);
                scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

                // 封装请求
                DeleteAgentAiReq deleteAgentAiReq =
                    DeleteAgentAiReq.builder().agentHomePath(clusterNode.getAgentHomePath()).aiId(ai.getId()).build();

                // 调用Agent检测接口
                BaseResponse<?> baseResponse = HttpUtils.doPost(
                    httpUrlUtils.genHttpUrl(clusterNode.getHost(), clusterNode.getAgentPort(), AgentUrl.DELETE_AI_URL),
                    deleteAgentAiReq, BaseResponse.class);

                if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
                    throw new IsxAppException(baseResponse.getMsg());
                }
            });
        }

        // 删除智能体
        aiRepository.deleteById(ai.getId());
    }
}
