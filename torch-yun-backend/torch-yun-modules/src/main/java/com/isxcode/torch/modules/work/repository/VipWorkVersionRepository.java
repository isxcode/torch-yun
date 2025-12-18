package com.isxcode.torch.modules.work.repository;

import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.modules.work.entity.VipWorkVersionEntity;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleCode.VIP_WORK})
public interface VipWorkVersionRepository extends JpaRepository<VipWorkVersionEntity, String> {
}
