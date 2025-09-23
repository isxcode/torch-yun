package com.isxcode.torch.modules.app.run;

import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AppFactory {

    private final ApplicationContext applicationContext;

    public App getApp(String appType) {

        return applicationContext.getBeansOfType(App.class).values().stream()
            .filter(bot -> bot.appType().equals(appType)).findFirst().orElseThrow(() -> new IsxAppException("应用不支持"));
    }
}
