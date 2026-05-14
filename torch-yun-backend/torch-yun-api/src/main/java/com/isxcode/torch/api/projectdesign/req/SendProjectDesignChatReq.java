package com.isxcode.torch.api.projectdesign.req;

import com.isxcode.torch.api.chat.dto.ChatContent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SendProjectDesignChatReq {

    @Schema(title = "项目ID", example = "ty_project_1")
    @NotEmpty(message = "项目ID不能为空")
    private String projectId;

    @Schema(title = "会话ID", example = "ty_chat_1")
    @NotEmpty(message = "会话ID不能为空")
    private String chatId;

    @Schema(title = "最大indexId", example = "0")
    @NotNull(message = "最大indexId不能为空")
    private Integer maxChatIndexId;

    @Schema(title = "内容")
    @NotNull(message = "内容不能为空")
    private ChatContent chatContent;
}
