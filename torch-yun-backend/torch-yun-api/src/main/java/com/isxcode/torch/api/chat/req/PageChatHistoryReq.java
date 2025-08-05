package com.isxcode.torch.api.chat.req;

import com.isxcode.torch.backend.api.base.pojos.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PageChatHistoryReq extends BasePageRequest {

    @Schema(title = "应用id", example = "如果应用id不为空，查询应用下面的聊天记录，如果应用为空，查询所有聊天记录")
    private String appId;
}
