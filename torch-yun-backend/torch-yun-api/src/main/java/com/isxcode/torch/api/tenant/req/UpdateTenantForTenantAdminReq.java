package com.isxcode.torch.api.tenant.req;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdateTenantForTenantAdminReq {

    @Schema(title = "租户唯一id", example = "ty_123456789")
    @NotEmpty(message = "租户id不能为空")
    private String id;

    @Schema(title = "租户简介", example = "超轻量级人工智能应用平台")
    private String introduce;
}
