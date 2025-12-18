package com.isxcode.torch.modules.model.plaza.mapper;

import com.isxcode.torch.api.model.plaza.res.PageModelRes;
import com.isxcode.torch.modules.model.plaza.entity.ModelPlazaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ModelPlazaMapper {

    PageModelRes modelPlazaEntityToPageModelRes(ModelPlazaEntity modelPlazaEntity);
}
