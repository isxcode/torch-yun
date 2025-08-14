package com.isxcode.torch.api.agent.req;

import com.isxcode.torch.api.chat.dto.ChatContent;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatAgentAiReq {

    private List<ChatContent> messages;

    private String aiPort;
}
