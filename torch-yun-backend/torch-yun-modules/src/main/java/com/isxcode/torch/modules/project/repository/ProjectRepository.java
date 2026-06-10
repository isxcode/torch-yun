package com.isxcode.torch.modules.project.repository;

import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.modules.project.entity.ProjectEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@CacheConfig(cacheNames = {ModuleCode.PROJECT})
public interface ProjectRepository extends JpaRepository<ProjectEntity, String> {

    @Query("SELECT P FROM ProjectEntity P WHERE (P.name LIKE %:keyword% OR P.workspace LIKE %:keyword% OR P.remark LIKE %:keyword%) ORDER BY P.createDateTime DESC")
    Page<ProjectEntity> searchAll(@Param("keyword") String searchKeyWord, Pageable pageable);

    Optional<ProjectEntity> findByName(String name);
}
