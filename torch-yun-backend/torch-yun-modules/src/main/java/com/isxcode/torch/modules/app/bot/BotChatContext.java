package com.isxcode.torch.modules.app.bot;

import com.isxcode.torch.api.ai.dto.AuthConfig;
import com.isxcode.torch.api.ai.dto.ClusterConfig;
import com.isxcode.torch.api.app.dto.BaseConfig;
import com.isxcode.torch.api.chat.dto.ChatContent;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class BotChatContext {

    private String prompt;

    private ClusterConfig clusterConfig;

    private String aiPort;

    private String modelCode;

    private AuthConfig authConfig;

    private Integer nowChatIndex;

    private String chatId;

    private List<ChatContent> chats;

    private BaseConfig baseConfig;

    private String userAskSessionId;

    private String aiSessionId;

    private String userId;

    private String tenantId;
}
