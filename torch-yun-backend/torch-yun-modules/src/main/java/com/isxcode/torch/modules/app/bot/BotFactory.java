package com.isxcode.torch.modules.app.bot;

import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class BotFactory {

    private final ApplicationContext applicationContext;

    public Bot getBot(String modelCode) {

        return applicationContext.getBeansOfType(Bot.class).values().stream()
            .filter(bot -> bot.name().equals(modelCode)).findFirst().orElseThrow(() -> new IsxAppException("模型不支持"));
    }
}
