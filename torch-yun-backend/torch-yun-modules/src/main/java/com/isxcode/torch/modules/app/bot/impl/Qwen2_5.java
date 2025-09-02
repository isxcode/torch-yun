package com.isxcode.torch.modules.app.bot.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isxcode.torch.api.agent.constants.AgentUrl;
import com.isxcode.torch.api.agent.req.ChatAgentAiReq;
import com.isxcode.torch.api.chat.constants.ChatSessionStatus;
import com.isxcode.torch.api.chat.dto.ChatContent;
import com.isxcode.torch.api.cluster.constants.ClusterNodeStatus;
import com.isxcode.torch.api.cluster.dto.ScpFileEngineNodeDto;
import com.isxcode.torch.api.model.constant.ModelCode;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.common.utils.aes.AesUtils;
import com.isxcode.torch.common.utils.http.HttpUrlUtils;
import com.isxcode.torch.common.utils.http.HttpUtils;
import com.isxcode.torch.modules.app.bot.Bot;
import com.isxcode.torch.modules.app.bot.BotChatContext;
import com.isxcode.torch.modules.chat.entity.ChatSessionEntity;
import com.isxcode.torch.modules.chat.repository.ChatSessionRepository;
import com.isxcode.torch.modules.cluster.entity.ClusterNodeEntity;
import com.isxcode.torch.modules.cluster.mapper.ClusterNodeMapper;
import com.isxcode.torch.modules.cluster.repository.ClusterNodeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Random;


@Service
@Slf4j
public class Qwen2_5 extends Bot {

    private final ChatSessionRepository chatSessionRepository;

    private final ClusterNodeRepository clusterNodeRepository;

    private final ClusterNodeMapper clusterNodeMapper;

    private final AesUtils aesUtils;

    private final HttpUrlUtils httpUrlUtils;

    public Qwen2_5(ChatSessionRepository chatSessionRepository, ClusterNodeRepository clusterNodeRepository,
        ClusterNodeMapper clusterNodeMapper, AesUtils aesUtils, HttpUrlUtils httpUrlUtils) {
        this.chatSessionRepository = chatSessionRepository;
        this.clusterNodeRepository = clusterNodeRepository;
        this.clusterNodeMapper = clusterNodeMapper;
        this.aesUtils = aesUtils;
        this.httpUrlUtils = httpUrlUtils;
    }

    @Override
    public void chatStream(BotChatContext botChatContext, SseEmitter sseEmitter) {

        try {
            // 随机一个集群id
            List<ClusterNodeEntity> allEngineNodes = clusterNodeRepository
                .findAllByClusterIdAndStatus(botChatContext.getClusterConfig().getClusterId(),
                    ClusterNodeStatus.RUNNING);
            if (allEngineNodes.isEmpty()) {
                throw new IsxAppException("申请资源失败 : 集群不存在可用节点，请切换一个集群 \n");
            }
            ClusterNodeEntity engineNode = allEngineNodes.get(new Random().nextInt(allEngineNodes.size()));

            // 翻译节点信息
            ScpFileEngineNodeDto scpFileEngineNodeDto =
                clusterNodeMapper.engineNodeEntityToScpFileEngineNodeDto(engineNode);
            scpFileEngineNodeDto.setPasswd(aesUtils.decrypt(scpFileEngineNodeDto.getPasswd()));

            // 封装请求
            ChatAgentAiReq chatAgentAiReq = new ChatAgentAiReq();
            chatAgentAiReq.setMessages(botChatContext.getChats());
            chatAgentAiReq.setAiPort(botChatContext.getAiPort());
            chatAgentAiReq.setPrompt(botChatContext.getPrompt());
            if (botChatContext.getBaseConfig() != null) {
                chatAgentAiReq.setMaxTokens(botChatContext.getBaseConfig().getMaxTokens());
                chatAgentAiReq.setTopK(botChatContext.getBaseConfig().getTopK());
                chatAgentAiReq.setTopP(botChatContext.getBaseConfig().getTopP());
                chatAgentAiReq.setTemperature(botChatContext.getBaseConfig().getTemperature());
                chatAgentAiReq.setRepetitionPenalty(botChatContext.getBaseConfig().getRepetitionPenalty());
            }

            // 获取当前会话实体
            ChatSessionEntity nowChatSession = chatSessionRepository
                .findBySessionIndexAndChatId(botChatContext.getNowChatIndex(), botChatContext.getChatId()).get();

            // 调用Agent的流式接口
            String streamUrl = httpUrlUtils.genHttpUrl(scpFileEngineNodeDto.getHost(), botChatContext.getAiPort(),
                AgentUrl.CHAT_AI_STREAM_URL);

            // 创建HTTP连接调用流式接口
            URL url = new URL(streamUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "text/event-stream");
            connection.setDoOutput(true);

            // 发送请求数据
            String jsonData = new ObjectMapper().writeValueAsString(chatAgentAiReq);
            connection.getOutputStream().write(jsonData.getBytes("UTF-8"));
            connection.getOutputStream().flush();

            // 读取流式响应并转发
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line;
            StringBuilder fullContent = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("event: delta")) {
                    // 读取下一行的data
                    String dataLine = reader.readLine();
                    if (dataLine != null && dataLine.startsWith("data: ")) {
                        String deltaContent = dataLine.substring(6); // 去掉"data: "前缀
                        fullContent.append(deltaContent);

                        // 转发delta事件
                        sseEmitter.send(SseEmitter.event().name("delta").data(deltaContent));
                    }
                } else if (line.startsWith("event: complete")) {
                    // 完成事件，保存最终内容
                    ChatContent chatContent = ChatContent.builder().content(fullContent.toString()).build();
                    nowChatSession.setSessionContent(JSON.toJSONString(chatContent));
                    nowChatSession.setStatus(ChatSessionStatus.OVER);
                    chatSessionRepository.save(nowChatSession);

                    // 转发完成事件
                    sseEmitter.send(SseEmitter.event().name("complete").data(""));
                    sseEmitter.complete();
                    break;
                } else if (line.startsWith("event: error")) {
                    // 错误事件
                    String dataLine = reader.readLine();
                    String errorMsg = dataLine != null && dataLine.startsWith("data: ") ?
                        dataLine.substring(6) : "未知错误";
                    throw new IsxAppException("AI流式响应错误: " + errorMsg);
                }
            }

            reader.close();
            connection.disconnect();

        } catch (Exception e) {
            log.error("Qwen2.5流式聊天失败", e);
            try {
                sseEmitter.send(SseEmitter.event().name("error").data(e.getMessage()));
                sseEmitter.completeWithError(e);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public String name() {
        return ModelCode.QWEN2_5;
    }
}
