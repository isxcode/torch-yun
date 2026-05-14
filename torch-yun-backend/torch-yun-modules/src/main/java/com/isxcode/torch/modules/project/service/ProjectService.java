package com.isxcode.torch.modules.project.service;

import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.project.entity.ProjectEntity;
import com.isxcode.torch.modules.project.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectEntity getProject(String projectId) {

        return projectRepository.findById(projectId).orElseThrow(() -> new IsxAppException("项目不存在"));
    }
}
