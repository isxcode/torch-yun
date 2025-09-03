package com.isxcode.torch.api.chat.constants;

/**
 * 聊天事件关键词.
 */
public interface ChatSseEvent {

    /**
     * 开始事件.
     */
    String START_EVENT = "start";

    /**
     * 聊天事件.
     */
    String CHAT_EVENT = "chat";

    /**
     * 结束事件.
     */
    String END_EVENT = "end";

    /**
     * 异常事件.
     */
    String ERROR_EVENT = "error";
}
