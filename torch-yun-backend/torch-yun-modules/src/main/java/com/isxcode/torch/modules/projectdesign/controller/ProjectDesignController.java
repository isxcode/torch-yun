package com.isxcode.torch.modules.projectdesign.controller;

import com.isxcode.torch.api.projectdesign.req.*;
import com.isxcode.torch.api.projectdesign.res.AddProjectDesignRes;
import com.isxcode.torch.api.projectdesign.res.PageProjectDesignRes;
import com.isxcode.torch.common.annotations.successResponse.SuccessResponse;
import com.isxcode.torch.modules.projectdesign.service.ProjectDesignBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "项目设计模块")
@RequestMapping("project-design")
@RestController
@RequiredArgsConstructor
public class ProjectDesignController {

    private final ProjectDesignBizService bizService;

    @Operation(summary = "新增项目设计")
    @PostMapping("/addProjectDesign")
    @SuccessResponse("新增成功")
    public AddProjectDesignRes addProjectDesign(@Valid @RequestBody AddProjectDesignReq req) {
        return bizService.addProjectDesign(req);
    }

    @Operation(summary = "更新项目设计")
    @PostMapping("/updateProjectDesign")
    @SuccessResponse("更新成功")
    public void updateProjectDesign(@Valid @RequestBody UpdateProjectDesignReq req) {
        bizService.updateProjectDesign(req);
    }

    @Operation(summary = "删除项目设计")
    @PostMapping("/deleteProjectDesign")
    @SuccessResponse("删除成功")
    public void deleteProjectDesign(@Valid @RequestBody DeleteProjectDesignReq req) {
        bizService.deleteProjectDesign(req);
    }

    @Operation(summary = "分页查询项目设计")
    @PostMapping("/pageProjectDesign")
    @SuccessResponse("查询成功")
    public Page<PageProjectDesignRes> pageProjectDesign(@Valid @RequestBody PageProjectDesignReq req) {
        return bizService.pageProjectDesign(req);
    }
}
