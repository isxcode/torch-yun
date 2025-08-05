package com.isxcode.torch.api.instance.res;

import com.isxcode.torch.api.instance.dto.WorkInstanceDto;

import java.util.List;
import lombok.Data;

@Data
public class GetWorkflowInstanceRes {

    private String webConfig;

    private List<WorkInstanceDto> workInstances;
}
