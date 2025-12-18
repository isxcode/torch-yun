package com.isxcode.torch.api.model.plaza.req;

import com.isxcode.torch.backend.api.base.pojos.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageModelReq extends BasePageRequest {

    private String orgName;

    private String isOnline;
}
