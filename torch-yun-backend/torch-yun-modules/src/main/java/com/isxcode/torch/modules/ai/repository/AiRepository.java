package com.isxcode.torch.modules.ai.repository;

import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.modules.ai.entity.AiEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@CacheConfig(cacheNames = {ModuleCode.AI})
public interface AiRepository extends JpaRepository<AiEntity, String> {

    @Query("SELECT A FROM AiEntity A" + " WHERE A.name LIKE %:keyword% " + " OR A.remark LIKE %:keyword% "
        + "OR A.name LIKE %:keyword% " + "order by A.createDateTime desc ")
    Page<AiEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);

    Optional<AiEntity> findByName(String name);
}
