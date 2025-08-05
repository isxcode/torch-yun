package com.isxcode.torch.api.chat.res;

import com.isxcode.torch.api.chat.dto.ChatContent;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class GetChatRes {

    private ChatContent chatContent;

    private String status;
}
