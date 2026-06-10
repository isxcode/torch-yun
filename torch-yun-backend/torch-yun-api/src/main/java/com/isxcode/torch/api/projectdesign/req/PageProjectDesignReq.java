package com.isxcode.torch.api.projectdesign.req;

import com.isxcode.torch.backend.api.base.pojos.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageProjectDesignReq extends BasePageRequest {

    @NotEmpty(message = "项目ID不能为空")
    private String projectId;
}
