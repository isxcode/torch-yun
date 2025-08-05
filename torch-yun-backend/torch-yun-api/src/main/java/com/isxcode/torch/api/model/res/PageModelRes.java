package com.isxcode.torch.api.model.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.torch.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageModelRes {

    private String id;

    private String name;

    private String code;

    private String modelType;

    private String modelLabel;

    private String modelFile;

    private String modelFileName;

    private String status;

    private String remark;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDateTime;

    private String createBy;
}
