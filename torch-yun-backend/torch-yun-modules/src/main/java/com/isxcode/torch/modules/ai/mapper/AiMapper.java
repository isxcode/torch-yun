package com.isxcode.torch.modules.ai.mapper;

import com.isxcode.torch.api.ai.req.AddAiReq;
import com.isxcode.torch.api.ai.req.UpdateAiReq;
import com.isxcode.torch.api.ai.res.PageAiRes;
import com.isxcode.torch.modules.ai.entity.AiEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AiMapper {

    PageAiRes aiEntityToPageAiRes(AiEntity aiEntity);

    @Mapping(target = "authConfig", ignore = true)
    @Mapping(target = "clusterConfig", ignore = true)
    AiEntity addAiReqToAiEntity(AddAiReq addAiReq);

    @Mapping(target = "authConfig", ignore = true)
    @Mapping(target = "clusterConfig", ignore = true)
    @Mapping(target = "id", source = "aiEntity.id")
    @Mapping(target = "status", source = "aiEntity.status")
    @Mapping(target = "name", source = "updateAiReq.name")
    @Mapping(target = "remark", source = "updateAiReq.remark")
    AiEntity updateAiReqToAiEntity(UpdateAiReq updateAiReq, AiEntity aiEntity);
}
