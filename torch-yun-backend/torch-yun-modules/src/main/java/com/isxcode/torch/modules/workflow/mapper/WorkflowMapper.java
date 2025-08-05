package com.isxcode.torch.modules.workflow.mapper;

import com.isxcode.torch.api.instance.ao.WorkflowInstanceAo;
import com.isxcode.torch.api.instance.res.QueryWorkFlowInstancesRes;
import com.isxcode.torch.api.monitor.ao.WorkflowMonitorAo;
import com.isxcode.torch.api.monitor.res.PageInstancesRes;
import com.isxcode.torch.api.workflow.dto.WorkInstanceInfo;
import com.isxcode.torch.api.workflow.req.AddWorkflowReq;
import com.isxcode.torch.api.workflow.req.UpdateWorkflowReq;
import com.isxcode.torch.api.workflow.res.PageWorkflowRes;
import com.isxcode.torch.modules.work.entity.WorkInstanceEntity;
import com.isxcode.torch.modules.workflow.entity.WorkflowEntity;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkflowMapper {

    WorkflowEntity addWorkflowReqToWorkflowEntity(AddWorkflowReq addWorkflowReq);

    @Mapping(source = "wofUpdateWorkflowReq.name", target = "name")
    @Mapping(source = "workflowEntity.id", target = "id")
    @Mapping(source = "wofUpdateWorkflowReq.remark", target = "remark")
    @Mapping(source = "wofUpdateWorkflowReq.defaultClusterId", target = "defaultClusterId")
    WorkflowEntity updateWorkflowReqToWorkflowEntity(UpdateWorkflowReq wofUpdateWorkflowReq,
        WorkflowEntity workflowEntity);

    PageWorkflowRes workflowEntityToQueryWorkflowRes(WorkflowEntity workflowEntity);

    @Mapping(source = "status", target = "runStatus")
    @Mapping(source = "id", target = "workInstanceId")
    WorkInstanceInfo workInstanceEntityToWorkInstanceInfo(WorkInstanceEntity workInstances);

    List<WorkInstanceInfo> workInstanceEntityListToWorkInstanceInfoList(List<WorkInstanceEntity> workInstances);

    @Mapping(target = "startDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "endDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    PageInstancesRes workflowMonitorAoToPageInstancesRes(WorkflowMonitorAo workflowMonitorAo);

    @Mapping(target = "startDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "endDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "nextPlanDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "planStartDateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    QueryWorkFlowInstancesRes wfiWorkflowInstanceAo2WfiQueryWorkFlowInstancesRes(
        WorkflowInstanceAo workflowInstanceAoPage);
}
