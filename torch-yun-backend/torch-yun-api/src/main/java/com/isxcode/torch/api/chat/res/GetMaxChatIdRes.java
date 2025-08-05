
package com.isxcode.torch.api.chat.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMaxChatIdRes {

    private Integer chatIndexId;

    private String appId;

    private String chatId;

    private String appName;
}
