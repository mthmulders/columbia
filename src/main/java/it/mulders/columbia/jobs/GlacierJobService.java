package it.mulders.columbia.jobs;

import it.mulders.columbia.shared.TechnicalException;
import it.mulders.columbia.vaults.Vault;

public interface GlacierJobService {

    /**
     * Initiates a "inventory retrieval" job for the given Vault. The job will be submitted in the "Bulk" tier.
     * @param vault The vault for which inventory should be retrieved.
     * @return An AWS identifier for the Job.
     * @throws TechnicalException When the AWS API call failed.
     */
    String startInventoryRetrievalJob(final Vault vault) throws TechnicalException;
}
