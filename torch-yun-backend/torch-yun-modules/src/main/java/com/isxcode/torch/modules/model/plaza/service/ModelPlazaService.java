package com.isxcode.torch.modules.model.plaza.service;

import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.model.plaza.entity.ModelPlazaEntity;
import com.isxcode.torch.modules.model.plaza.repository.ModelPlazaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ModelPlazaService {

    private final ModelPlazaRepository modelPlazaRepository;

    public ModelPlazaEntity getModelPlaza(String modelPlazaId) {
        return modelPlazaRepository.findById(modelPlazaId).orElseThrow(() -> new IsxAppException("模型不存在"));
    }

}
