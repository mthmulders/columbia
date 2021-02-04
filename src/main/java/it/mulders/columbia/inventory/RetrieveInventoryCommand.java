package it.mulders.columbia.inventory;

import it.mulders.columbia.vaults.Vault;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class RetrieveInventoryCommand extends ApplicationEvent {
    private final Vault vault;

    public RetrieveInventoryCommand(final Object source, final Vault vault) {
        super(source);
        this.vault = vault;
    }
}
