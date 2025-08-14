package com.isxcode.torch.api.agent.req;

import com.isxcode.torch.api.chat.dto.ChatContent;
import lombok.Data;

import java.util.List;

@Data
public class ChatAgentAiContent {

    private List<ChatContent> messages;
}
