package com.isxcode.torch.api.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SseBody {

    /**
     * 对话内容.
     */
    private String chat;

    /**
     * 异常消息.
     */
    private String msg;
}
