package com.isxcode.torch.api.alarm.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.torch.api.alarm.dto.MessageConfig;
import com.isxcode.torch.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageMessageRes {

    private String id;

    private String name;

    private String remark;

    private String msgType;

    private String status;

    private String response;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDateTime;

    private String createBy;

    private String createByUsername;

    private String msgConfig;

    private MessageConfig messageConfig;
}
