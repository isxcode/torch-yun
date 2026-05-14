package com.isxcode.torch.modules.projectdesign.repository;

import com.isxcode.torch.modules.projectdesign.entity.ProjectDesignEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectDesignRepository extends JpaRepository<ProjectDesignEntity, String> {

    @Query("SELECT P FROM ProjectDesignEntity P WHERE P.projectId = :projectId AND (P.name LIKE %:keyword% OR P.remark LIKE %:keyword%) ORDER BY P.createDateTime DESC")
    Page<ProjectDesignEntity> searchAll(@Param("projectId") String projectId, @Param("keyword") String keyword, Pageable pageable);

    Optional<ProjectDesignEntity> findByProjectIdAndName(String projectId, String name);
}
