package com.isxcode.torch.modules.ai.service;

import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.ai.entity.AiEntity;
import com.isxcode.torch.modules.ai.repository.AiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiService {

    private final AiRepository aiRepository;

    public AiEntity getAi(String aiId) {

        return aiRepository.findById(aiId).orElseThrow(() -> new IsxAppException("ai不存在"));
    }

    public String getAiName(String aiId) {

        AiEntity aiEntity = aiRepository.findById(aiId).orElse(null);
        return aiEntity == null ? aiId : aiEntity.getName();
    }
}
