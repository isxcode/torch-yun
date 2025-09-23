package com.isxcode.torch.modules.chat.service;

import com.alibaba.fastjson2.JSON;
import com.isxcode.torch.api.ai.dto.AuthConfig;
import com.isxcode.torch.api.ai.dto.ClusterConfig;
import com.isxcode.torch.api.app.dto.BaseConfig;
import com.isxcode.torch.api.chat.dto.ChatContent;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.ai.entity.AiEntity;
import com.isxcode.torch.modules.app.bot.BotChatContext;
import com.isxcode.torch.modules.app.entity.AppEntity;
import com.isxcode.torch.modules.chat.entity.ChatEntity;
import com.isxcode.torch.modules.chat.entity.ChatSessionEntity;
import com.isxcode.torch.modules.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatEntity getChat(String chatId) {

        return chatRepository.findById(chatId).orElseThrow(() -> new IsxAppException("会话不存在"));
    }

    public ChatEntity getChat(String chatId, String userId) {

        return chatRepository.findByIdAndSubmitter(chatId, userId).orElseThrow(() -> new IsxAppException("会话不存在"));
    }

    public BotChatContext transSessionListToBotChatContext(List<ChatSessionEntity> chatSessionList, AppEntity app,
        AiEntity ai, Integer nowIndex, String chatId, String modelCode) {

        List<ChatContent> chatContents = new ArrayList<>();
        chatSessionList.forEach(session -> {
            ChatContent chatContent = JSON.parseObject(session.getSessionContent(), ChatContent.class);
            chatContent.setIndex(session.getSessionIndex());
            chatContent.setRole(session.getSessionType());
            chatContents.add(chatContent);
        });


        return BotChatContext.builder().chatId(chatId).chats(chatContents).nowChatIndex(nowIndex + 1)
            .baseConfig(JSON.parseObject(app.getBaseConfig(), BaseConfig.class)).prompt(app.getPrompt())
            .authConfig(JSON.parseObject(ai.getAuthConfig(), AuthConfig.class)).modelCode(modelCode)
            .aiPort(ai.getAiPort()).clusterConfig(JSON.parseObject(ai.getClusterConfig(), ClusterConfig.class)).build();
    }
}
