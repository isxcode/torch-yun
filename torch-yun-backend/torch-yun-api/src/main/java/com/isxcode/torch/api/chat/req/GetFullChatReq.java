package com.isxcode.torch.api.chat.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetFullChatReq {

    @Schema(title = "会话id", example = "123")
    @NotEmpty(message = "会话id不能为空")
    private String chatId;
}
