package it.mulders.columbia.inventory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RetrieveInventoryListener implements ApplicationListener<RetrieveInventoryCommand> {

    @Override
    public void onApplicationEvent(final RetrieveInventoryCommand command) {
        log.info("Will schedule retrieval of inventory for Vault {}", command.getVault());
    }
}
