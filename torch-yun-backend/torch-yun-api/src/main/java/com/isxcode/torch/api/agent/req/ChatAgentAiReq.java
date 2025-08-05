package com.isxcode.torch.api.agent.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatAgentAiReq {

    private String prompt;

    private String aiPort;
}
