package com.isxcode.torch.api.instance.res;

import lombok.Data;


@Data
public class GetWorkInstanceValuePathRes {

    private String jsonPath;

    private String value;

    private String copyValue;
}
