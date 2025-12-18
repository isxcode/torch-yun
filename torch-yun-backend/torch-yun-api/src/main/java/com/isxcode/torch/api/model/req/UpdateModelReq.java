package com.isxcode.torch.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateModelReq {

    @Schema(title = "模型id", example = "ty_123")
    @NotEmpty(message = "模型id不能为空")
    private String id;

    @Schema(title = "模型名称", example = "ty_123")
    @NotEmpty(message = "模型名称不能为空")
    private String name;

    @Schema(title = "模型id", example = "ty_123")
    @NotEmpty(message = "模型id不能为空")
    private String modelPlazaId;

    @Schema(title = "模型文件", example = "ty_123")
    @NotEmpty(message = "模型文件不能为空")
    private String modelFile;

    @Schema(title = "部署脚本", example = "ty_123")
    private String deployScript;

    @Schema(title = "备注", example = "备注")
    private String remark;
}
