package com.isxcode.torch.api.ai.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.torch.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageAiRes {

    private String id;

    private String name;

    private String remark;

    private String modelId;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime checkDateTime;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDateTime;

    private String createBy;

    private String modelName;

    private String clusterName;

    private String clusterConfig;

    private String aiType;

    private String status;

    private String createByUsername;
}
