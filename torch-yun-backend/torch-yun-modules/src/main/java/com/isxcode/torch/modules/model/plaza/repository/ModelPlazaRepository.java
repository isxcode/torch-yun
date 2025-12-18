package com.isxcode.torch.modules.model.plaza.repository;

import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.modules.model.plaza.entity.ModelPlazaEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleCode.MODEL_PLAZA})
public interface ModelPlazaRepository extends JpaRepository<ModelPlazaEntity, String> {

    @Query("SELECT M FROM ModelPlazaEntity M WHERE M.orgName LIKE %:keyword% OR M.modelName LIKE %:keyword% OR M.remark LIKE %:keyword% order by M.createDateTime desc ")
    Page<ModelPlazaEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);
}
