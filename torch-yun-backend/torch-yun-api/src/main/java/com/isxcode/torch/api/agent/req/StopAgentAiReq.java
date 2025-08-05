package com.isxcode.torch.api.agent.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StopAgentAiReq {

    private String pid;

    private String aiId;
}
