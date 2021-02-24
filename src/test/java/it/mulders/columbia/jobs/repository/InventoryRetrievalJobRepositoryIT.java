package it.mulders.columbia.jobs.repository;

import it.mulders.columbia.AbstractIT;
import it.mulders.columbia.jobs.entity.InventoryRetrievalJobEntity;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class InventoryRetrievalJobRepositoryIT extends AbstractIT implements WithAssertions {
    @Autowired
    private InventoryRetrievalJobRepository repository;

    @BeforeEach
    void clear() {
        repository.deleteAll();
    }

    @Test
    void should_retrieve_by_id() {
        // Arrange
        var started = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        var entity = repository.save(InventoryRetrievalJobEntity.builder()
                .id(UUID.randomUUID())
                .started(started)
                .jobId("42")
                .status(InventoryRetrievalJobEntity.Status.IN_PROGRESS)
                .vaultArn("arn:aws:glacier:eu-central-1:460046808775:vaults/Example_Vault")
                .vaultName("Example_Vault")
                .build()
        );

        // Act
        var result = repository.findById(entity.getId());

        // Assert
        assertThat(result).hasValue(entity);
    }

    @Test
    void should_find_in_progress() {
        // Arrange
        var started = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
        var entity1 = repository.save(InventoryRetrievalJobEntity.builder()
                .id(UUID.randomUUID())
                .started(started)
                .jobId("42")
                .status(InventoryRetrievalJobEntity.Status.IN_PROGRESS)
                .vaultArn("arn:aws:glacier:eu-central-1:460046808775:vaults/Example_Vault")
                .vaultName("Example_Vault")
                .build()
        );
        var entity2 = repository.save(InventoryRetrievalJobEntity.builder()
                .id(UUID.randomUUID())
                .started(started)
                .jobId("43")
                .status(InventoryRetrievalJobEntity.Status.SUCCEEDED)
                .vaultArn("arn:aws:glacier:eu-central-1:460046808775:vaults/Example_Vault")
                .vaultName("Example_Vault")
                .build()
        );

        // Act
        var result = repository.findInProgress();

        // Assert
        assertThat(result)
                .contains(entity1)
                .doesNotContain(entity2);
    }
}