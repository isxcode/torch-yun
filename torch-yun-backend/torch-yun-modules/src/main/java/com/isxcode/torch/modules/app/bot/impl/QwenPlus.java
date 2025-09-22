package com.isxcode.torch.modules.app.bot.impl;

import com.isxcode.torch.api.agent.req.ChatAgentAiReq;
import com.isxcode.torch.api.app.dto.SseBody;
import com.isxcode.torch.api.chat.constants.ChatSseEvent;
import com.isxcode.torch.modules.app.bot.Bot;
import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.model.constant.ModelCode;
import com.isxcode.torch.modules.app.bot.BotChatContext;
import com.isxcode.torch.modules.chat.repository.ChatSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;

@Service
@Slf4j
public class QwenPlus extends Bot {

    private final ChatSessionRepository chatSessionRepository;

    public QwenPlus(ChatSessionRepository chatSessionRepository) {
        this.chatSessionRepository = chatSessionRepository;
    }

    @Override
    public void chat(BotChatContext botChatContext, SseEmitter sseEmitter) {

        // 封装对话请求体
        ChatAgentAiReq chatAgentAiReq = ChatAgentAiReq.builder().topK(botChatContext.getBaseConfig().getTopK())
            .topP(botChatContext.getBaseConfig().getTopP()).maxTokens(botChatContext.getBaseConfig().getMaxTokens())
            .temperature(botChatContext.getBaseConfig().getTemperature())
            .repetitionPenalty(botChatContext.getBaseConfig().getRepetitionPenalty()).prompt(botChatContext.getPrompt())
            .apiKey(botChatContext.getAuthConfig().getApiKey()).messages(botChatContext.getChats()).build();

        // base64压缩请求体
        String aiReq = Base64.getEncoder().encodeToString(JSON.toJSONString(chatAgentAiReq).getBytes());

        String pythonScriptPath = "/Users/ispong/isxcode/torch-yun/torch-yun-vip/torch-yun-plugins/tongyi/ai_local.py";

        // 提交到本地的python脚本中
        ProcessBuilder processBuilder = new ProcessBuilder("python3", "-u", pythonScriptPath, aiReq);

        try {
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    String decoded = new String(Base64.getDecoder().decode(line));
                    sseEmitter.send(SseEmitter.event().name(ChatSseEvent.CHAT_EVENT)
                        .data(JSON.toJSONString(SseBody.builder().chat(decoded).build())));
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new RuntimeException("Python脚本执行失败，退出码: " + exitCode);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("执行外部进程失败", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("进程被中断", e);
        }
    }

    @Override
    public String name() {
        return ModelCode.QWEN_PLUS;
    }
}
