package com.isxcode.torch.api.chat.req;

import com.isxcode.torch.api.chat.dto.ChatContent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SendChatReq {

    @Schema(title = "会话id", example = "123")
    private String chatId;

    @Schema(title = "应用id", example = "")
    @NotEmpty(message = "应用id不能为空")
    private String appId;

    @Schema(title = "最大indexId", example = "")
    private Integer maxChatIndexId;

    @Schema(title = "内容", example = "")
    private ChatContent chatContent;
}
