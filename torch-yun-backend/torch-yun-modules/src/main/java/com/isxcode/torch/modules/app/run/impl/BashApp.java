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
public class BashApp extends App {

    private final BotFactory botFactory;

    private final ChatSessionRepository chatSessionRepository;

    public BashApp(BotFactory botFactory, ChatSessionRepository chatSessionRepository,
        ChatSessionRepository chatSessionRepository1) {

        super(chatSessionRepository);
        this.botFactory = botFactory;
        this.chatSessionRepository = chatSessionRepository1;
    }

    @Override
    public String appType() {
        return AppType.BASH_APP;
    }

    @Override
    public void start(BotChatContext botChatContext, SseEmitter sseEmitter) {

        String textTemplate = "%s";

        // 重新封装message
        ChatSessionEntity userAskSession = chatSessionRepository.findById(botChatContext.getUserAskSessionId()).get();
        String submitContent = String.format(textTemplate,
            JSON.parseObject(userAskSession.getSessionContent(), ChatContent.class).getContent());
        userAskSession
            .setSubmitContent(JSON.toJSONString(ChatContent.builder().content(submitContent).role("user").build()));
        chatSessionRepository.save(userAskSession);

        // 添加用户提问
        botChatContext.getChats().add(ChatContent.builder().content(submitContent).role("user").build());

        Integer exitCode = 0;
        int[] runningCode = new int[] {1, 2, 3, 4, 5};

        // 直到成功为止
        while (runningCode.contain(exitCode)) {

            // exitCode 1 写思路
            if (exitCode == 1) {
                return getExplain1();
            }

            // exitCode 2 写bash脚本
            if (exitCode == 2) {
                return getExplain2();
            }

            // exitCode 3 检测bash脚本
            if (exitCode == 3) {
                return getExplain3();
            }

            // exitCode 4 运行bash脚本
            if (exitCode == 4) {
                return getExplain4();
            }

            // exitCode 5 成功接收数据
            if (exitCode == 5) {
                return getExplain5();
            }

            // exitCode 6 markdown更好的展示结果
            if (exitCode == 6) {
                return getExplain5();
            }
        }

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
