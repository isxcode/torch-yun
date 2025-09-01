package com.isxcode.torch.modules.app.bot;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public abstract class StreamBot extends Bot {

    /**
     * 流式对话接口.
     */
    public abstract void chatStream(BotChatContext botChatContext, SseEmitter sseEmitter);

    /**
     * 异步发送流式聊天.
     */
    @Async
    public void sendChatStream(BotChatContext botChatContext, SseEmitter sseEmitter) {
        chatStream(botChatContext, sseEmitter);
    }
}
