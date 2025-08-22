package com.isxcode.torch.api.ai.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CheckAiReq {

    @Schema(title = "ai的id", example = "ty_123456789")
    @NotEmpty(message = "智能体id不能为空")
    private String id;
}
