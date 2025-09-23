package com.isxcode.torch.api.app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatResponse {

    private String content;
}
