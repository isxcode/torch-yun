package com.isxcode.torch.api.chat.res;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class SendChatRes {

    private String chatId;

    private String appId;

    private Integer responseIndexId;

    private String chatSessionId;
}
