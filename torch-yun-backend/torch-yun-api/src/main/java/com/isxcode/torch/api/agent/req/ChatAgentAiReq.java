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

    private Integer maxTokens;

    private String modelId;

    private Integer topK;

    private Double topP;

    private Float temperature;

    private Float repetitionPenalty;

    private String prompt;

    private String apiKey;

    private String endpointId;
}
