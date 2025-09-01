package com.isxcode.torch.api.agent.req;

import com.isxcode.torch.api.chat.dto.ChatContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatAgentAiReq {

    private List<ChatContent> messages;

    private String aiPort;

    private Integer maxTokens;

    private Integer topK;

    private Double topP;

    private Float temperature;

    private Float repetitionPenalty;

    private String prompt;
}
