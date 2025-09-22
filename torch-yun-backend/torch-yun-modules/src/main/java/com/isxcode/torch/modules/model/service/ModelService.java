package com.isxcode.torch.modules.model.service;

import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.model.entity.ModelEntity;
import com.isxcode.torch.modules.model.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModelService {

    private final ModelRepository modelRepository;

    public ModelEntity getModel(String modelId) {

        return modelRepository.findById(modelId).orElseThrow(() -> new IsxAppException("模型不存在"));
    }

    public String getModelName(String modelId) {

        ModelEntity model = modelRepository.findById(modelId).orElse(null);
        return model == null ? modelId : model.getName();
    }

    public String getModelType(String modelId) {

        ModelEntity model = modelRepository.findById(modelId).orElse(null);
        return model == null ? modelId : model.getModelType();
    }
}
