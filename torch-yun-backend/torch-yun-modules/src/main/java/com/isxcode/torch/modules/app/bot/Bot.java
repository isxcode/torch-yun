package com.isxcode.torch.modules.app.bot;

import org.springframework.scheduling.annotation.Async;

public abstract class Bot {

    /**
     * 流对话.
     */
    public abstract void chat(BotChatContext botChatContext);

    /**
     * 智能体名称.
     */
    public abstract String name();

    @Async
    public void sendChat(BotChatContext botChatContext) {

        chat(botChatContext);
    }
}
