package com.isxcode.torch.modules.model.mapper;

import com.isxcode.torch.api.model.req.AddModelReq;
import com.isxcode.torch.api.model.req.UpdateModelReq;
import com.isxcode.torch.api.model.res.PageModelRes;
import com.isxcode.torch.modules.model.entity.ModelEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ModelMapper {

    PageModelRes modelEntityToPageModelRes(ModelEntity modelEntity);

    ModelEntity addModelReqToModelEntity(AddModelReq addModelReq);

    @Mapping(source = "updateModelReq.name", target = "name")
    @Mapping(source = "updateModelReq.modelFile", target = "modelFile")
    @Mapping(source = "updateModelReq.remark", target = "remark")
    @Mapping(source = "updateModelReq.modelPlazaId", target = "modelPlazaId")
    @Mapping(source = "updateModelReq.deployScript", target = "deployScript")
    @Mapping(source = "modelEntity.id", target = "id")
    ModelEntity updateModelReqToModelEntity(UpdateModelReq updateModelReq, ModelEntity modelEntity);
}
