package com.isxcode.torch.api.chat.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class GetMaxChatIdReq {

    @Schema(title = "会话id", example = "123")
    private String chatId;

    @Schema(title = "应用id", example = "")
    private String appId;

    @Schema(title = "会话类型", example = "测试(TEST)还是真实使用(PROD)")
    private String chatType;
}
