package com.isxcode.torch.modules.work.mapper;

import com.isxcode.torch.api.work.dto.SyncWorkConfig;
import com.isxcode.torch.api.work.res.GetSyncWorkConfigRes;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkConfigMapper {

    GetSyncWorkConfigRes syncWorkConfigToGetSyncWorkConfigRes(SyncWorkConfig syncWorkConfig);
}
