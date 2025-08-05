package com.isxcode.torch.modules.model.repository;

import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.modules.model.entity.ModelEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
@CacheConfig(cacheNames = {ModuleCode.MODEL})
public interface ModelRepository extends JpaRepository<ModelEntity, String> {

    @Query("SELECT M FROM ModelEntity M " + "WHERE M.name LIKE %:keyword% " + " OR M.remark LIKE %:keyword%  "
        + "order by M.createDateTime desc ")
    Page<ModelEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);

    Optional<ModelEntity> findByName(String name);
}
