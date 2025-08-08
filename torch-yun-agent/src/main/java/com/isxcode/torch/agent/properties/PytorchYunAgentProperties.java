package com.isxcode.torch.agent.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "torch-yun")
@EnableConfigurationProperties(com.isxcode.torch.agent.properties.torchYunAgentProperties.class)
public class torchYunAgentProperties {

}
