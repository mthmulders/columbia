package it.mulders.columbia.vaults;

import it.mulders.columbia.shared.TechnicalException;

import java.util.List;

/**
 * Interface for working with AWS Glacier Vaults.
 */
public interface VaultService {
    /**
     * This operation lists all vaults owned by the calling user’s account.
     * @return Vaults available in the AWS account.
     * @throws TechnicalException When the AWS API call failed.
     */
    List<Vault> listVaults() throws TechnicalException;
}
