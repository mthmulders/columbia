package it.mulders.columbia.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.mulders.columbia.inventory.aws.VaultInventory;
import it.mulders.columbia.jobs.entity.InventoryRetrievalJobEntity;
import it.mulders.columbia.shared.TechnicalException;
import it.mulders.columbia.vaults.Vault;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.DescribeJobRequest;
import software.amazon.awssdk.services.glacier.model.GetJobOutputRequest;
import software.amazon.awssdk.services.glacier.model.InitiateJobRequest;
import software.amazon.awssdk.services.glacier.model.JobParameters;
import software.amazon.awssdk.services.glacier.model.ResourceNotFoundException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import static software.amazon.awssdk.services.glacier.model.StatusCode.UNKNOWN_TO_SDK_VERSION;

@AllArgsConstructor
@Slf4j
public class GlacierJobServiceImpl implements GlacierJobService {
    private static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.registerModule(new JavaTimeModule());
    }
    private static final ObjectReader reader = mapper.readerFor(VaultInventory.class);

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
        } catch (SdkException e) {
            log.error("Cannot initiate inventory retrieval for Vault {} - communication error: arn={},message={}",
                    vault.getName(), vault.getArn(), e.getMessage(), e);
            throw new TechnicalException("Cannot initiate inventory retrieval for Vault " + vault.getName(), e);
        }
    }

    @Override
    public Optional<InventoryRetrievalJobEntity.Status> getInventoryRetrievalJobStatus(final InventoryRetrievalJobEntity job) throws TechnicalException {
        var jobId = job.getJobId();
        var vaultName = job.getVaultName();

        var request = DescribeJobRequest.builder()
                .jobId(jobId)
                .vaultName(vaultName)
                .build();

        try {
            var response = glacierClient.describeJob(request);

            if (response.statusCode() == UNKNOWN_TO_SDK_VERSION) {
                log.error("Inventory retrieval job has unknown status: job-id={}, vault-name={}, status={}",
                        jobId, vaultName, response.statusCode());
                return Optional.empty();
            }

            return Optional.of(switch (response.statusCode()) {
                case IN_PROGRESS -> InventoryRetrievalJobEntity.Status.IN_PROGRESS;
                case FAILED -> InventoryRetrievalJobEntity.Status.FAILED;
                case SUCCEEDED -> InventoryRetrievalJobEntity.Status.SUCCEEDED;
                case UNKNOWN_TO_SDK_VERSION -> null;
            });

        } catch (final ResourceNotFoundException e) {
            log.info("{}", e.getMessage());
            return Optional.of(InventoryRetrievalJobEntity.Status.EXPIRED);
        } catch (final SdkException e) {
            log.error("Cannot get inventory retrieval job status for job {} - communication error: job-id={}, vault-name={}, message={}",
                    jobId, vaultName, e.getMessage(), e);
            throw new TechnicalException("Cannot get inventory retrieval job status for job " + jobId, e);
        }
    }

    @Override
    public Optional<VaultInventory> getInventoryRetrievalJobOutput(final InventoryRetrievalJobEntity job) throws TechnicalException {
        var jobId = job.getJobId();
        var vaultName = job.getVaultName();

        var request = GetJobOutputRequest.builder()
                .jobId(jobId)
                .vaultName(vaultName)
                .build();

        try {
            var response = glacierClient.getJobOutput(request);

            try (var input = new BufferedReader(new InputStreamReader(response))) {
                var data = reader.readValue(input, VaultInventory.class);
                return Optional.of(data);
            }
        } catch (final ResourceNotFoundException e) {
            log.info("{}", e.getMessage());
            return Optional.empty();
        } catch (final IOException | SdkException e) {
            log.error("Cannot get inventory retrieval job output for job {} - communication error: job-id={},message={}",
                    jobId, e.getMessage(), e);
            throw new TechnicalException("Cannot get inventory retrieval job output for job " + jobId, e);
        }
    }
}
