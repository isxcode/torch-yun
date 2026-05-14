package com.isxcode.torch.modules.project.service;

import com.isxcode.torch.api.project.req.AddProjectReq;
import com.isxcode.torch.api.project.req.DeleteProjectReq;
import com.isxcode.torch.api.project.req.PageProjectReq;
import com.isxcode.torch.api.project.req.UpdateProjectReq;
import com.isxcode.torch.api.project.res.AddProjectRes;
import com.isxcode.torch.api.project.res.PageProjectRes;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.app.service.AppService;
import com.isxcode.torch.modules.project.entity.ProjectEntity;
import com.isxcode.torch.modules.project.mapper.ProjectMapper;
import com.isxcode.torch.modules.project.repository.ProjectRepository;
import com.isxcode.torch.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProjectBizService {

    private final ProjectRepository projectRepository;

    private final ProjectMapper projectMapper;

    private final ProjectService projectService;

    private final AppService appService;

    private final UserService userService;

    public AddProjectRes addProject(AddProjectReq addProjectReq) {

        Optional<ProjectEntity> projectEntityByName = projectRepository.findByName(addProjectReq.getName());
        if (projectEntityByName.isPresent()) {
            throw new IsxAppException("项目名称重复");
        }

        ProjectEntity projectEntity = projectMapper.addProjectReqToProjectEntity(addProjectReq);
        projectEntity = projectRepository.save(projectEntity);
        return projectMapper.projectEntityToAddProjectRes(projectEntity);
    }

    public void updateProject(UpdateProjectReq updateProjectReq) {

        ProjectEntity project = projectService.getProject(updateProjectReq.getId());

        Optional<ProjectEntity> projectEntityByName = projectRepository.findByName(updateProjectReq.getName());
        if (projectEntityByName.isPresent() && !projectEntityByName.get().getId().equals(updateProjectReq.getId())) {
            throw new IsxAppException("项目名称重复");
        }

        ProjectEntity projectEntity = projectMapper.updateProjectReqToProjectEntity(project, updateProjectReq);
        projectRepository.save(projectEntity);
    }

    public void deleteProject(DeleteProjectReq deleteProjectReq) {

        ProjectEntity project = projectService.getProject(deleteProjectReq.getId());
        projectRepository.delete(project);
    }

    public Page<PageProjectRes> pageProject(PageProjectReq pageProjectReq) {

        Page<ProjectEntity> projectEntityPage =
            projectRepository.searchAll(pageProjectReq.getSearchKeyWord(), PageRequest.of(pageProjectReq.getPage(), pageProjectReq.getPageSize()));

        Page<PageProjectRes> result = projectEntityPage.map(projectMapper::projectEntityToPageProjectRes);
        result.forEach(project -> {
            project.setDesignAppName(appService.getAppName(project.getDesignAppId()));
            project.setPlanAppName(appService.getAppName(project.getPlanAppId()));
            project.setDevelopAppName(appService.getAppName(project.getDevelopAppId()));
            project.setCreateByUsername(userService.getUserName(project.getCreateBy()));
        });
        return result;
    }
}
