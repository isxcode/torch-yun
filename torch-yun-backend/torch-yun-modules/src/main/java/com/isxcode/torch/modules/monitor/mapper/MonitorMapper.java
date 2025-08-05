package com.isxcode.torch.modules.monitor.mapper;

import com.isxcode.torch.api.monitor.dto.NodeMonitorInfo;
import com.isxcode.torch.modules.monitor.entity.MonitorEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MonitorMapper {

    MonitorEntity nodeMonitorInfoToMonitorEntity(NodeMonitorInfo nodeMonitorInfo);
}
