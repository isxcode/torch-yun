package com.isxcode.torch.modules.app.bot.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.agent.constants.AgentUrl;
import com.isxcode.torch.api.agent.req.ChatAgentAiReq;
import com.isxcode.torch.api.agent.res.ChatAgentAiRes;
import com.isxcode.torch.api.app.dto.ChatResponse;
import com.isxcode.torch.api.app.dto.SseBody;
import com.isxcode.torch.api.chat.constants.ChatSseEvent;
import com.isxcode.torch.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.torch.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.torch.api.model.constant.ModelCode;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.backend.api.base.pojos.BaseResponse;
import com.isxcode.torch.common.utils.aes.AesUtils;
import com.isxcode.torch.common.utils.http.HttpUrlUtils;
import com.isxcode.torch.common.utils.http.HttpUtils;
import com.isxcode.torch.modules.app.bot.Bot;
import com.isxcode.torch.modules.app.bot.BotChatContext;
import com.isxcode.torch.modules.chat.repository.ChatSessionRepository;
import com.isxcode.torch.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.torch.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.torch.modules.cluster.repository.ClusterNodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class Gemma3 extends Bot {

    private final ChatSessionRepository chatSessionRepository;

    private final ClusterNodeRepository clusterNodeRepository;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    private final HttpUrlUtils httpUrlUtils;

    public Gemma3(ChatSessionRepository chatSessionRepository, ClusterNodeRepository clusterNodeRepository,
        ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils, HttpUrlUtils httpUrlUtils) {
        this.chatSessionRepository = chatSessionRepository;
        this.clusterNodeRepository = clusterNodeRepository;
        this.clusterNodeMapper = clusterNodeMapper;
        this.aesUtils = aesUtils;
        this.httpUrlUtils = httpUrlUtils;
    }

    @Override
    public ChatResponse chat(BotChatContext botChatContext, SseEmitter sseEmitter) {

        // 随机一个集群id
        List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository
            .findAllByClusterIdAndStatus(botChatContext.getClusterConfig().getClusterId(), ClusterNodeStatus.RUNNING);
        if (allEngineNodes.isEmpty()) {
            throw new IsxAppException("申请资源失败 : 集群不存在可用节点，请切换一个集群 \n");
        }
        ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));

        // 翻译节点信息
        ScpFileEngineNodeDto scpFileEngineNodeDto =
            clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
        scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

        // 重新封装对应的请求
        ChatAgentAiReq chatAgentAiReq = ChatAgentAiReq.builder().topK(botChatContext.getBaseConfig().getTopK())
            .topP(botChatContext.getBaseConfig().getTopP()).maxTokens(botChatContext.getBaseConfig().getMaxTokens())
            .temperature(botChatContext.getBaseConfig().getTemperature())
            .repetitionPenalty(botChatContext.getBaseConfig().getRepetitionPenalty()).prompt(botChatContext.getPrompt())
            .messages(botChatContext.getChats()).aiPort(botChatContext.getAiPort()).build();

        // 封装请求
        BaseResponse<?> baseResponse = HttpUtils.doPost(
            httpUrlUtils.genHttpUrl(engineNode.getHost(), engineNode.getAgentPort(), AgentUrl.CHAT_AI_URL),
            chatAgentAiReq, BaseResponse.class);
        if (!String.valueOf(HttpStatus.OK.value()).equals(baseResponse.getCode())) {
            throw new IsxAppException(baseResponse.getMsg());
        }

        String content;
        if (baseResponse.getMsg().contains("Connection refused")) {
            content = "智能体已停用";
        } else {
            content = JSON.parseObject(JSON.toJSONString(baseResponse.getData()), ChatAgentAiRes.class).getResponse();
        }

        // 推送SSE消息 - 分块发送大内容
        try {

            sseEmitter.send(SseEmitter.event().name(ChatSseEvent.CHAT_EVENT)
                .data(JSON.toJSONString(SseBody.builder().chat(content).build())));

            sseEmitter.send(SseEmitter.event().name(ChatSseEvent.CHAT_EVENT)
                .data(JSON.toJSONString(SseBody.builder().chat("\n\n").build())));

            return ChatResponse.builder().content(content).build();
        } catch (Exception e) {
            throw new IsxAppException("对话异常");
        }
    }

    @Override
    public String name() {
        return ModelCode.GEMMA3_270M;
    }
}
