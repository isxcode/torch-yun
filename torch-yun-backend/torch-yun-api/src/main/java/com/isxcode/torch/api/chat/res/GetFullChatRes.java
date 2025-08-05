package com.isxcode.torch.api.chat.res;

import com.isxcode.torch.api.chat.dto.ChatContent;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetFullChatRes {

    private String appName;

    private List<ChatContent> chatSessions;
}
