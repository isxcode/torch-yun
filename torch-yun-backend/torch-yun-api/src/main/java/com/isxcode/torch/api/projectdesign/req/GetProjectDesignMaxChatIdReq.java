package com.isxcode.torch.api.projectdesign.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetProjectDesignMaxChatIdReq {

    @Schema(title = "项目ID", example = "ty_project_1")
    @NotEmpty(message = "项目ID不能为空")
    private String projectId;

    @Schema(title = "项目设计ID", example = "ty_design_1")
    @NotEmpty(message = "项目设计ID不能为空")
    private String projectDesignId;

    @Schema(title = "会话ID", example = "ty_chat_1")
    private String chatId;

    @Schema(title = "会话类型", example = "PROD")
    private String chatType;
}
