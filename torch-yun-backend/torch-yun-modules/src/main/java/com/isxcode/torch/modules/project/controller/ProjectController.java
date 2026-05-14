package com.isxcode.torch.modules.project.controller;

import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.api.project.req.AddProjectReq;
import com.isxcode.torch.api.project.req.DeleteProjectReq;
import com.isxcode.torch.api.project.req.PageProjectReq;
import com.isxcode.torch.api.project.req.UpdateProjectReq;
import com.isxcode.torch.api.project.res.AddProjectRes;
import com.isxcode.torch.api.project.res.PageProjectRes;
import com.isxcode.torch.common.annotations.successResponse.SuccessResponse;
import com.isxcode.torch.modules.project.service.ProjectBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "项目管理模块")
@RequestMapping(ModuleCode.PROJECT)
@RestController
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectBizService projectBizService;

    @Operation(summary = "新增项目")
    @PostMapping("/addProject")
    @SuccessResponse("新增成功")
    public AddProjectRes addProject(@Valid @RequestBody AddProjectReq addProjectReq) {

        return projectBizService.addProject(addProjectReq);
    }

    @Operation(summary = "更新项目")
    @PostMapping("/updateProject")
    @SuccessResponse("更新成功")
    public void updateProject(@Valid @RequestBody UpdateProjectReq updateProjectReq) {

        projectBizService.updateProject(updateProjectReq);
    }

    @Operation(summary = "删除项目")
    @PostMapping("/deleteProject")
    @SuccessResponse("删除成功")
    public void deleteProject(@Valid @RequestBody DeleteProjectReq deleteProjectReq) {

        projectBizService.deleteProject(deleteProjectReq);
    }

    @Operation(summary = "分页查询项目")
    @PostMapping("/pageProject")
    @SuccessResponse("查询成功")
    public Page<PageProjectRes> pageProject(@Valid @RequestBody PageProjectReq pageProjectReq) {

        return projectBizService.pageProject(pageProjectReq);
    }
}
