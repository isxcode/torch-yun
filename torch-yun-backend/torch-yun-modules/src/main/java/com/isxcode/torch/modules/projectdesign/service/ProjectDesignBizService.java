package com.isxcode.torch.modules.projectdesign.service;

import com.isxcode.torch.api.projectdesign.req.*;
import com.isxcode.torch.api.projectdesign.res.AddProjectDesignRes;
import com.isxcode.torch.api.projectdesign.res.PageProjectDesignRes;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.projectdesign.entity.ProjectDesignEntity;
import com.isxcode.torch.modules.projectdesign.mapper.ProjectDesignMapper;
import com.isxcode.torch.modules.projectdesign.repository.ProjectDesignRepository;
import com.isxcode.torch.modules.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
}
