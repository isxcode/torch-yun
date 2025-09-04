package com.isxcode.torch.modules.app.bot;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.app.dto.SseBody;
import com.isxcode.torch.api.chat.constants.ChatSseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
public abstract class Bot {

    /**
     * 智能体名称.
     */
    public abstract String name();

    /**
     * 对话方法.
     */
    public abstract void chat(BotChatContext botChatContext, SseEmitter sseEmitter);

    /**
     * 异步发送流式聊天.
     */
    @Async
    public void sendChat(BotChatContext botChatContext, SseEmitter sseEmitter) {

        try {
            // 推送开始事件
            sseEmitter.send(SseEmitter.event().name(ChatSseEvent.START_EVENT).data(JSON.toJSONString(SseBody.builder().msg("建立连接").build())));

            // 开始聊天
            chat(botChatContext, sseEmitter);
        } catch (Exception e) {

            log.error(e.getMessage(), e);
            try {
                sseEmitter.send(SseEmitter.event().name(ChatSseEvent.ERROR_EVENT).data(JSON.toJSONString(SseBody.builder().msg(e.getMessage()).build())));
                sseEmitter.completeWithError(e);
            } catch (Exception ignored) {
            }
        }

        // 设置连接关闭和超时的回调
        sseEmitter.onCompletion(() -> log.debug("SSE 聊天连接关闭"));
        sseEmitter.onTimeout(() -> log.error("流式聊天 SSE 连接超时"));
        sseEmitter.onError((ex) -> log.error("流式聊天 SSE 连接错误", ex));
    }
}
