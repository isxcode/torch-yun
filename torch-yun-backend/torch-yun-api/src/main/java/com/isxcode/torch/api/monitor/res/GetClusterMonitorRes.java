package com.isxcode.torch.api.monitor.res;

import com.isxcode.torch.api.monitor.dto.MonitorLineDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetClusterMonitorRes {

    private List<MonitorLineDto> line;
}
