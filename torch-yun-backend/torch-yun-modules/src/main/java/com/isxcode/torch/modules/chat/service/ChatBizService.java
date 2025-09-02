package com.isxcode.torch.modules.chat.service;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.ai.dto.ClusterConfig;
import com.isxcode.torch.api.app.constants.DefaultAppStatus;
import com.isxcode.torch.api.app.dto.BaseConfig;
import com.isxcode.torch.api.chat.ao.ChatAo;
import com.isxcode.torch.api.chat.constants.ChatSessionStatus;
import com.isxcode.torch.api.chat.constants.ChatSessionType;
import com.isxcode.torch.api.chat.constants.ChatType;
import com.isxcode.torch.api.chat.dto.ChatContent;
import com.isxcode.torch.api.chat.req.*;
import com.isxcode.torch.api.chat.res.*;
import com.isxcode.torch.api.model.constant.ModelType;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.ai.entity.AiEntity;
import com.isxcode.torch.modules.ai.service.AiService;
import com.isxcode.torch.modules.app.bot.Bot;
import com.isxcode.torch.modules.app.bot.BotChatContext;
import com.isxcode.torch.modules.app.bot.BotFactory;
import com.isxcode.torch.modules.app.entity.AppEntity;
import com.isxcode.torch.modules.app.repository.AppRepository;
import com.isxcode.torch.modules.app.service.AppService;
import com.isxcode.torch.modules.chat.entity.ChatEntity;
import com.isxcode.torch.modules.chat.entity.ChatSessionEntity;
import com.isxcode.torch.modules.chat.mapper.ChatMapper;
import com.isxcode.torch.modules.chat.repository.ChatRepository;
import com.isxcode.torch.modules.chat.repository.ChatSessionRepository;
import com.isxcode.torch.modules.model.entity.ModelEntity;
import com.isxcode.torch.modules.model.service.ModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;

import java.util.*;

import static com.isxcode.torch.common.config.CommonConfig.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatBizService {

    private final ChatRepository chatRepository;

    private final ChatSessionRepository chatSessionRepository;

    private final AppService appService;

    private final ChatService chatService;

    private final AiService aiService;

    private final BotFactory botFactory;

    private final ModelService modelService;

    private final ChatMapper chatMapper;

    private final AppRepository appRepository;

    public static final Map<String, Thread> CHAT_THREAD_MAP = new HashMap<>();

    public GetMaxChatIdRes getMaxChatId(GetMaxChatIdReq getMaxChatIdReq) {

        if (getMaxChatIdReq.getChatType() == null) {
            getMaxChatIdReq.setChatType(ChatType.PROD);
        }

        if (Strings.isEmpty(getMaxChatIdReq.getChatId())) {
            getMaxChatIdReq.setChatId(null);
        }

        if (Strings.isEmpty(getMaxChatIdReq.getAppId())) {
            Optional<AppEntity> byDefaultApp = appRepository.findByDefaultApp(DefaultAppStatus.ENABLE);
            if (!byDefaultApp.isPresent()) {
                throw new IsxAppException("请先创建智能体");
            }
            getMaxChatIdReq.setAppId(byDefaultApp.get().getId());
        }

        // 判断应用是否存在
        AppEntity app = appService.getApp(getMaxChatIdReq.getAppId());

        GetMaxChatIdRes getMaxChatIdRes = new GetMaxChatIdRes();

        // 如果chatId为空，则新建一个会话
        if (getMaxChatIdReq.getChatId() == null) {

            ChatEntity chat = new ChatEntity();
            chat.setAppId(app.getId());
            chat.setSubmitter(USER_ID.get());
            chat.setChatType(getMaxChatIdReq.getChatType());
            chat = chatRepository.save(chat);
            getMaxChatIdRes.setChatIndexId(0);
            getMaxChatIdRes.setChatId(chat.getId());
            getMaxChatIdRes.setAppId(getMaxChatIdReq.getAppId());
        } else {

            // 判断chat是否存在
            ChatEntity chat = chatService.getChat(getMaxChatIdReq.getChatId());

            Integer maxIndex =
                chatSessionRepository.getMaxIndex(getMaxChatIdReq.getChatId(), getMaxChatIdReq.getAppId());

            // 判断上一个会话是否结束
            if (maxIndex != 0) {
                ChatSessionEntity chatSessionEntity =
                    chatSessionRepository.findBySessionIndexAndChatId(maxIndex, getMaxChatIdReq.getChatId()).get();
                if (ChatSessionStatus.CHATTING.equals(chatSessionEntity.getStatus())) {
                    throw new IsxAppException("请稍后");
                }
            } else {
                maxIndex = maxIndex - 1;
            }

            getMaxChatIdRes.setChatIndexId(maxIndex + 1);
            getMaxChatIdRes.setChatId(chat.getId());
            getMaxChatIdRes.setAppId(getMaxChatIdReq.getAppId());
        }

        getMaxChatIdRes.setAppName(appService.getAppName(getMaxChatIdRes.getAppId()));

        return getMaxChatIdRes;
    }

    public GetChatRes getChat(GetChatReq getChatReq) {

        ChatSessionEntity chatSessionEntity =
            chatSessionRepository.findBySessionIndexAndChatId(getChatReq.getChatIndex(), getChatReq.getChatId())
                .orElseThrow(() -> new IsxAppException("当前会话不存在"));
        return GetChatRes.builder().status(chatSessionEntity.getStatus())
            .chatContent(JSON.parseObject(chatSessionEntity.getSessionContent(), ChatContent.class)).build();
    }

    public Page<PageChatHistoryRes> pageChatHistory(PageChatHistoryReq pageChatHistoryReq) {

        // 查询个人所有的聊天，时间倒序
        JPA_TENANT_MODE.set(false);
        Page<ChatAo> chatAoPage = chatSessionRepository.pageChatHistory(TENANT_ID.get(),
            pageChatHistoryReq.getSearchKeyWord(), pageChatHistoryReq.getAppId(), USER_ID.get(),
            PageRequest.of(pageChatHistoryReq.getPage(), pageChatHistoryReq.getPageSize()));
        JPA_TENANT_MODE.set(true);

        // 翻译
        Page<PageChatHistoryRes> map = chatAoPage.map(chatMapper::chatAoToPageChatHistoryRes);
        map.forEach(e -> {
            e.setAppName(appService.getAppName(e.getAppId()));
            e.setChatContent(JSON.parseObject(e.getSessionContent(), ChatContent.class));
        });
        return map;
    }

    public GetFullChatRes getFullChat(GetFullChatReq getFullChatReq) {

        // 判断会话是否存在
        ChatEntity chat = chatService.getChat(getFullChatReq.getChatId(), USER_ID.get());

        // 封装对话
        List<ChatSessionEntity> chatSessionList = chatSessionRepository.findAllByChatId(chat.getId());
        List<ChatContent> chatSessions = new ArrayList<>();
        chatSessionList.forEach(e -> {
            ChatContent chatContent = JSON.parseObject(e.getSessionContent(), ChatContent.class);
            chatSessions.add(ChatContent.builder().content(chatContent.getContent()).role(e.getSessionType())
                .index(e.getSessionIndex()).build());
        });

        return GetFullChatRes.builder().appName(appService.getAppName(chat.getAppId())).chatSessions(chatSessions)
            .build();
    }

    public void stopChat(StopChatReq stopChatReq) {

        // 杀死进程
        Thread thread = CHAT_THREAD_MAP.get(stopChatReq.getChatSessionId());
        try {
            thread.interrupt();
        } catch (Exception ignored) {

        }

        Optional<ChatSessionEntity> sessionRepositoryById =
            chatSessionRepository.findById(stopChatReq.getChatSessionId());
        if (sessionRepositoryById.isPresent()) {
            ChatSessionEntity chatSessionEntity = sessionRepositoryById.get();
            chatSessionEntity.setSessionContent(JSON.toJSONString(ChatContent.builder().content("已停止思考").build()));
            chatSessionEntity.setStatus(ChatSessionStatus.OVER);
            chatSessionRepository.save(chatSessionEntity);
        }
    }

    public SseEmitter sendChatStream(SendChatReq sendChatReq) {

        // 创建 SSE 连接，设置超时时间为 30 分钟
        SseEmitter sseEmitter = new SseEmitter(30 * 60 * 1000L);

        try {
            // 判断应用是否存在
            AppEntity app = appService.getApp(sendChatReq.getAppId());

            // 判断会话是否存在
            ChatEntity chat = chatService.getChat(sendChatReq.getChatId());

            // 判断会话index是否存在
            if (chatSessionRepository.existsBySessionIndexAndChatId(sendChatReq.getMaxChatIndexId(),
                sendChatReq.getChatId())) {
                throw new IsxAppException("当前index已存在");
            }

            // 判断上一个会话是否结束
            if (sendChatReq.getMaxChatIndexId() != 0) {
                ChatSessionEntity chatSession = chatSessionRepository
                    .findBySessionIndexAndChatId(sendChatReq.getMaxChatIndexId() - 1, sendChatReq.getChatId())
                    .orElseThrow(() -> new IsxAppException("请等待上一个会话结束"));
                if (ChatSessionStatus.CHATTING.equals(chatSession.getStatus())) {
                    throw new IsxAppException("等待会话结束");
                }
            }

            // 初始化当前会话
            ChatSessionEntity chatSession = new ChatSessionEntity();
            chatSession.setSessionType(ChatSessionType.USER);
            chatSession.setStatus(ChatSessionStatus.OVER);
            chatSession.setAppId(sendChatReq.getAppId());
            chatSession.setChatId(sendChatReq.getChatId());
            chatSession.setSessionIndex(sendChatReq.getMaxChatIndexId());
            chatSession.setSessionContent(JSON.toJSONString(sendChatReq.getChatContent()));

            // 获取上下文
            List<ChatSessionEntity> chatSessionList = chatSessionRepository.findAllByChatId(chat.getId());
            chatSessionList.add(chatSession);

            // 获取机器人id
            AiEntity ai = aiService.getAi(app.getAiId());
            String modelId = ai.getModelId();
            JPA_TENANT_MODE.set(false);
            ModelEntity model = modelService.getModel(modelId);
            JPA_TENANT_MODE.set(true);

            // 封装会话请求体
            BotChatContext botChatContext = chatService.transSessionListToBotChatContext(chatSessionList, app, ai,
                sendChatReq.getMaxChatIndexId(), sendChatReq.getChatId());
            if (!Strings.isEmpty(app.getBaseConfig())) {
                botChatContext.setBaseConfig(JSON.parseObject(app.getBaseConfig(), BaseConfig.class));
            }
            if (ModelType.MANUAL.equals(model.getModelType())) {
                botChatContext.setAiPort(ai.getAiPort());
                botChatContext.setClusterConfig(JSON.parseObject(ai.getClusterConfig(), ClusterConfig.class));
                botChatContext.setBaseConfig(JSON.parseObject(app.getBaseConfig(), BaseConfig.class));
                botChatContext.setPrompt(app.getPrompt());
            }

            // 保存请求会话
            chatSessionRepository.save(chatSession);

            // 初始化当前会话
            ChatSessionEntity nowChatSession = new ChatSessionEntity();
            nowChatSession.setSessionType(ChatSessionType.ASSISTANT);
            nowChatSession.setStatus(ChatSessionStatus.CHATTING);
            nowChatSession.setAppId(sendChatReq.getAppId());
            nowChatSession.setChatId(sendChatReq.getChatId());
            nowChatSession.setSessionIndex(sendChatReq.getMaxChatIndexId() + 1);
            nowChatSession.setSessionContent("{}");
            chatSessionRepository.saveAndFlush(nowChatSession);

            // 发送开始事件
            SendChatRes response = SendChatRes.builder().chatId(sendChatReq.getChatId())
                .responseIndexId(sendChatReq.getMaxChatIndexId() + 1).chatSessionId(chatSession.getId())
                .appId(sendChatReq.getAppId()).build();

            sseEmitter.send(SseEmitter.event().name("start").data(JSON.toJSONString(response)));

            // 异步发送流式请求
            new Thread(() -> {
                try {
                    // 记录当前线程
                    CHAT_THREAD_MAP.put(chatSession.getId(), Thread.currentThread());

                    // 发送流式请求
                    Bot bot = botFactory.getBot(model.getCode());
                    bot.sendChatStream(botChatContext, sseEmitter);

                    // 删除当前线程
                    CHAT_THREAD_MAP.remove(chatSession.getId());

                } catch (Exception e) {
                    log.error("流式聊天异常", e);
                    try {
                        sseEmitter.send(SseEmitter.event().name("error").data(e.getMessage()));
                        sseEmitter.completeWithError(e);
                    } catch (Exception ignored) {
                    }
                    CHAT_THREAD_MAP.remove(chatSession.getId());
                }
            }).start();

        } catch (Exception e) {
            log.error("发送流式聊天异常", e);
            try {
                sseEmitter.send(SseEmitter.event().name("error").data(e.getMessage()));
                sseEmitter.completeWithError(e);
            } catch (Exception ignored) {
            }
        }

        // 设置连接关闭和超时的回调
        sseEmitter.onCompletion(() -> log.info("流式聊天 SSE 连接完成"));
        sseEmitter.onTimeout(() -> log.warn("流式聊天 SSE 连接超时"));
        sseEmitter.onError((ex) -> log.error("流式聊天 SSE 连接错误", ex));

        return sseEmitter;
    }
}
