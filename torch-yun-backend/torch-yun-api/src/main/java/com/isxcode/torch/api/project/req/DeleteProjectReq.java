package com.isxcode.torch.api.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class DeleteProjectReq {

    @Schema(title = "项目唯一ID", example = "ty_123456789")
    @NotEmpty(message = "项目ID不能为空")
    private String id;
}
