package com.isxcode.torch.modules.app.bot.impl;

import com.isxcode.torch.api.agent.req.ChatAgentAiReq;
import com.isxcode.torch.api.app.dto.SseBody;
import com.isxcode.torch.api.chat.constants.ChatSessionStatus;
import com.isxcode.torch.api.chat.constants.ChatSseEvent;
import com.isxcode.torch.api.chat.dto.ChatContent;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.app.bot.Bot;
import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.model.constant.ModelCode;
import com.isxcode.torch.modules.app.bot.BotChatContext;
import com.isxcode.torch.modules.chat.entity.ChatSessionEntity;
import com.isxcode.torch.modules.chat.repository.ChatSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.stream.Collectors;

@Service
@Slf4j
public class Doubao extends Bot {

    private final ChatSessionRepository chatSessionRepository;

    private final ResourceLoader resourceLoader;

    public Doubao(ChatSessionRepository chatSessionRepository, ResourceLoader resourceLoader) {
        this.chatSessionRepository = chatSessionRepository;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void chat(BotChatContext botChatContext, SseEmitter sseEmitter) {

        // 封装对话请求体
        ChatAgentAiReq chatAgentAiReq = ChatAgentAiReq.builder().topK(botChatContext.getBaseConfig().getTopK())
            .topP(botChatContext.getBaseConfig().getTopP()).maxTokens(botChatContext.getBaseConfig().getMaxTokens())
            .temperature(botChatContext.getBaseConfig().getTemperature())
            .endpointId(botChatContext.getAuthConfig().getEndpointId())
            .repetitionPenalty(botChatContext.getBaseConfig().getRepetitionPenalty()).prompt(botChatContext.getPrompt())
            .apiKey(botChatContext.getAuthConfig().getApiKey()).messages(botChatContext.getChats()).build();

        // base64压缩请求体
        String aiReq = Base64.getEncoder().encodeToString(JSON.toJSONString(chatAgentAiReq).getBytes());

        Resource resource = resourceLoader.getResource("classpath:ai/deepseek/ai.py");
        String pythonScript;
        try (InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            pythonScript = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new IsxAppException("文件获取异常");
        }

        // 提交到本地的python脚本中
        ProcessBuilder processBuilder = new ProcessBuilder("python3", "-u", "-c", pythonScript, aiReq);

        // 获取当前会话实体
        ChatSessionEntity nowChatSession = chatSessionRepository
            .findBySessionIndexAndChatId(botChatContext.getNowChatIndex(), botChatContext.getChatId()).get();

        StringBuilder fullContent = new StringBuilder();

        try {
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String decoded = new String(Base64.getDecoder().decode(line));
                    fullContent.append(decoded);
                    sseEmitter.send(SseEmitter.event().name(ChatSseEvent.CHAT_EVENT)
                        .data(JSON.toJSONString(SseBody.builder().chat(decoded).build())));
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new RuntimeException("Python脚本执行失败，退出码: " + exitCode);
                }

                // 保存聊天对话状态和内容
                ChatContent chatContent = ChatContent.builder().content(fullContent.toString()).build();
                nowChatSession.setSessionContent(JSON.toJSONString(chatContent));
                nowChatSession.setStatus(ChatSessionStatus.OVER);
                chatSessionRepository.saveAndFlush(nowChatSession);

                // 发送完成事件
                try {
                    sseEmitter.send(SseEmitter.event().name(ChatSseEvent.END_EVENT)
                        .data(JSON.toJSONString(SseBody.builder().msg("对话结束").build())));
                    sseEmitter.complete();
                } catch (Exception e) {
                    log.error("发送完成事件失败", e);
                }
            }
        } catch (IOException | InterruptedException e) {

            log.error(e.getMessage(), e);
            try {
                sseEmitter.send(SseEmitter.event().name(ChatSseEvent.ERROR_EVENT)
                    .data(JSON.toJSONString(SseBody.builder().msg(e.getMessage()).build())));
                sseEmitter.completeWithError(e);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public String name() {
        return ModelCode.DOUBAO;
    }
}
