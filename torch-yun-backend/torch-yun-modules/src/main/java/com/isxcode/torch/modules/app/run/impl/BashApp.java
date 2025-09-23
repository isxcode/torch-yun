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

import java.util.Arrays;
import java.util.List;


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

        Integer runCode = 1;
        List<Integer> runningCode = Arrays.asList(1, 2, 3, 4);

        Bot bot = botFactory.getBot(botChatContext.getModelCode());

        ChatResponse[] chatResponseHolder = new ChatResponse[1];

        // 直到成功为止
        while (runningCode.contains(runCode)) {

            // exitCode 1 写思路
            if (runCode == 1) {
                runCode = chatWay1(botChatContext, bot, sseEmitter, chatResponseHolder);
                continue;
            }

            // exitCode 2 写bash脚本
            if (runCode == 2) {
                runCode = chatWay2(botChatContext, bot, sseEmitter, chatResponseHolder);
                continue;
            }

            // // exitCode 3 检测bash脚本
            // if (runCode == 3) {
            // runCode = chatWay3(botChatContext, bot, sseEmitter, chatResponse);
            // continue;
            // }
            //
            // // exitCode 4 运行bash脚本
            // if (runCode == 4) {
            // runCode = chatWay4(botChatContext, bot, sseEmitter, chatResponse);
            // }

            // // exitCode 5 成功接收数据
            // if (exitCode == 5) {
            // return getExplain5();
            // }
            //
            // // exitCode 6 markdown更好的展示结果
            // if (exitCode == 6) {
            // return getExplain5();
            // }
        }

        // 找到对应的ai体


        // 获取当前会话实体
        ChatSessionEntity nowChatSession = chatSessionRepository
            .findBySessionIndexAndChatId(botChatContext.getNowChatIndex(), botChatContext.getChatId()).get();

        // 保存聊天对话状态和内容
        ChatContent chatContent = ChatContent.builder().content(chatResponseHolder[0].getContent()).build();
        nowChatSession.setSessionContent(JSON.toJSONString(chatContent));
        nowChatSession.setStatus(ChatSessionStatus.OVER);
        chatSessionRepository.saveAndFlush(nowChatSession);
    }

    public Integer chatWay1(BotChatContext botChatContext, Bot bot, SseEmitter sseEmitter,
        ChatResponse[] chatResponseHolder) {


        String content = "帮我写个脚本，获取服务器的内存，";
        String textTemplate = "%s，返回思路即可，且思路内容精简清晰，只返回一个思路即可";
        String format = String.format(textTemplate, content);

        // 添加用户提问
        botChatContext.getChats().add(ChatContent.builder().content(format).role("user").build());

        // 提交给ai分析
        chatResponseHolder[0] = bot.sendChat(botChatContext, sseEmitter);

        return 2;
    }

    public Integer chatWay2(BotChatContext botChatContext, Bot bot, SseEmitter sseEmitter,
        ChatResponse[] chatResponseHolder) {

        // 获取之前的结果
        botChatContext.getChats()
            .add(ChatContent.builder().content(chatResponseHolder[0].getContent()).role("assistant").build());

        // 封装新的提交
        String content = "帮我写个脚本，获取服务器的内存，";
        String textTemplate = "%s，根据上述思路，返回可执行的Bash脚本即可，只返回脚本内容，不要以markdown的形式内容返回";
        String format = String.format(textTemplate, content);

        // 添加新的提交
        botChatContext.getChats().add(ChatContent.builder().content(format).role("user").build());

        // 开始对话
        chatResponseHolder[0] = bot.sendChat(botChatContext, sseEmitter);
        return 0;
    }

    public Integer chatWay3(BotChatContext botChatContext, Bot bot, SseEmitter sseEmitter, ChatResponse chatResponse) {

        // chatResponse = bot.sendChat(botChatContext, sseEmitter);

        chatResponse.setContent("3");
        return 4;
    }

    public Integer chatWay4(BotChatContext botChatContext, Bot bot, SseEmitter sseEmitter, ChatResponse chatResponse) {

        // chatResponse = bot.sendChat(botChatContext, sseEmitter);

        chatResponse.setContent("4");
        return 0;
    }
}
