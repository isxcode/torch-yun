package com.isxcode.torch.api.agent.req;

import com.isxcode.torch.api.chat.dto.ChatContent;
import lombok.Data;

import java.util.List;

@Data
public class ChatAgentAiContent {

    private List<ChatContent> messages;

    private Integer maxTokens;

    private Integer topK;

    private Double topP;

    private Float temperature;

    private Float repetitionPenalty;

    private String prompt;
}
