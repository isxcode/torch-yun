package com.isxcode.torch.modules.model.plaza.controller;

import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.api.model.plaza.req.PageModelReq;
import com.isxcode.torch.api.model.plaza.res.PageModelRes;
import com.isxcode.torch.common.annotations.successResponse.SuccessResponse;
import com.isxcode.torch.modules.model.plaza.service.biz.ModePlazaBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Tag(name = "模型广场模块")
@RequestMapping(ModuleCode.MODEL_PLAZA)
@RestController
@RequiredArgsConstructor
public class ModelPlazaController {

    private final ModePlazaBizService modePlazaBizService;

    @Operation(summary = "查询模型仓库接口")
    @PostMapping("/pageModel")
    @SuccessResponse("查询成功")
    public Page<PageModelRes> pageModel(@Valid @RequestBody PageModelReq pageModelReq) {

        return modePlazaBizService.pageModel(pageModelReq);
    }
}
