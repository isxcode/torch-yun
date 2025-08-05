package com.isxcode.torch.api.app.req;

import com.isxcode.torch.api.ai.dto.AuthConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateAppReq {

    @Schema(title = "数据源唯一id", example = "py_123456789")
    @NotEmpty(message = "数据源id不能为空")
    private String id;

    @Schema(title = "ai名称", example = "ai")
    @NotEmpty(message = "ai名称不能为空")
    private String name;

    @Schema(title = "备注", example = "")
    private String remark;

    @Schema(title = "模型id", example = "123")
    @NotEmpty(message = "模型id不能为空")
    private String modelId;

    @Schema(title = "Api认证", example = "")
    private AuthConfig authConfig;
}
