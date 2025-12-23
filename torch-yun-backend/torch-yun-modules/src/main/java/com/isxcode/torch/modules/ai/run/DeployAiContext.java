package com.isxcode.torch.modules.ai.run;

import com.isxcode.torch.api.ai.dto.ClusterConfig;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeployAiContext {

    private String aiId;

    private ClusterConfig clusterConfig;

    private String modelCode;

    private String modelFileId;

    private String deployScript;
}
