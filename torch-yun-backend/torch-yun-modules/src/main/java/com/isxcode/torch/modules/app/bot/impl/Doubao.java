package com.isxcode.torch.modules.app.bot.impl;

import com.isxcode.torch.api.agent.req.ChatAgentAiReq;
import com.isxcode.torch.api.app.dto.ChatResponse;
import com.isxcode.torch.api.app.dto.SseBody;
import com.isxcode.torch.api.chat.constants.ChatSseEvent;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.app.bot.Bot;
import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.model.constant.ModelCode;
import com.isxcode.torch.modules.app.bot.BotChatContext;
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

    private final ResourceLoader resourceLoader;

    public Doubao(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public ChatResponse chat(BotChatContext botChatContext, SseEmitter sseEmitter) {

        // 封装对话请求体
        ChatAgentAiReq chatAgentAiReq = ChatAgentAiReq.builder().topK(botChatContext.getBaseConfig().getTopK())
            .topP(botChatContext.getBaseConfig().getTopP()).maxTokens(botChatContext.getBaseConfig().getMaxTokens())
            .temperature(botChatContext.getBaseConfig().getTemperature())
            .endpointId(botChatContext.getAuthConfig().getEndpointId())
            .repetitionPenalty(botChatContext.getBaseConfig().getRepetitionPenalty()).prompt(botChatContext.getPrompt())
            .apiKey(botChatContext.getAuthConfig().getApiKey()).messages(botChatContext.getChats()).build();

        // base64压缩请求体
        String aiReq = Base64.getEncoder().encodeToString(JSON.toJSONString(chatAgentAiReq).getBytes());

        Resource resource = resourceLoader.getResource("classpath:ai/doubao/ai.py");
        String pythonScript;
        try (InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            pythonScript = reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new IsxAppException("文件获取异常");
        }

        // 提交到本地的python脚本中
        ProcessBuilder processBuilder = new ProcessBuilder("python3", "-u", "-c", pythonScript, aiReq);

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

                return ChatResponse.builder().content(fullContent.toString()).build();
            }
        } catch (IOException | InterruptedException e) {
            throw new IsxAppException("对话异常");
        }
    }

    @Override
    public String name() {
        return ModelCode.DOUBAO;
    }
}
