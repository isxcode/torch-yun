package com.isxcode.torch.modules.app.controller;

import com.isxcode.torch.api.app.req.*;
import com.isxcode.torch.api.app.res.AddAppRes;
import com.isxcode.torch.api.app.res.GetAppConfigRes;
import com.isxcode.torch.api.app.res.PageAppRes;
import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.common.annotations.successResponse.SuccessResponse;
import com.isxcode.torch.modules.app.service.AppBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "应用模块")
@RequestMapping(ModuleCode.APP)
@RestController
@RequiredArgsConstructor
public class AppController {

    private final AppBizService appBizService;

    @Operation(summary = "添加应用接口")
    @PostMapping("/addApp")
    @SuccessResponse("添加成功")
    public AddAppRes addApp(@Valid @RequestBody AddAppReq addAppReq) {

        return appBizService.addApp(addAppReq);
    }

    @Operation(summary = "更新应用接口")
    @PostMapping("/updateApp")
    @SuccessResponse("更新成功")
    public void updateApp(@Valid @RequestBody UpdateAppReq updateAppReq) {

        appBizService.updateApp(updateAppReq);
    }

    @Operation(summary = "配置应用接口")
    @PostMapping("/configApp")
    @SuccessResponse("配置成功")
    public void configApp(@Valid @RequestBody ConfigAppReq configAppReq) {

        appBizService.configApp(configAppReq);
    }

    @Operation(summary = "获取应用配置接口")
    @PostMapping("/getAppConfig")
    @SuccessResponse("获取成功")
    public GetAppConfigRes getAppConfig(@Valid @RequestBody GetAppConfigReq getAppConfigReq) {

        return appBizService.getAppConfig(getAppConfigReq);
    }

    @Operation(summary = "查询应用")
    @PostMapping("/pageApp")
    @SuccessResponse("查询成功")
    public Page<PageAppRes> pageApp(@Valid @RequestBody PageAppReq pageAppReq) {

        return appBizService.pageApp(pageAppReq);
    }

    @Operation(summary = "设置默认应用")
    @PostMapping("/setDefaultApp")
    @SuccessResponse("设置成功")
    public void setDefaultApp(@Valid @RequestBody SetDefaultAppReq setDefaultAppReq) {

        appBizService.setDefaultApp(setDefaultAppReq);
    }

    @Operation(summary = "删除应用接口")
    @PostMapping("/deleteApp")
    @SuccessResponse("设置成功")
    public void deleteApp(@Valid @RequestBody DeleteAppReq deleteAppReq) {

        appBizService.deleteApp(deleteAppReq);
    }

    @Operation(summary = "禁用应用接口")
    @PostMapping("/disableApp")
    @SuccessResponse("禁用成功")
    public void disableApp(@Valid @RequestBody DisableAppReq disableAppReq) {

        appBizService.disableApp(disableAppReq);
    }

    @Operation(summary = "启用应用接口")
    @PostMapping("/enableApp")
    @SuccessResponse("启用成功")
    public void enableApp(@Valid @RequestBody EnableAppReq enableAppReq) {

        appBizService.enableApp(enableAppReq);
    }
}
