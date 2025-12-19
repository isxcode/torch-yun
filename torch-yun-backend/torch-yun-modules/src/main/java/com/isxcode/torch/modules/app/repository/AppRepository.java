package com.isxcode.torch.modules.app.repository;

import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.modules.app.entity.AppEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@CacheConfig(cacheNames = {ModuleCode.AI})
public interface AppRepository extends JpaRepository<AppEntity, String> {

    @Query("SELECT A FROM AppEntity A WHERE (A.status = :appStatus or :appStatus is null ) and ( A.name LIKE %:keyword% OR A.remark LIKE %:keyword% OR A.name LIKE %:keyword% ) order by A.createDateTime desc ")
    Page<AppEntity> searchAll(@Param("keyword") String searchKeyWord, @Param("appStatus") String appStatus,
        Pageable pageable);

    Optional<AppEntity> findByName(String name);

    List<AppEntity> findByStatusAndAiId(String status, String aiId);

    Optional<AppEntity> findByDefaultApp(String defaultApp);

    void deleteAllByAiId(String aiId);
}
