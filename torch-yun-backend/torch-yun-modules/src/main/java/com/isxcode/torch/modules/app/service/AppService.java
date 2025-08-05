package com.isxcode.torch.modules.app.service;

import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.app.entity.AppEntity;
import com.isxcode.torch.modules.app.repository.AppRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppService {

    private final AppRepository appRepository;

    public AppEntity getApp(String appId) {

        return appRepository.findById(appId).orElseThrow(() -> new IsxAppException("应用不存在"));
    }

    public String getAppName(String appId) {

        AppEntity appEntity = appRepository.findById(appId).orElse(null);
        return appEntity == null ? appId : appEntity.getName();
    }

}
