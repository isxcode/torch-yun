package com.isxcode.torch.modules.model.service;

import com.isxcode.torch.api.datasource.req.*;
import com.isxcode.torch.api.datasource.res.*;
import com.isxcode.torch.api.model.constant.ModelStatus;
import com.isxcode.torch.api.model.req.AddModelReq;
import com.isxcode.torch.api.model.req.PageModelReq;
import com.isxcode.torch.api.model.req.UpdateModelReq;
import com.isxcode.torch.api.model.res.PageModelRes;

import javax.transaction.Transactional;

import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.modules.file.service.FileService;
import com.isxcode.torch.modules.model.entity.ModelEntity;
import com.isxcode.torch.modules.model.mapper.ModelMapper;
import com.isxcode.torch.modules.model.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.ap.internal.util.Strings;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static com.isxcode.torch.common.config.CommonConfig.JPA_TENANT_MODE;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ModelBizService {

    private final ModelRepository modelRepository;

    private final ModelMapper modelMapper;

    private final ModelService modelService;

    private final FileService fileService;

    private final ResourceLoader resourceLoader;

    public Page<PageModelRes> pageModel(PageModelReq pageModelReq) {

        JPA_TENANT_MODE.set(false);

        Page<ModelEntity> modelEntityPage = modelRepository.searchAll(pageModelReq.getSearchKeyWord(),
            PageRequest.of(pageModelReq.getPage(), pageModelReq.getPageSize()));

        // 翻译模型文件名称
        Page<PageModelRes> map = modelEntityPage.map(modelMapper::modelEntityToPageModelRes);
        map.getContent().forEach(e -> {
            if (!Strings.isEmpty(e.getModelFile())) {
                e.setModelFileName(fileService.getFileName(e.getModelFile()));
            }
        });

        return map;
    }

    public void addModel(AddModelReq addModelReq) {

        // 判断名称是否重复
        modelRepository.findByName(addModelReq.getName()).ifPresent(e -> {
            throw new IsxAppException("模型名称重复");
        });

        // 转换对象
        ModelEntity modelEntity = modelMapper.addModelReqToModelEntity(addModelReq);

        // 手动创建的仓库
        modelEntity.setStatus(ModelStatus.ENABLE);

        // 如果脚本为空，则添加默认脚本
        if (addModelReq.getDeployScript().isEmpty()) {
            Resource resource = resourceLoader.getResource("classpath:deployScript.py");
            try (InputStream inputStream = resource.getInputStream();
                BufferedReader reader =
                    new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String content = reader.lines().collect(Collectors.joining("\n"));
                modelEntity.setDeployScript(content);
            } catch (IOException e) {
                throw new IsxAppException("安装包部署异常");
            }
        }

        // 持久化
        modelRepository.save(modelEntity);
    }

    public void updateModel(UpdateModelReq updateModelReq) {

        // 判断模型是否存在
        ModelEntity model = modelService.getModel(updateModelReq.getId());

        // 判断名称是否重复
        modelRepository.findByName(updateModelReq.getName()).ifPresent(e -> {
            if (!updateModelReq.getId().equals(e.getId())) {
                throw new IsxAppException("模型名称重复");
            }
        });

        // 修改标签备注模型文件
        model = modelMapper.updateModelReqToModelEntity(updateModelReq, model);

        // 持久化
        modelRepository.save(model);
    }
}
