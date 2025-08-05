package com.isxcode.torch.modules.monitor.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@SQLDelete(sql = "UPDATE PY_MONITOR SET deleted = 1 WHERE id = ?")
@Where(clause = "deleted = 0 ${TENANT_FILTER} ")
@Table(name = "PY_MONITOR")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@EntityListeners(AuditingEntityListener.class)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class MonitorEntity {

    @Id
    @GeneratedValue(generator = "sy-id-generator")
    @GenericGenerator(name = "sy-id-generator", strategy = "com.isxcode.torch.config.GeneratedValueConfig")
    private String id;

    private String clusterId;

    private String clusterNodeId;

    private String status;

    private String log;

    private Long usedStorageSize;

    private Long usedMemorySize;

    private Long networkIoReadSpeed;

    private Long networkIoWriteSpeed;

    private Long diskIoReadSpeed;

    private Long diskIoWriteSpeed;

    private Double cpuPercent;

    private LocalDateTime createDateTime;

    @Transient
    private Integer deleted;

    private String tenantId;
}
