package com.isxcode.torch.modules.app.service;


import javax.transaction.Transactional;

import com.alibaba.fastjson.JSON;
import com.isxcode.torch.api.app.constants.AppStatus;
import com.isxcode.torch.api.app.constants.AppType;
import com.isxcode.torch.api.app.constants.DefaultAppStatus;
import com.isxcode.torch.api.app.dto.BaseConfig;
import com.isxcode.torch.api.app.req.*;
import com.isxcode.torch.api.app.res.AddAppRes;
import com.isxcode.torch.api.app.res.GetAppConfigRes;
import com.isxcode.torch.api.app.res.PageAppRes;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.ai.service.AiService;
import com.isxcode.torch.modules.app.entity.AppEntity;
import com.isxcode.torch.modules.app.mapper.AppMapper;
import com.isxcode.torch.modules.app.repository.AppRepository;
import com.isxcode.torch.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AppBizService {

    private final AppRepository appRepository;

    private final AppMapper appMapper;

    private final AppService appService;

    private final AiService aiService;

    private final UserService userService;

    public AddAppRes addApp(AddAppReq addAppReq) {

        Optional<AppEntity> appEntityByName = appRepository.findByName(addAppReq.getName());
        if (appEntityByName.isPresent()) {
            throw new IsxAppException("应用重复");
        }

        AppEntity appEntity = appMapper.addAppReqToAppEntity(addAppReq);
        appEntity.setStatus(AppStatus.ENABLE);
        appEntity.setDefaultApp(DefaultAppStatus.DISABLE);
        appEntity.setCheckDateTime(LocalDateTime.now());

        // 应用添加默认配置
        BaseConfig baseConfig = BaseConfig.builder().topK(50).topP(0.9).maxTokens(512).repetitionPenalty(1.2f)
            .enableSearch(false).temperature(0.8f).build();
        appEntity.setPrompt("");
        appEntity.setBaseConfig(JSON.toJSONString(baseConfig));
        appEntity.setAppType(AppType.TEXT_APP);

        appEntity = appRepository.save(appEntity);

        return appMapper.appEntityToAddAppRes(appEntity);
    }

    public void updateApp(UpdateAppReq updateAppReq) {

        // 检测名称重复
        AppEntity app = appService.getApp(updateAppReq.getId());

        Optional<AppEntity> appEntityByName = appRepository.findByName(updateAppReq.getName());
        if (appEntityByName.isPresent() && !appEntityByName.get().getId().equals(updateAppReq.getId())) {
            throw new IsxAppException("应用名称重复");
        }

        AppEntity appEntity = appMapper.updateAppReqToAppEntity(app, updateAppReq);
        appRepository.save(appEntity);
    }

    public void configApp(ConfigAppReq configAppReq) {

        AppEntity app = appService.getApp(configAppReq.getId());

        app.setPrompt(configAppReq.getPrompt());
        app.setBaseConfig(JSON.toJSONString(configAppReq.getBaseConfig()));
        app.setResources(JSON.toJSONString(configAppReq.getResources()));

        appRepository.save(app);
    }

    public Page<PageAppRes> pageApp(PageAppReq pageAppReq) {

        Page<AppEntity> appEntityPage = appRepository.searchAll(pageAppReq.getSearchKeyWord(),
            pageAppReq.getAppStatus(), PageRequest.of(pageAppReq.getPage(), pageAppReq.getPageSize()));

        Page<PageAppRes> result = appEntityPage.map(appMapper::appEntityToPageAppRes);
        result.forEach(appEntity -> {
            appEntity.setAiName(aiService.getAiName(appEntity.getAiId()));
            appEntity.setCreateByUsername(userService.getUserName(appEntity.getCreateBy()));
        });
        return result;
    }

    public GetAppConfigRes getAppConfig(GetAppConfigReq getAppConfigReq) {

        AppEntity app = appService.getApp(getAppConfigReq.getId());

        return GetAppConfigRes.builder().baseConfig(JSON.parseObject(app.getBaseConfig(), BaseConfig.class))
            .prompt(app.getPrompt()).resources(JSON.parseArray(app.getResources(), String.class)).build();
    }

    public void setDefaultApp(SetDefaultAppReq setDefaultAppReq) {

        AppEntity app = appService.getApp(setDefaultAppReq.getId());

        // 如果应用不可用，则不能设为默认应用
        if (AppStatus.DISABLE.equals(app.getStatus())) {
            throw new IsxAppException("禁用应用，无法设为默认应用");
        }

        List<AppEntity> allApp = appRepository.findAll();
        allApp.forEach(e -> e.setDefaultApp(DefaultAppStatus.DISABLE));
        appRepository.saveAll(allApp);

        // 再把当前改为ENABLE
        app.setDefaultApp(DefaultAppStatus.ENABLE);
        appRepository.save(app);
    }

    public void deleteApp(DeleteAppReq deleteAppReq) {

        AppEntity app = appService.getApp(deleteAppReq.getId());
        appRepository.delete(app);
    }

    public void enableApp(EnableAppReq enableAppReq) {

        AppEntity app = appService.getApp(enableAppReq.getId());

        app.setStatus(DefaultAppStatus.ENABLE);
        appRepository.save(app);
    }

    public void disableApp(DisableAppReq disableAppReq) {

        AppEntity app = appService.getApp(disableAppReq.getId());

        app.setStatus(DefaultAppStatus.DISABLE);
        appRepository.save(app);
    }
}
