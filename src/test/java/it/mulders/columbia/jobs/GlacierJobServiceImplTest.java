package it.mulders.columbia.jobs;

import it.mulders.columbia.jobs.entity.InventoryRetrievalJobEntity;
import it.mulders.columbia.shared.TechnicalException;
import it.mulders.columbia.vaults.Vault;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.DescribeJobRequest;
import software.amazon.awssdk.services.glacier.model.DescribeJobResponse;
import software.amazon.awssdk.services.glacier.model.InitiateJobRequest;
import software.amazon.awssdk.services.glacier.model.InitiateJobResponse;
import software.amazon.awssdk.services.glacier.model.StatusCode;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GlacierJobServiceImplTest implements WithAssertions {
    private final GlacierClient client = mock(GlacierClient.class);
    private final GlacierJobService service = new GlacierJobServiceImpl(client);

    @DisplayName("startInventoryRetrievalJob")
    @Nested
    class StartInventoryRetrievalJob {
        @Test
        void should_return_job_id() throws TechnicalException {
            // Arrange
            var jobId = "42";
            var response = InitiateJobResponse.builder()
                    .jobId(jobId)
                    .build();
            when(client.initiateJob(any(InitiateJobRequest.class))).thenReturn(response);
            var vault = Vault.builder()
                    .build();

            // Act
            var result = service.startInventoryRetrievalJob(vault);

            // Assert
            assertThat(result).isEqualTo(jobId);
        }

        @Test
        void should_request_inventory_for_vault() throws TechnicalException {
            // Arrange
            var captor = ArgumentCaptor.forClass(InitiateJobRequest.class);
            var vault = Vault.builder()
                    .name("Example_Vault")
                    .build();
            when(client.initiateJob(captor.capture())).thenReturn(InitiateJobResponse.builder().build());

            // Act
            service.startInventoryRetrievalJob(vault);

            // Assert
            assertThat(captor.getValue().vaultName()).isEqualTo(vault.getName());
            assertThat(captor.getValue().jobParameters().type()).isEqualTo("inventory-retrieval");
        }
    }

    @DisplayName("getInventoryRetrievalJobStatus")
    @Nested
    class GetInventoryRetrievalJobStatus {
        @Test
        void should_translate_in_progress_status() throws TechnicalException {
            // Arrange
            var jobId = "42";
            var response = DescribeJobResponse.builder()
                    .statusCode(StatusCode.IN_PROGRESS)
                    .build();
            when(client.describeJob(any(DescribeJobRequest.class))).thenReturn(response);

            // Act
            var result = service.getInventoryRetrievalJobStatus(jobId);

            // Assert
            assertThat(result).hasValue(InventoryRetrievalJobEntity.Status.IN_PROGRESS);
        }

        @Test
        void should_translate_failed_status() throws TechnicalException {
            // Arrange
            var jobId = "42";
            var response = DescribeJobResponse.builder()
                    .statusCode(StatusCode.FAILED)
                    .build();
            when(client.describeJob(any(DescribeJobRequest.class))).thenReturn(response);

            // Act
            var result = service.getInventoryRetrievalJobStatus(jobId);

            // Assert
            assertThat(result).hasValue(InventoryRetrievalJobEntity.Status.FAILED);
        }

        @Test
        void should_translate_succeeded_status() throws TechnicalException {
            // Arrange
            var jobId = "42";
            var response = DescribeJobResponse.builder()
                    .statusCode(StatusCode.SUCCEEDED)
                    .build();
            when(client.describeJob(any(DescribeJobRequest.class))).thenReturn(response);

            // Act
            var result = service.getInventoryRetrievalJobStatus(jobId);

            // Assert
            assertThat(result).hasValue(InventoryRetrievalJobEntity.Status.SUCCEEDED);
        }

        @Test
        void should_return_empty_for_unknown_status() throws TechnicalException {
            // Arrange
            var jobId = "42";
            var response = DescribeJobResponse.builder()
                    .statusCode(StatusCode.UNKNOWN_TO_SDK_VERSION)
                    .build();
            when(client.describeJob(any(DescribeJobRequest.class))).thenReturn(response);

            // Act
            var result = service.getInventoryRetrievalJobStatus(jobId);

            // Assert
            assertThat(result).isNotPresent();
        }
    }
}