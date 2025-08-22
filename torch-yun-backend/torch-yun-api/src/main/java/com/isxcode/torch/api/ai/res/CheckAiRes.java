package com.isxcode.torch.api.ai.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckAiRes {

    private String status;
    
    private String message;
}
