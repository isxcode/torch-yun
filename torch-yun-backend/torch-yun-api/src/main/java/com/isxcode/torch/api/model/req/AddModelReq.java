package com.isxcode.torch.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AddModelReq {

    @Schema(title = "模型名称", example = "ty_123")
    @NotEmpty(message = "模型名称不能为空")
    private String name;

    @Schema(title = "模型编码", example = "例如：Qwen2.5-0.5B")
    @NotEmpty(message = "模型编码不能为空")
    private String code;

    @Schema(title = "模型标签", example = "0.5b")
    private String modelLabel;

    @Schema(title = "模型文件", example = "ty_123")
    @NotEmpty(message = "模型文件不能为空")
    private String modelFile;

    @Schema(title = "备注", example = "备注")
    private String remark;
}
