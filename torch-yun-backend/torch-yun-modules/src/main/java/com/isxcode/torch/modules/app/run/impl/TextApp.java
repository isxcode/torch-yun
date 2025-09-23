package com.isxcode.torch.modules.app.run.impl;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.app.constants.AppType;
import com.isxcode.torch.api.app.dto.ChatResponse;
import com.isxcode.torch.api.chat.constants.ChatSessionStatus;
import com.isxcode.torch.api.chat.dto.ChatContent;
import com.isxcode.torch.modules.app.bot.Bot;
import com.isxcode.torch.modules.app.bot.BotChatContext;
import com.isxcode.torch.modules.app.bot.BotFactory;
import com.isxcode.torch.modules.app.run.App;
import com.isxcode.torch.modules.chat.entity.ChatSessionEntity;
import com.isxcode.torch.modules.chat.repository.ChatSessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Service
@Slf4j
public class TextApp extends App {

    private final BotFactory botFactory;

    private final ChatSessionRepository chatSessionRepository;

    public TextApp(BotFactory botFactory, ChatSessionRepository chatSessionRepository,
        ChatSessionRepository chatSessionRepository1) {

        super(chatSessionRepository);
        this.botFactory = botFactory;
        this.chatSessionRepository = chatSessionRepository1;
    }

    @Override
    public String appType() {
        return AppType.TEXT_APP;
    }

    @Override
    public void start(BotChatContext botChatContext, SseEmitter sseEmitter) {

        // 找到对应的ai体
        Bot bot = botFactory.getBot(botChatContext.getModelCode());
        ChatResponse chatResponse = bot.sendChat(botChatContext, sseEmitter);

        // 获取当前会话实体
        ChatSessionEntity nowChatSession = chatSessionRepository
            .findBySessionIndexAndChatId(botChatContext.getNowChatIndex(), botChatContext.getChatId()).get();

        // 保存聊天对话状态和内容
        ChatContent chatContent = ChatContent.builder().content(chatResponse.getContent()).build();
        nowChatSession.setSessionContent(JSON.toJSONString(chatContent));
        nowChatSession.setStatus(ChatSessionStatus.OVER);
        chatSessionRepository.saveAndFlush(nowChatSession);
    }
}
