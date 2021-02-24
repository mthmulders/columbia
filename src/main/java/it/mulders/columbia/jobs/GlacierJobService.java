package it.mulders.columbia.jobs;

import it.mulders.columbia.jobs.entity.InventoryRetrievalJobEntity;
import it.mulders.columbia.shared.TechnicalException;
import it.mulders.columbia.vaults.Vault;

import java.util.Optional;

public interface GlacierJobService {

    /**
     * Initiates a "inventory retrieval" job for the given Vault. The job will be submitted in the "Bulk" tier.
     * @param vault The vault for which inventory should be retrieved.
     * @return An AWS identifier for the job.
     * @throws TechnicalException When the AWS API call failed.
     */
    String startInventoryRetrievalJob(final Vault vault) throws TechnicalException;

    /**
     * Checks the specified "inventory retrieval" job to see if it has finished
     * @param jobId The AWS identifier for the job.
     * @return The status of the job, or an empty {@link Optional} if it cannot be determined.
     * @throws TechnicalException When the AWS API call failed.
     */
    Optional<InventoryRetrievalJobEntity.Status> getInventoryRetrievalJobStatus(final String jobId) throws TechnicalException;
}
