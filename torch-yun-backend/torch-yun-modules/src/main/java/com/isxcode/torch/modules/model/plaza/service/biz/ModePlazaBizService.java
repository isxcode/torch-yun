package com.isxcode.torch.modules.model.plaza.service.biz;

import com.isxcode.torch.api.model.plaza.req.PageModelReq;
import com.isxcode.torch.api.model.plaza.res.PageModelRes;
import com.isxcode.torch.modules.model.plaza.entity.ModelPlazaEntity;
import com.isxcode.torch.modules.model.plaza.mapper.ModelPlazaMapper;
import com.isxcode.torch.modules.model.plaza.repository.ModelPlazaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ModePlazaBizService {

    private final ModelPlazaRepository modelPlazaRepository;

    private final ModelPlazaMapper modelPlazaMapper;

    public Page<PageModelRes> pageModel(PageModelReq pageModelReq) {

        Page<ModelPlazaEntity> modelPlazaPage = modelPlazaRepository.searchAll(pageModelReq.getSearchKeyWord(),
            PageRequest.of(pageModelReq.getPage(), pageModelReq.getPageSize()), pageModelReq.getOrgName(),
            pageModelReq.getIsOnline());

        return modelPlazaPage.map(modelPlazaMapper::modelPlazaEntityToPageModelRes);
    }
}
