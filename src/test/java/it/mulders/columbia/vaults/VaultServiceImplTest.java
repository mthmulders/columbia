package it.mulders.columbia.vaults;

import it.mulders.columbia.shared.TechnicalException;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.DescribeVaultOutput;
import software.amazon.awssdk.services.glacier.model.ListVaultsRequest;
import software.amazon.awssdk.services.glacier.model.ListVaultsResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class VaultServiceImplTest implements WithAssertions {
    private final GlacierClient client = mock(GlacierClient.class);
    private final VaultServiceImpl service = new VaultServiceImpl(client);

    @DisplayName("listVaults")
    @Nested
    class ListVaults {
        @Test
        void should_convert_response_with_data() throws TechnicalException {
            // Arrange
            var response = ListVaultsResponse.builder()
                    .vaultList(DescribeVaultOutput.builder()
                            .vaultARN("arn:aws:glacier:eu-central-1:460046808775:vaults/Example_Vault")
                            .vaultName("Example_Vault")
                            .numberOfArchives(3L)
                            .build())
                    .build();
            when(client.listVaults(any(ListVaultsRequest.class))).thenReturn(response);

            // Act
            var result = service.listVaults();

            // Assert
            assertThat(result).containsOnly(Vault.builder()
                    .arn("arn:aws:glacier:eu-central-1:460046808775:vaults/Example_Vault")
                    .archiveCount(3)
                    .name("Example_Vault")
                    .build()
            );
        }

        @Test
        void should_return_empty_list_on_response_without_data() throws TechnicalException {
            // Arrange
            var response = ListVaultsResponse.builder().build();
            when(client.listVaults(any(ListVaultsRequest.class))).thenReturn(response);

            // Act
            var result = service.listVaults();

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        void should_throw_technical_exception_on_api_failure() {
            // Arrange
            when(client.listVaults(any(ListVaultsRequest.class))).thenThrow(SdkException.class);

            // Act
            assertThatThrownBy(service::listVaults)

                // Assert
                .isInstanceOf(TechnicalException.class);
        }
    }
}