package com.isxcode.torch.api.project.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class AddProjectReq {

    @Schema(title = "项目名称", example = "示例项目")
    @NotEmpty(message = "项目名称不能为空")
    private String name;

    @Schema(title = "工作空间", example = "/workspace/demo")
    @NotEmpty(message = "工作空间不能为空")
    private String workspace;

    @Schema(title = "资产目录", example = "spec")
    @NotEmpty(message = "资产目录不能为空")
    private String assetsDir;

    @Schema(title = "项目设计模型应用ID", example = "app_1")
    @NotEmpty(message = "项目设计模型应用不能为空")
    private String designAppId;

    @Schema(title = "项目计划模型应用ID", example = "app_2")
    @NotEmpty(message = "项目计划模型应用不能为空")
    private String planAppId;

    @Schema(title = "项目开发模型应用ID", example = "app_3")
    @NotEmpty(message = "项目开发模型应用不能为空")
    private String developAppId;

    @Schema(title = "备注", example = "")
    private String remark;
}
