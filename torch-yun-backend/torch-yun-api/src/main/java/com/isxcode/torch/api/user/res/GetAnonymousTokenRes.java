package com.isxcode.torch.api.user.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetAnonymousTokenRes {

    private String token;
}
