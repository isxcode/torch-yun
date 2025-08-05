package com.isxcode.torch.api.app.req;

import com.isxcode.torch.backend.api.base.pojos.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageAppReq extends BasePageRequest {

    @Schema(description = "应用状态", example = "启用ENABLE/禁用DISABLE")
    private String appStatus;
}
