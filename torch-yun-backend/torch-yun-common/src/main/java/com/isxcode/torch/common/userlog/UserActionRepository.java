package com.isxcode.torch.common.userlog;

import com.isxcode.torch.api.main.constants.ModuleCode;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@CacheConfig(cacheNames = {ModuleCode.USER})
public interface UserActionRepository extends JpaRepository<UserActionEntity, String> {
}
