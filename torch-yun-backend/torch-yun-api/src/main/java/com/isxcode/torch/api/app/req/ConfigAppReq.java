package com.isxcode.torch.api.app.req;

import com.isxcode.torch.api.app.dto.BaseConfig;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ConfigAppReq {

    @Schema(title = "应用唯一id", example = "py_123456789")
    @NotEmpty(message = "应用id不能为空")
    private String id;

    private List<String> resources;

    private BaseConfig baseConfig;

    private String prompt;
}
