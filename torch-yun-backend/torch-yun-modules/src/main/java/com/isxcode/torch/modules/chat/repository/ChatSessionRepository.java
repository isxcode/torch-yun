package com.isxcode.torch.modules.chat.repository;

import com.isxcode.torch.api.chat.ao.ChatAo;
import com.isxcode.torch.api.main.constants.ModuleCode;
import com.isxcode.torch.modules.chat.entity.ChatSessionEntity;
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
@CacheConfig(cacheNames = {ModuleCode.CHAT})
public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, String> {

    @Query(
        value = "select coalesce(max(C.sessionIndex),0) from ChatSessionEntity C where C.chatId = :chatId and C.appId= :appId")
    Integer getMaxIndex(@Param("chatId") String chatId, @Param("appId") String appId);

    Optional<ChatSessionEntity> findBySessionIndexAndChatId(Integer sessionIndex, String chatId);

    List<ChatSessionEntity> findAllByChatId(String chatId);

    @Query(
        value = "select new com.isxcode.torch.api.chat.ao.ChatAo(C2.sessionContent,C1.id,C1.appId,C1.createDateTime) from ChatEntity C1 left join ChatSessionEntity C2 on C1.id=C2.chatId and C2.sessionIndex=0 and C1.chatType = 'PROD' where C1.submitter = :userId and (:appId is null or :appId ='' or C1.appId=:appId) and C2.sessionContent like %:searchKeyWord% and C1.tenantId=:tenantId order by C1.createDateTime desc")
    Page<ChatAo> pageChatHistory(@Param("tenantId") String tenantId, @Param("searchKeyWord") String searchKeyWord,
        @Param("appId") String appId, @Param("userId") String userId, Pageable pageable);

    Boolean existsBySessionIndexAndChatId(Integer sessionIndex, String chatId);
}
