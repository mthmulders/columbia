package it.mulders.columbia.vaults;

import it.mulders.columbia.shared.TechnicalException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.glacier.GlacierClient;
import software.amazon.awssdk.services.glacier.model.DescribeVaultOutput;
import software.amazon.awssdk.services.glacier.model.ListVaultsRequest;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor(onConstructor = @__({ @Autowired }))
@Slf4j
public class VaultServiceImpl implements VaultService {
    private final GlacierClient client;

    @Override
    public List<Vault> listVaults() throws TechnicalException {
        return listVaults(null);
    }

    List<Vault> listVaults(final String marker) throws TechnicalException {
        var request = ListVaultsRequest.builder()
                .marker(marker)
                .build();

        try {
            log.info("List Glacier vaults: marker={}", marker);
            var response = client.listVaults(request);

            if (response.hasVaultList()) {
                var result = new ArrayList<Vault>();

                response.vaultList().stream()
                        .map(this::mapToVault)
                        .forEach(result::add);

                // By default, this operation returns up to 10 items per request.
                // If the marker is present, it indicates there is more data to retrieve.
                if (response.marker() != null) {
                    result.addAll(listVaults(response.marker()));
                }

                return result;
            }

            log.warn("Response contained no vaults");
            return List.of();
        } catch (SdkException e) {
            log.error("Cannot list vaults in Amazon account - communication error: marker={}, error={}",
                    marker, e.getMessage(), e);
            throw new TechnicalException("Cannot list vaults in Amazon account", e);
        }
    }

    private Vault mapToVault(final DescribeVaultOutput describeVaultOutput) {
        return Vault.builder()
                .archiveCount(describeVaultOutput.numberOfArchives())
                .arn(describeVaultOutput.vaultARN())
                .name(describeVaultOutput.vaultName())
                .sizeInBytes(describeVaultOutput.sizeInBytes())
                .build();
    }
}
