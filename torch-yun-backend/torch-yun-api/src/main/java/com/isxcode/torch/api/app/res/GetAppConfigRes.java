package com.isxcode.torch.api.app.res;

import com.isxcode.torch.api.app.dto.BaseConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetAppConfigRes {

    private List<String> resources;

    private BaseConfig baseConfig;

    private String prompt;
}
