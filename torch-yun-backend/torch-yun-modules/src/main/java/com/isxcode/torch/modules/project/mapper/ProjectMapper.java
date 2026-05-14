package com.isxcode.torch.modules.project.mapper;

import com.isxcode.torch.api.project.req.AddProjectReq;
import com.isxcode.torch.api.project.req.UpdateProjectReq;
import com.isxcode.torch.api.project.res.AddProjectRes;
import com.isxcode.torch.api.project.res.PageProjectRes;
import com.isxcode.torch.modules.project.entity.ProjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectEntity addProjectReqToProjectEntity(AddProjectReq addProjectReq);

    AddProjectRes projectEntityToAddProjectRes(ProjectEntity projectEntity);

    PageProjectRes projectEntityToPageProjectRes(ProjectEntity projectEntity);

    @Mapping(target = "id", source = "projectEntity.id")
    @Mapping(target = "name", source = "updateProjectReq.name")
    @Mapping(target = "workspace", source = "updateProjectReq.workspace")
    @Mapping(target = "assetsDir", source = "updateProjectReq.assetsDir")
    @Mapping(target = "designAppId", source = "updateProjectReq.designAppId")
    @Mapping(target = "planAppId", source = "updateProjectReq.planAppId")
    @Mapping(target = "developAppId", source = "updateProjectReq.developAppId")
    @Mapping(target = "remark", source = "updateProjectReq.remark")
    ProjectEntity updateProjectReqToProjectEntity(ProjectEntity projectEntity, UpdateProjectReq updateProjectReq);
}
