package com.isxcode.torch.api.workflow.res;

import com.isxcode.torch.api.work.dto.CronConfig;
import lombok.Data;

import java.util.List;

@Data
public class GetWorkflowRes {

    private Object webConfig;

    private CronConfig cronConfig;

    private List<String> alarmList;

    private String invokeStatus;

    private String invokeUrl;
}
