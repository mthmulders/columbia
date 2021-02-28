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
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.DescribeJobRequest;
import software.amazon.awssdk.services.glacier.model.DescribeJobResponse;
import software.amazon.awssdk.services.glacier.model.GetJobOutputRequest;
import software.amazon.awssdk.services.glacier.model.GetJobOutputResponse;
import software.amazon.awssdk.services.glacier.model.InitiateJobRequest;
import software.amazon.awssdk.services.glacier.model.InitiateJobResponse;
import software.amazon.awssdk.services.glacier.model.ResourceNotFoundException;
import software.amazon.awssdk.services.glacier.model.StatusCode;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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
            var job = InventoryRetrievalJobEntity.builder()
                    .jobId("42")
                    .vaultName("Example")
                    .build();
            var response = DescribeJobResponse.builder()
                    .statusCode(StatusCode.IN_PROGRESS)
                    .build();
            when(client.describeJob(any(DescribeJobRequest.class))).thenReturn(response);

            // Act
            var result = service.getInventoryRetrievalJobStatus(job);

            // Assert
            assertThat(result).hasValue(InventoryRetrievalJobEntity.Status.IN_PROGRESS);
        }

        @Test
        void should_translate_failed_status() throws TechnicalException {
            // Arrange
            var job = InventoryRetrievalJobEntity.builder()
                    .jobId("42")
                    .vaultName("Example")
                    .build();
            var response = DescribeJobResponse.builder()
                    .statusCode(StatusCode.FAILED)
                    .build();
            when(client.describeJob(any(DescribeJobRequest.class))).thenReturn(response);

            // Act
            var result = service.getInventoryRetrievalJobStatus(job);

            // Assert
            assertThat(result).hasValue(InventoryRetrievalJobEntity.Status.FAILED);
        }

        @Test
        void should_translate_succeeded_status() throws TechnicalException {
            // Arrange
            var job = InventoryRetrievalJobEntity.builder()
                    .jobId("42")
                    .vaultName("Example")
                    .build();
            var response = DescribeJobResponse.builder()
                    .statusCode(StatusCode.SUCCEEDED)
                    .build();
            when(client.describeJob(any(DescribeJobRequest.class))).thenReturn(response);

            // Act
            var result = service.getInventoryRetrievalJobStatus(job);

            // Assert
            assertThat(result).hasValue(InventoryRetrievalJobEntity.Status.SUCCEEDED);
        }

        @Test
        void should_return_empty_for_unknown_status() throws TechnicalException {
            // Arrange
            var job = InventoryRetrievalJobEntity.builder()
                    .jobId("42")
                    .vaultName("Example")
                    .build();
            var response = DescribeJobResponse.builder()
                    .statusCode(StatusCode.UNKNOWN_TO_SDK_VERSION)
                    .build();
            when(client.describeJob(any(DescribeJobRequest.class))).thenReturn(response);

            // Act
            var result = service.getInventoryRetrievalJobStatus(job);

            // Assert
            assertThat(result).isNotPresent();
        }

        @Test
        void should_return_expired_for_no_job() throws TechnicalException {
            // Arrange
            var job = InventoryRetrievalJobEntity.builder()
                    .jobId("42")
                    .vaultName("Example")
                    .build();
            when(client.describeJob(any(DescribeJobRequest.class))).thenThrow(ResourceNotFoundException.class);

            // Act
            var result = service.getInventoryRetrievalJobStatus(job);

            // Assert
            assertThat(result).hasValue(InventoryRetrievalJobEntity.Status.EXPIRED);
        }
    }

    @DisplayName("getInventoryRetrievalJobOutput")
    @Nested
    class GetInventoryRetrievalJobOutput {
        @Test
        void should_parse_inventory_json() throws TechnicalException {
            // Arrange
            var job = InventoryRetrievalJobEntity.builder()
                    .jobId("42")
                    .vaultName("Example_Vault")
                    .build();
            var data = """
                    { "VaultARN":"arn:aws:glacier:eu-central-1:460046808775:vaults/Example_Vault",
                      "InventoryDate":"2021-02-02T13:26:19Z",
                      "ArchiveList": [
                        { "ArchiveId":"EQsa8jBfaGH-yKf5NIQd5ZW8c3KjPO7YXXPWJCDGeQJiTQgz2Cw7On09EHNIX0mDwMuwP7WIbuSlqb0WpaIyAGO-snMoyvgVftxgoIH8_tsevhgdmfloP7YFKk2bz7W2NAE5MxKHEA",
                          "ArchiveDescription":"{\\"path\\": \\"/file1.txt\\", \\"type\\": \\"file\\"}",
                          "CreationDate":"2021-01-03T13:48:33Z",
                          "Size":528,
                          "SHA256TreeHash":"624d68f02172c23d8a22d0579d183b2597c9211bcd2df6e8fd84937689af5062"
                        },
                        { "ArchiveId":"PTehft2gBdoQ1PxbLejw8ceqBbupFGsL5WNZRA2cvXJmamhFnd9IGbA7Kjgn_FEdPWJhgqk_IiQpjdUdzufhLY1JOpQYESt_ZUrQ_myRKdo_UAZjwInP0TKFO5sSp5gF-POYiLELQQ",
                          "ArchiveDescription":"{\\"path\\": \\"/file2.txt\\", \\"type\\": \\"file\\"}",
                          "CreationDate":"2021-01-03T13:48:34Z",
                          "Size":528,
                          "SHA256TreeHash":"0b9d98845e3d87cb02ac4eece86cbb2b6f5377242785544527da3965f1dc166e"
                        }
                      ]
                   }
                """;
            var bytes = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
            var response = new ResponseInputStream<>(
                    GetJobOutputResponse.builder().build(),
                    AbortableInputStream.create(bytes)
            );
            when(client.getJobOutput(any(GetJobOutputRequest.class))).thenReturn(response);

            // Act
            var result = service.getInventoryRetrievalJobOutput(job);

            // Assert
            assertThat(result).isPresent().hasValueSatisfying(inventory -> {
                assertThat(inventory.getVaultARN()).isEqualTo("arn:aws:glacier:eu-central-1:460046808775:vaults/Example_Vault");
                assertThat(inventory.getInventoryDate()).isEqualToIgnoringNanos(LocalDateTime.parse("2021-02-02T13:26:19"));
                assertThat(inventory.getArchives()).hasSize(2);
                assertThat(inventory.getArchives()).allSatisfy(archive -> {
                    assertThat(archive.getSize()).isEqualTo(528);
                    assertThat(archive.getDescription().getType()).isEqualTo("file");
                });
            });
        }
    }
}