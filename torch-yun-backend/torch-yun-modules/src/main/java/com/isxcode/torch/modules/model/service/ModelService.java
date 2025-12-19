package com.isxcode.torch.modules.model.service;

import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.model.entity.ModelEntity;
import com.isxcode.torch.modules.model.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModelService {

    private final ModelRepository modelRepository;

    private final ResourceLoader resourceLoader;

    public ModelEntity getModel(String modelId) {

        return modelRepository.findById(modelId).orElseThrow(() -> new IsxAppException("模型不存在"));
    }

    public String getModelName(String modelId) {

        ModelEntity model = modelRepository.findById(modelId).orElse(null);
        return model == null ? modelId : model.getName();
    }

    public String getDeployScript(String modelCode) {

        String aiPyPath = "";
        if ("Qwen/Qwen2.5-0.5B".equals(modelCode)) {
            aiPyPath = "qwen2.5";
        } else if ("Google/gemma-3-270m".equals(modelCode)) {
            aiPyPath = "gemma3";
        } else {
            aiPyPath = "basic";
        }

        Resource resource = resourceLoader.getResource("classpath:ai/" + aiPyPath + "/ai.py");
        try (InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new IsxAppException("安装包部署异常");
        }
    }
}
