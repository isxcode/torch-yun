package com.isxcode.torch.api.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatContent {

    private String content;

    private String threadId;

    private Integer index;

    private String role;
}
