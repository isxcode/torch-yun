package com.isxcode.torch.modules.projectdesign.service;

import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.projectdesign.entity.ProjectDesignEntity;
import com.isxcode.torch.modules.projectdesign.repository.ProjectDesignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectDesignService {

    private final ProjectDesignRepository repository;

    public ProjectDesignEntity getProjectDesign(String id) {
        return repository.findById(id).orElseThrow(() -> new IsxAppException("项目设计不存在"));
    }
}
