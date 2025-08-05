package com.isxcode.torch.agent.controller;

import com.isxcode.torch.agent.service.PytorchYunAgentBizService;
import com.isxcode.torch.api.agent.req.ChatAgentAiReq;
import com.isxcode.torch.api.agent.req.DeployAiReq;
import com.isxcode.torch.api.agent.constants.AgentUrl;
import com.isxcode.torch.api.agent.req.GetAgentAiLogReq;
import com.isxcode.torch.api.agent.req.StopAgentAiReq;
import com.isxcode.torch.api.agent.res.ChatAgentAiRes;
import com.isxcode.torch.api.agent.res.DeployAiRes;
import com.isxcode.torch.api.agent.res.GetAgentAiLogRes;
import com.isxcode.torch.common.annotations.successResponse.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Tag(name = "至数云代理模块")
@RestController
@RequiredArgsConstructor
public class PytorchYunAgentController {

    private final PytorchYunAgentBizService pytorchYunAgentBizService;

    @Operation(summary = "部署ai接口")
    @PostMapping(AgentUrl.DEPLOY_AI_URL)
    @SuccessResponse("部署成功")
    public DeployAiRes deployAi(@Valid @RequestBody DeployAiReq deployAiReq) {

        return pytorchYunAgentBizService.deployAi(deployAiReq);
    }

    @Operation(summary = "停止ai接口")
    @PostMapping(AgentUrl.STOP_AI_URL)
    @SuccessResponse("停止成功")
    public void stopAi(@Valid @RequestBody StopAgentAiReq stopAgentAiReq) {

        pytorchYunAgentBizService.stopAi(stopAgentAiReq);
    }

    @Operation(summary = "获取ai日志接口")
    @PostMapping(AgentUrl.GET_AI_LOG_URL)
    @SuccessResponse("获取成功")
    public GetAgentAiLogRes getAiLog(@Valid @RequestBody GetAgentAiLogReq getAgentAiLogReq) {

        return pytorchYunAgentBizService.getAiLog(getAgentAiLogReq);
    }

    @Operation(summary = "调用ai接口")
    @PostMapping(AgentUrl.CHAT_AI_URL)
    @SuccessResponse("对话请求成功")
    public ChatAgentAiRes chatAi(@Valid @RequestBody ChatAgentAiReq chatAgentAiReq) {

        return pytorchYunAgentBizService.chatAi(chatAgentAiReq);
    }

    @Operation(summary = "心跳检测接口")
    @PostMapping(AgentUrl.HEART_CHECK_URL)
    @SuccessResponse("正常心跳")
    public void heartCheck() {}
}
