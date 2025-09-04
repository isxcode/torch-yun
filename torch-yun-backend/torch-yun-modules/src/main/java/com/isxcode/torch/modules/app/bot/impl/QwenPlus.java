package com.isxcode.torch.modules.app.bot.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.isxcode.torch.api.app.dto.SseBody;
import com.isxcode.torch.api.chat.constants.ChatSseEvent;
import com.isxcode.torch.modules.app.bot.Bot;
import io.reactivex.Flowable;
import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.app.dto.BaseConfig;
import com.isxcode.torch.api.chat.constants.ChatSessionStatus;
import com.isxcode.torch.api.chat.dto.ChatContent;
import com.isxcode.torch.api.model.constant.ModelCode;
import com.isxcode.torch.modules.app.bot.BotChatContext;
import com.isxcode.torch.modules.chat.entity.ChatSessionEntity;
import com.isxcode.torch.modules.chat.repository.ChatSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class QwenPlus extends Bot {

    private final ChatSessionRepository chatSessionRepository;

    public QwenPlus(ChatSessionRepository chatSessionRepository) {
        this.chatSessionRepository = chatSessionRepository;
    }

    @Override
    public void chat(BotChatContext botChatContext, SseEmitter sseEmitter) {

        // 获取基础配置
        BaseConfig baseConfig =
            botChatContext.getBaseConfig() == null ? new BaseConfig() : botChatContext.getBaseConfig();

        // 重新封装对应的请求
        List<Message> messages = new ArrayList<>();
        botChatContext.getChats()
            .forEach(chat -> messages.add(Message.builder().role(chat.getRole()).content(chat.getContent()).build()));

        // 封装配置参数
        QwenParam.QwenParamBuilder<?, ?> modelBuild = QwenParam.builder();

        if (botChatContext.getPrompt() != null) {
            modelBuild = modelBuild.prompt(botChatContext.getPrompt());
        }

        if (baseConfig.getMaxTokens() != null) {
            modelBuild = modelBuild.maxTokens(baseConfig.getMaxTokens());
        }

        if (baseConfig.getSeed() != null) {
            modelBuild = modelBuild.seed(baseConfig.getSeed());
        }

        if (baseConfig.getTopK() != null) {
            modelBuild = modelBuild.topK(baseConfig.getTopK());
        }

        if (baseConfig.getTopP() != null) {
            modelBuild = modelBuild.topP(baseConfig.getTopP());
        }

        if (baseConfig.getTemperature() != null) {
            modelBuild = modelBuild.temperature(baseConfig.getTemperature());
        }

        if (baseConfig.getRepetitionPenalty() != null) {
            modelBuild = modelBuild.repetitionPenalty(baseConfig.getRepetitionPenalty());
        }

        if (baseConfig.getEnableSearch() != null) {
            modelBuild = modelBuild.enableSearch(baseConfig.getEnableSearch());
        }

        Generation gen = new Generation();
        try {
            // 启用流式响应
            modelBuild = modelBuild.incrementalOutput(true);

            // 获取当前会话实体
            ChatSessionEntity nowChatSession = chatSessionRepository
                .findBySessionIndexAndChatId(botChatContext.getNowChatIndex(), botChatContext.getChatId()).get();

            StringBuilder fullContent = new StringBuilder();

            // 调用流式API
            Flowable<GenerationResult> resultFlowable = gen.streamCall(modelBuild.model("qwen-plus").messages(messages)
                .apiKey(botChatContext.getAuthConfig().getApiKey()).build());

            // 遍历流式对话内容
            resultFlowable.blockingForEach(result -> {

                if (result.getOutput() != null && result.getOutput().getText() != null) {

                    // 获取聊天内容
                    String chatContent = result.getOutput().getText();
                    fullContent.append(chatContent);

                    // 发送聊天内容
                    try {
                        sseEmitter.send(SseEmitter.event().name(ChatSseEvent.CHAT_EVENT).data(JSON.toJSONString(SseBody.builder().chat(chatContent).build())));
                    } catch (Exception e) {
                        log.error("发送SSE事件失败", e);
                    }
                }
            });

            // 保存聊天对话状态和内容
            ChatContent chatContent = ChatContent.builder().content(fullContent.toString()).build();
            nowChatSession.setSessionContent(JSON.toJSONString(chatContent));
            nowChatSession.setStatus(ChatSessionStatus.OVER);
            chatSessionRepository.saveAndFlush(nowChatSession);

            // 发送完成事件
            try {
                sseEmitter.send(SseEmitter.event().name(ChatSseEvent.END_EVENT).data(JSON.toJSONString(SseBody.builder().msg("对话结束").build())));
                sseEmitter.complete();
            } catch (Exception e) {
                log.error("发送完成事件失败", e);
            }

        } catch (NoApiKeyException | InputRequiredException e) {

            log.error(e.getMessage(), e);
            try {
                sseEmitter.send(SseEmitter.event().name(ChatSseEvent.ERROR_EVENT).data(JSON.toJSONString(SseBody.builder().msg(e.getMessage()).build())));
                sseEmitter.completeWithError(e);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public String name() {
        return ModelCode.QWEN_PLUS;
    }
}
