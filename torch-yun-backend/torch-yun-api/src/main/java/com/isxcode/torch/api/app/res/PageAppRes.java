package com.isxcode.torch.api.app.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.torch.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageAppRes {

    private String id;

    private String name;

    private String logoId;

    private String aiId;

    private String aiName;

    private String remark;

    private String status;

    private String defaultApp;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDateTime;

    private String createBy;

    private String createByUsername;
}
