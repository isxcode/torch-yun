package com.isxcode.torch.modules.app.bot;

import com.isxcode.torch.api.app.dto.ChatResponse;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
public abstract class Bot {

    /**
     * 智能体名称.
     */
    public abstract List<String> name();

    /**
     * 对话方法.
     */
    public abstract ChatResponse chat(BotChatContext botChatContext, SseEmitter sseEmitter);

    /**
     * 同步发送流式聊天.
     */
    public ChatResponse sendChat(BotChatContext botChatContext, SseEmitter sseEmitter) {

        try {
            return chat(botChatContext, sseEmitter);
        } catch (Exception e) {
            throw new IsxAppException("对话异常");
        }
    }
}
