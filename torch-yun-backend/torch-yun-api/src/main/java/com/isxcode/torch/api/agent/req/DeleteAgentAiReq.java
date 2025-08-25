package com.isxcode.torch.api.agent.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteAgentAiReq {

    private String aiId;

    private String agentHomePath;
}
