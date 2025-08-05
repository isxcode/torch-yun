package com.isxcode.torch.api.agent.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAgentAiLogReq {

    private String agentHomePath;

    private String aiId;
}
