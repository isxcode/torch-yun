package com.isxcode.torch.api.agent.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeployAiRes {

    private String aiPort;

    private String aiPid;
}
