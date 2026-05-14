package com.isxcode.torch.api.projectdesign.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UpdateProjectDesignReq {

    @Schema(title = "ID", example = "ty_123")
    @NotEmpty(message = "ID不能为空")
    private String id;

    @Schema(title = "项目ID", example = "ty_project_1")
    @NotEmpty(message = "项目ID不能为空")
    private String projectId;

    @Schema(title = "名称", example = "系统架构设计")
    @NotEmpty(message = "名称不能为空")
    private String name;

    @Schema(title = "备注", example = "")
    private String remark;
}
