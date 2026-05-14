package com.isxcode.torch.modules.projectdesign.service;

import com.isxcode.torch.api.projectdesign.req.*;
import com.isxcode.torch.api.projectdesign.res.AddProjectDesignRes;
import com.isxcode.torch.api.projectdesign.res.PageProjectDesignRes;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.api.chat.req.GetMaxChatIdReq;
import com.isxcode.torch.api.chat.req.SendChatReq;
import com.isxcode.torch.api.chat.res.GetMaxChatIdRes;
import com.isxcode.torch.modules.projectdesign.entity.ProjectDesignEntity;
import com.isxcode.torch.modules.projectdesign.mapper.ProjectDesignMapper;
import com.isxcode.torch.modules.projectdesign.repository.ProjectDesignRepository;
import com.isxcode.torch.modules.chat.service.ChatBizService;
import com.isxcode.torch.modules.project.service.ProjectService;
import com.isxcode.torch.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectDesignBizService {

    private final ProjectDesignRepository repository;
    private final ProjectDesignMapper mapper;
    private final ProjectDesignService service;
    private final UserService userService;
    private final ProjectService projectService;
    private final ChatBizService chatBizService;

    public AddProjectDesignRes addProjectDesign(AddProjectDesignReq req) {
        Optional<ProjectDesignEntity> byName = repository.findByProjectIdAndName(req.getProjectId(), req.getName());
        if (byName.isPresent()) {
            throw new IsxAppException("名称重复");
        }
        ProjectDesignEntity saved = repository.save(mapper.addReqToEntity(req));
        return mapper.entityToAddRes(saved);
    }

    public void updateProjectDesign(UpdateProjectDesignReq req) {
        ProjectDesignEntity old = service.getProjectDesign(req.getId());
        Optional<ProjectDesignEntity> byName = repository.findByProjectIdAndName(req.getProjectId(), req.getName());
        if (byName.isPresent() && !byName.get().getId().equals(req.getId())) {
            throw new IsxAppException("名称重复");
        }
        repository.save(mapper.updateReqToEntity(old, req));
    }

    public void deleteProjectDesign(DeleteProjectDesignReq req) {
        repository.delete(service.getProjectDesign(req.getId()));
    }

    public Page<PageProjectDesignRes> pageProjectDesign(PageProjectDesignReq req) {
        Page<ProjectDesignEntity> page = repository.searchAll(req.getProjectId(), req.getSearchKeyWord(),
            PageRequest.of(req.getPage(), req.getPageSize()));
        Page<PageProjectDesignRes> result = page.map(mapper::entityToPageRes);
        result.forEach(e -> e.setCreateByUsername(userService.getUserName(e.getCreateBy())));
        return result;
    }

    public GetMaxChatIdRes getProjectDesignMaxChatId(GetProjectDesignMaxChatIdReq req) {

        String designAppId = projectService.getProject(req.getProjectId()).getDesignAppId();
        if (designAppId == null || designAppId.trim().isEmpty()) {
            throw new IsxAppException("当前项目未配置项目设计模型应用");
        }

        GetMaxChatIdReq chatReq = new GetMaxChatIdReq();
        chatReq.setChatId(req.getChatId());
        chatReq.setChatType(req.getChatType());
        chatReq.setAppId(designAppId);
        return chatBizService.getMaxChatId(chatReq);
    }

    public SseEmitter sendProjectDesignChat(SendProjectDesignChatReq req) {

        String designAppId = projectService.getProject(req.getProjectId()).getDesignAppId();
        if (designAppId == null || designAppId.trim().isEmpty()) {
            throw new IsxAppException("当前项目未配置项目设计模型应用");
        }

        SendChatReq chatReq = new SendChatReq();
        chatReq.setAppId(designAppId);
        chatReq.setChatId(req.getChatId());
        chatReq.setMaxChatIndexId(req.getMaxChatIndexId());
        chatReq.setChatContent(req.getChatContent());
        return chatBizService.sendChat(chatReq);
    }
}
