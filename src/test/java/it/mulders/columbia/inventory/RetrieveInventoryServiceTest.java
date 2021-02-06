package it.mulders.columbia.inventory;

import it.mulders.columbia.jobs.GlacierJobService;
import it.mulders.columbia.jobs.entity.InventoryRetrievalJobEntity;
import it.mulders.columbia.jobs.repository.InventoryRetrievalJobRepository;
import it.mulders.columbia.shared.TechnicalException;
import it.mulders.columbia.vaults.Vault;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RetrieveInventoryServiceTest implements WithAssertions {
    private final GlacierJobService glacierJobService = mock(GlacierJobService.class);
    private final InventoryRetrievalJobRepository inventoryRetrievalJobRepository = mock(InventoryRetrievalJobRepository.class);

    private final RetrieveInventoryService service = new RetrieveInventoryService(
            glacierJobService,
            inventoryRetrievalJobRepository
    );

    @DisplayName("handleRetrieveInventoryCommand")
    @Nested
    class HandleRetrieveInventoryCommand {
        @Test
        void should_initiate_inventory_retrieval() throws TechnicalException {
            // Arrange
            var vault = Vault.builder().build();
            var captured = new AtomicReference<Vault>();
            var service = new RetrieveInventoryService(glacierJobService, inventoryRetrievalJobRepository) {
                @Override
                public void retrieveInventory(final Vault vault) {
                    captured.set(vault);
                }
            };


            // Act
            service.handleRetrieveInventoryCommand(new RetrieveInventoryCommand(this, vault));

            // Assert
            assertThat(captured.get()).isEqualTo(vault);
        }
    }

    @DisplayName("retrieveInventory")
    @Nested
    class RetrieveInventory {
        @Test
        void should_initiate_aws_inventory_retrieval_job() throws TechnicalException {
            // Arrange
            var vault = Vault.builder().name("Example").build();

            // Act
            service.retrieveInventory(vault);

            // Assert
            verify(glacierJobService).startInventoryRetrievalJob(vault);
        }

        @Test
        void should_store_job_in_database() throws TechnicalException {
            // Arrange
            var arn = "arn:aws:glacier:eu-central-1:460046808775:vaults/Example_Vault";
            var name = "Example_Vault";
            var vault = Vault.builder().name(name).arn(arn).build();
            var jobId = "42";
            when(glacierJobService.startInventoryRetrievalJob(any())).thenReturn(jobId);

            // Act
            service.retrieveInventory(vault);

            // Assert
            var captor = ArgumentCaptor.forClass(InventoryRetrievalJobEntity.class);
            verify(inventoryRetrievalJobRepository).save(captor.capture());
            assertThat(captor.getValue().getStarted()).isNotNull();
            assertThat(captor.getValue().getJobId()).isEqualTo(jobId);
            assertThat(captor.getValue().getVaultArn()).isEqualTo(arn);
            assertThat(captor.getValue().getVaultName()).isEqualTo(name);
            assertThat(captor.getValue().getStatus()).isEqualTo(InventoryRetrievalJobEntity.Status.IN_PROGRESS);
        }
    }

}