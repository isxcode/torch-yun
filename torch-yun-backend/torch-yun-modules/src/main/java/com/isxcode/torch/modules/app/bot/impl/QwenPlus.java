package com.isxcode.torch.modules.app.bot.impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.app.dto.BaseConfig;
import com.isxcode.torch.api.chat.constants.ChatSessionStatus;
import com.isxcode.torch.api.chat.dto.ChatContent;
import com.isxcode.torch.api.model.constant.ModelCode;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.app.bot.Bot;
import com.isxcode.torch.modules.app.bot.BotChatContext;
import com.isxcode.torch.modules.chat.entity.ChatSessionEntity;
import com.isxcode.torch.modules.chat.repository.ChatSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public void chat(BotChatContext botChatContext) {

        // 获取基础配置
        BaseConfig baseConfig =
            botChatContext.getBaseConfig() == null ? new BaseConfig() : botChatContext.getBaseConfig();

        // 重新封装对应的请求
        List<Message> messages = new ArrayList<>();
        botChatContext.getChats().forEach(chat -> {
            messages.add(Message.builder().role(chat.getRole()).content(chat.getContent()).build());
        });

        Generation gen = new Generation();

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

        try {
            GenerationResult call = gen.call(modelBuild.model("qwen-plus").messages(messages)
                .apiKey(botChatContext.getAuthConfig().getApiKey()).build());

            // 提交当前会话
            ChatSessionEntity nowChatSession = chatSessionRepository
                .findBySessionIndexAndChatId(botChatContext.getNowChatIndex(), botChatContext.getChatId()).get();
            nowChatSession.setStatus(ChatSessionStatus.OVER);
            ChatContent build = ChatContent.builder().content(call.getOutput().getText()).build();
            nowChatSession.setSessionContent(JSON.toJSONString(build));
            chatSessionRepository.save(nowChatSession);

        } catch (NoApiKeyException | InputRequiredException e) {
            log.error(e.getMessage(), e);
            throw new IsxAppException(e.getMessage());
        }
    }

    @Override
    public String name() {
        return ModelCode.QWEN_PLUS;
    }
}
