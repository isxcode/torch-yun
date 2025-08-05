package com.isxcode.torch.api.chat.res;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.isxcode.torch.api.chat.dto.ChatContent;
import com.isxcode.torch.backend.api.base.serializer.LocalDateTimeSerializer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PageChatHistoryRes {

    private String chatId;

    private String appId;

    private String appName;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDateTime;

    private String sessionContent;

    private ChatContent chatContent;
}
