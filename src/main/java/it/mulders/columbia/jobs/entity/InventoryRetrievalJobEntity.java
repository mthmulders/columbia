package it.mulders.columbia.jobs.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Entity
@EqualsAndHashCode
@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@Table(name = "inventory_retrieval_job")
@ToString(of = { "id", "vaultArn", "status" })
public class InventoryRetrievalJobEntity {
    public enum Status {
        IN_PROGRESS,
        FAILED,
        SUCCEEDED
    }
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "started_at", nullable = false)
    private LocalDateTime started;
    @Column(name = "job_id", nullable = false)
    private String jobId;
    @Column(name = "vault_arn", nullable = false)
    private String vaultArn;
    @Column(name = "vault_name", nullable = false)
    private String vaultName;
    @Enumerated( EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
}
