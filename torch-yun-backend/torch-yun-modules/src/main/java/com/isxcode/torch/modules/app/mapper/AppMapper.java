package com.isxcode.torch.modules.app.mapper;

import com.isxcode.torch.api.app.req.AddAppReq;
import com.isxcode.torch.api.app.req.UpdateAppReq;
import com.isxcode.torch.api.app.res.AddAppRes;
import com.isxcode.torch.api.app.res.PageAppRes;
import com.isxcode.torch.modules.app.entity.AppEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppMapper {

    PageAppRes appEntityToPageAppRes(AppEntity appEntity);

    AppEntity addAppReqToAppEntity(AddAppReq addAppReq);

    AddAppRes appEntityToAddAppRes(AppEntity appEntity);

    @Mapping(target = "id", source = "appEntity.id")
    @Mapping(target = "name", source = "updateAppReq.name")
    @Mapping(target = "remark", source = "updateAppReq.remark")
    @Mapping(target = "aiId", source = "updateAppReq.aiId")
    AppEntity updateAppReqToAppEntity(AppEntity appEntity, UpdateAppReq updateAppReq);
}
