package com.isxcode.torch.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteModelReq {

    @Schema(title = "模型id", example = "ty_123")
    @NotEmpty(message = "模型id不能为空")
    private String id;
}
