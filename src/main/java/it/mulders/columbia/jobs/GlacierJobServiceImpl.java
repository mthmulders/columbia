package it.mulders.columbia.jobs;

import it.mulders.columbia.shared.TechnicalException;
import it.mulders.columbia.vaults.Vault;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.InitiateJobRequest;
import software.amazon.awssdk.services.glacier.model.JobParameters;

@AllArgsConstructor
@Slf4j
public class GlacierJobServiceImpl implements GlacierJobService {
    private final GlacierClient glacierClient;

    @Override
    public String startInventoryRetrievalJob(final Vault vault) throws TechnicalException {
        var parameters = JobParameters.builder()
                .type("inventory-retrieval")
                .build();
        var request = InitiateJobRequest.builder()
                .jobParameters(parameters)
                .vaultName(vault.getName())
                .build();

        try {
            var response = glacierClient.initiateJob(request);
            return response.jobId();
        } catch ( SdkException e) {
            log.error("Cannot initiate inventory retrieval for Vault {} - communication error: arn={},message={}",
                    vault.getName(), vault.getArn(), e.getMessage(), e);
            throw new TechnicalException("Cannot initiate inventory retrieval for Vault " + vault.getName(), e);
        }
    }
}
