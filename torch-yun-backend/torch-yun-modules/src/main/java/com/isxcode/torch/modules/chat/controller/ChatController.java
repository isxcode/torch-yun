package com.isxcode.torch.modules.chat.controller;

import com.isxcode.torch.api.chat.req.*;
import com.isxcode.torch.api.chat.res.*;
import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.common.annotations.successResponse.SuccessResponse;
import com.isxcode.torch.modules.chat.service.ChatBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.validation.Valid;

@Tag(name = "对话模块")
@RequestMapping(ModuleCode.CHAT)
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatBizService chatBizService;

    @Operation(summary = "获取最大的对话")
    @PostMapping("/getMaxChatId")
    @SuccessResponse("获取成功")
    public GetMaxChatIdRes getMaxChatId(@Valid @RequestBody GetMaxChatIdReq getMaxChatIdReq) {

        return chatBizService.getMaxChatId(getMaxChatIdReq);
    }

    @Operation(summary = "发送对话(SSE流式)")
    @PostMapping(value = "/sendChatStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendChatStream(@Valid @RequestBody SendChatReq sendChatReq) {

        return chatBizService.sendChatStream(sendChatReq);
    }

    @Operation(summary = "接受对话")
    @PostMapping("/getChat")
    @SuccessResponse("获取成功")
    public GetChatRes getChat(@Valid @RequestBody GetChatReq getChatReq) {

        return chatBizService.getChat(getChatReq);
    }

    @Operation(summary = "接受对话(SSE流式)")
    @PostMapping(value = "/getChatStream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getChatStream(@Valid @RequestBody GetChatReq getChatReq) {

        return chatBizService.getChatStream(getChatReq);
    }

    @Operation(summary = "搜索聊天历史")
    @PostMapping("/pageChatHistory")
    @SuccessResponse("查询成功")
    public Page<PageChatHistoryRes> pageChatHistory(@Valid @RequestBody PageChatHistoryReq pageChatHistoryReq) {

        return chatBizService.pageChatHistory(pageChatHistoryReq);
    }

    @Operation(summary = "获取完整对话")
    @PostMapping("/getFullChat")
    @SuccessResponse("获取成功")
    public GetFullChatRes getFullChat(@Valid @RequestBody GetFullChatReq getFullChatReq) {

        return chatBizService.getFullChat(getFullChatReq);
    }

    @Operation(summary = "停止思考")
    @PostMapping("/stopChat")
    @SuccessResponse("停止成功")
    public void stopChat(@Valid @RequestBody StopChatReq stopChatReq) {

        chatBizService.stopChat(stopChatReq);
    }
}
