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
public class AuthConfig {

    @Schema(title = "api的key", example = "ai")
    private String apiKey;

    /**
     * 豆包的endpointId。moduleId.
     */
    private String endpointId;
}
