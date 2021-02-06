package it.mulders.columbia.jobs.repository;

import it.mulders.columbia.AbstractIT;
import it.mulders.columbia.jobs.entity.InventoryRetrievalJobEntity;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

class InventoryRetrievalJobRepositoryIT extends AbstractIT implements WithAssertions {
    @Autowired
    private InventoryRetrievalJobRepository repository;

    @Test
    void should_retrieve_by_id() {
        // Arrange
        var entity = repository.save(InventoryRetrievalJobEntity.builder()
                .id(UUID.randomUUID())
                .started(LocalDateTime.now())
                .jobId("42")
                .status(InventoryRetrievalJobEntity.Status.IN_PROGRESS)
                .vaultArn("arn:aws:glacier:eu-central-1:460046808775:vaults/Example_Vault")
                .vaultName("Example_Vault")
                .build());


        // Act
        var result = repository.findById(entity.getId());

        // Assert
        assertThat(result).hasValue(entity);
    }
}