package it.mulders.columbia.inventory;

import it.mulders.columbia.jobs.GlacierJobService;
import it.mulders.columbia.jobs.entity.InventoryRetrievalJobEntity;
import it.mulders.columbia.jobs.repository.InventoryRetrievalJobRepository;
import it.mulders.columbia.shared.TechnicalException;
import it.mulders.columbia.vaults.Vault;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Component
@Slf4j
public class RetrieveInventoryService {
    private final GlacierJobService glacierJobService;
    private final InventoryRetrievalJobRepository inventoryRetrievalJobRepository;

    @EventListener
    public void handleRetrieveInventoryCommand(final RetrieveInventoryCommand command) throws TechnicalException {
        retrieveInventory(command.getVault());
    }

    public void retrieveInventory(final Vault vault) throws TechnicalException {
        log.info("Starting retrieval of inventory for vault {}", vault);

        final String jobId = glacierJobService.startInventoryRetrievalJob(vault);

        inventoryRetrievalJobRepository.save(InventoryRetrievalJobEntity.builder()
                .id(UUID.randomUUID())
                .started(LocalDateTime.now())
                .status(InventoryRetrievalJobEntity.Status.IN_PROGRESS)
                .jobId(jobId)
                .vaultArn(vault.getArn())
                .vaultName(vault.getName())
                .build());

        // Periodically check if the jobId has completed (GetJobOutputRequest)
        // Fetch the output of the job (

        // see https://docs.aws.amazon.com/amazonglacier/latest/dev/retrieving-vault-inventory-java.html
    }
}
