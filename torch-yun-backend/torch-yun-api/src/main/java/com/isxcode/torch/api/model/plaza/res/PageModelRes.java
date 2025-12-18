package com.isxcode.torch.api.model.plaza.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageModelRes {

    private String id;

    private String orgName;

    private String modelName;

    private String isOnline;

    private String label;

    private String modelType;

    private String modelParam;

    private String remark;
}
