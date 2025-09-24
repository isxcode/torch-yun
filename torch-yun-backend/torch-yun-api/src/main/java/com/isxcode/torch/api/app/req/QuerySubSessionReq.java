package com.isxcode.torch.api.app.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class QuerySubSessionReq {

    @Schema(title = "会话唯一id", example = "ty_123456789")
    @NotEmpty(message = "应用id不能为空")
    private String chatSessionId;
}
