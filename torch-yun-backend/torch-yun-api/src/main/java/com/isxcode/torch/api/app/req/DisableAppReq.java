package com.isxcode.torch.api.app.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DisableAppReq {

    @Schema(title = "应用唯一id", example = "py_123456789")
    @NotEmpty(message = "应用id不能为空")
    private String id;
}
