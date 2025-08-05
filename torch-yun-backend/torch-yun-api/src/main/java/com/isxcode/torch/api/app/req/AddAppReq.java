package com.isxcode.torch.api.app.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AddAppReq {

    @Schema(title = "ai名称", example = "ai")
    @NotEmpty(message = "ai名称不能为空")
    private String name;

    @Schema(title = "logo Id", example = "logo id")
    @NotEmpty(message = "logo Id不能为空")
    private String logoId;

    @Schema(title = "AI id", example = "123")
    @NotEmpty(message = "AI id不能为空")
    private String aiId;

    @Schema(title = "备注", example = "")
    private String remark;
}
