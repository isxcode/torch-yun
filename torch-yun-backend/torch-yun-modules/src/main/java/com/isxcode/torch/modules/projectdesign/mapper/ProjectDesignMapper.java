package com.isxcode.torch.modules.projectdesign.mapper;

import com.isxcode.torch.api.projectdesign.req.AddProjectDesignReq;
import com.isxcode.torch.api.projectdesign.req.UpdateProjectDesignReq;
import com.isxcode.torch.api.projectdesign.res.AddProjectDesignRes;
import com.isxcode.torch.api.projectdesign.res.PageProjectDesignRes;
import com.isxcode.torch.modules.projectdesign.entity.ProjectDesignEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectDesignMapper {

    ProjectDesignEntity addReqToEntity(AddProjectDesignReq req);

    AddProjectDesignRes entityToAddRes(ProjectDesignEntity entity);

    PageProjectDesignRes entityToPageRes(ProjectDesignEntity entity);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "projectId", source = "req.projectId")
    @Mapping(target = "name", source = "req.name")
    @Mapping(target = "remark", source = "req.remark")
    ProjectDesignEntity updateReqToEntity(ProjectDesignEntity entity, UpdateProjectDesignReq req);
}
