package com.isxcode.torch.modules.app.run;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.app.dto.SseBody;
import com.isxcode.torch.api.chat.constants.ChatSseEvent;
import com.isxcode.torch.api.chat.dto.ChatContent;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.app.bot.BotChatContext;
import com.isxcode.torch.modules.chat.entity.ChatSubSessionEntity;
import com.isxcode.torch.modules.chat.repository.ChatSessionRepository;
import com.isxcode.torch.modules.chat.repository.ChatSubSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static com.isxcode.torch.common.config.CommonConfig.TENANT_ID;
import static com.isxcode.torch.common.config.CommonConfig.USER_ID;

@Slf4j
@RequiredArgsConstructor
public abstract class App {

    private final ChatSessionRepository chatSessionRepository;

    private final ChatSubSessionRepository chatSubSessionRepository;

    /**
     * 应用类型.
     */
    public abstract String appType();

    /**
     * 对话方法.
     */
    public abstract void start(BotChatContext botChatContext, SseEmitter sseEmitter);

    /**
     * 异步发送流式聊天.
     */
    @Async
    public void startAiChat(BotChatContext botChatContext, SseEmitter sseEmitter) {

        USER_ID.set(botChatContext.getUserId());
        TENANT_ID.set(botChatContext.getTenantId());

        try {
            // 对话开始事件
            sseEmitter.send(SseEmitter.event().name(ChatSseEvent.START_EVENT)
                .data(JSON.toJSONString(SseBody.builder().msg("建立连接").build())));

            // 开始应用聊天
            start(botChatContext, sseEmitter);

            // 对话结束事件
            sseEmitter.send(SseEmitter.event().name(ChatSseEvent.END_EVENT)
                .data(JSON.toJSONString(SseBody.builder().msg("对话结束").build())));
            sseEmitter.complete();

        } catch (IsxAppException e2) {

            try {
                sseEmitter.send(SseEmitter.event().name(ChatSseEvent.ERROR_EVENT)
                    .data(JSON.toJSONString(SseBody.builder().msg(e2.getMsg()).build())));
                sseEmitter.completeWithError(e2);
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                sseEmitter.send(SseEmitter.event().name(ChatSseEvent.ERROR_EVENT)
                    .data(JSON.toJSONString(SseBody.builder().msg(e.getMessage()).build())));
                sseEmitter.completeWithError(e);
            } catch (Exception ignored) {
            }
        }

        // 设置连接关闭和超时的回调
        sseEmitter.onCompletion(() -> log.debug("SSE 聊天连接关闭"));
        sseEmitter.onTimeout(() -> log.error("流式聊天 SSE 连接超时"));
        sseEmitter.onError((ex) -> log.error("流式聊天 SSE 连接错误", ex));
    }

    public void saveUserChatContent(String content, BotChatContext botChatContext, String sessionTye,
        Integer[] subSessionIndexHolder) {

        ChatContent userChat = ChatContent.builder().content(content).role("user").build();
        botChatContext.getChats().add(userChat);

        // 把对话保存到subSession中
        ChatSubSessionEntity userChatSubSession = new ChatSubSessionEntity();
        userChatSubSession.setSessionContent(JSON.toJSONString(userChat));
        userChatSubSession.setSessionId(botChatContext.getAiSessionId());
        userChatSubSession.setSessionRole("user");
        userChatSubSession.setStatus("OVER");
        userChatSubSession.setSessionType(sessionTye);
        userChatSubSession.setSessionIndex(subSessionIndexHolder[0]);
        subSessionIndexHolder[0] = subSessionIndexHolder[0] + 1;
        chatSubSessionRepository.save(userChatSubSession);
    }

    public void saveAiChatContent(String content, BotChatContext botChatContext, String sessionTye,
        Integer[] subSessionIndexHolder) {

        ChatContent aiChat = ChatContent.builder().content(content).role("assistant").build();
        botChatContext.getChats().add(aiChat);

        // 把对话保存到subSession中
        ChatSubSessionEntity aiChatSubSession = new ChatSubSessionEntity();
        aiChatSubSession.setSessionContent(content);
        aiChatSubSession.setSessionId(botChatContext.getAiSessionId());
        aiChatSubSession.setSessionRole("assistant");
        aiChatSubSession.setStatus("OVER");
        aiChatSubSession.setSessionType(sessionTye);
        aiChatSubSession.setSessionIndex(subSessionIndexHolder[0]);
        subSessionIndexHolder[0] = subSessionIndexHolder[0] + 1;
        chatSubSessionRepository.save(aiChatSubSession);
    }
}
