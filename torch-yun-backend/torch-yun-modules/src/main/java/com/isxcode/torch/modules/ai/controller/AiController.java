package com.isxcode.torch.modules.ai.controller;

import com.isxcode.torch.api.ai.req.*;
import com.isxcode.torch.api.ai.res.CheckAiRes;
import com.isxcode.torch.api.ai.res.GetAiLogRes;
import com.isxcode.torch.api.ai.res.PageAiRes;
import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.api.user.constants.RoleType;
import com.isxcode.torch.common.annotations.successResponse.SuccessResponse;
import com.isxcode.torch.modules.ai.service.AiBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI模块")
@RequestMapping(ModuleCode.AI)
@RestController
@RequiredArgsConstructor
public class AiController {

    private final AiBizService aiBizService;

    @Secured({RoleType.TENANT_ADMIN})
    @Operation(summary = "添加AI接口")
    @PostMapping("/addAi")
    @SuccessResponse("添加成功")
    public void addAi(@Valid @RequestBody AddAiReq addAiReq) {

        aiBizService.addAi(addAiReq);
    }

    @Secured({RoleType.TENANT_ADMIN})
    @Operation(summary = "更新AI接口")
    @PostMapping("/updateAi")
    @SuccessResponse("更新成功")
    public void updateAi(@Valid @RequestBody UpdateAiReq updateAiReq) {

        aiBizService.updateAi(updateAiReq);
    }

    @Secured({RoleType.TENANT_MEMBER, RoleType.TENANT_ADMIN})
    @Operation(summary = "查询AI")
    @PostMapping("/pageAi")
    @SuccessResponse("查询成功")
    public Page<PageAiRes> pageAi(@Valid @RequestBody PageAiReq pageAiReq) {

        return aiBizService.pageAi(pageAiReq);
    }

    @Secured({RoleType.TENANT_MEMBER, RoleType.TENANT_ADMIN})
    @Operation(summary = "部署智能体接口")
    @PostMapping("/deployAi")
    @SuccessResponse("部署中")
    public void deployAi(@Valid @RequestBody DeployAiReq deployAiReq) {

        aiBizService.deployAi(deployAiReq);
    }

    @Secured({RoleType.TENANT_MEMBER, RoleType.TENANT_ADMIN})
    @Operation(summary = "停止智能体接口")
    @PostMapping("/stopAi")
    @SuccessResponse("下线成功")
    public void stopAi(@Valid @RequestBody StopAiReq stopAiReq) {

        aiBizService.stopAi(stopAiReq);
    }

    @Secured({RoleType.TENANT_MEMBER})
    @Operation(summary = "获取智能体运行日志接口")
    @PostMapping("/getAiLog")
    @SuccessResponse("获取成功")
    public GetAiLogRes getAiLog(@Valid @RequestBody GetAiLogReq getAiLogReq) {

        return aiBizService.getAiLog(getAiLogReq);
    }

    @Secured({RoleType.TENANT_MEMBER})
    @Operation(summary = "获取智能体部署日志接口")
    @PostMapping("/getAiDeployLog")
    @SuccessResponse("获取成功")
    public GetAiLogRes getAiDeployLog(@Valid @RequestBody GetAiLogReq getAiLogReq) {

        return aiBizService.getAiDeployLog(getAiLogReq);
    }

    @Secured({RoleType.TENANT_MEMBER, RoleType.TENANT_ADMIN})
    @Operation(summary = "检测智能体接口")
    @PostMapping("/checkAi")
    @SuccessResponse("检测完成")
    public CheckAiRes checkAi(@Valid @RequestBody CheckAiReq checkAiReq) {

        return aiBizService.checkAi(checkAiReq);
    }

    @Secured({RoleType.TENANT_ADMIN})
    @Operation(summary = "删除智能体接口")
    @PostMapping("/deleteAi")
    @SuccessResponse("删除成功")
    public void deleteAi(@Valid @RequestBody DeleteAiReq deleteAiReq) {

        aiBizService.deleteAi(deleteAiReq);
    }
}
