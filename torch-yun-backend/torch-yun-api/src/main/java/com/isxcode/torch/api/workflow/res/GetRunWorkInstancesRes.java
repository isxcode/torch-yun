package com.isxcode.torch.api.workflow.res;

import com.isxcode.torch.api.workflow.dto.WorkInstanceInfo;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetRunWorkInstancesRes {

    private String flowStatus;

    private String flowRunLog;

    private List<WorkInstanceInfo> workInstances;
}
