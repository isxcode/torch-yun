package com.isxcode.torch.api.chat.ao;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ChatAo {

    private String sessionContent;

    private String chatId;

    private String appId;

    private LocalDateTime createDateTime;

    public ChatAo(String sessionContent, String chatId, String appId, LocalDateTime createDateTime) {
        this.sessionContent = sessionContent;
        this.chatId = chatId;
        this.appId = appId;
        this.createDateTime = createDateTime;
    }
}
