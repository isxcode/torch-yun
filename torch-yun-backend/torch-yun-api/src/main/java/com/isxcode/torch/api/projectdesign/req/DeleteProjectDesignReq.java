package com.isxcode.torch.api.projectdesign.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteProjectDesignReq {

    @Schema(title = "ID", example = "ty_123")
    @NotEmpty(message = "ID不能为空")
    private String id;
}
