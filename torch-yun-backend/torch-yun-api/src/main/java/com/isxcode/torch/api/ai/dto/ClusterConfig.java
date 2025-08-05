package com.isxcode.torch.api.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClusterConfig {

    @Schema(title = "集群id", example = "ai")
    private String clusterId;
}
