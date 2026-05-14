package com.isxcode.torch.api.project.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.torch.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageProjectRes {

    private String id;

    private String name;

    private String workspace;

    private String assetsDir;

    private String designAppId;

    private String designAppName;

    private String planAppId;

    private String planAppName;

    private String developAppId;

    private String developAppName;

    private String remark;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDateTime;

    private String createBy;

    private String createByUsername;
}
