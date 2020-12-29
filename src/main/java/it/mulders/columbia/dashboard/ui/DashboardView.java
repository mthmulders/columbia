package it.mulders.columbia.dashboard.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import it.mulders.columbia.shared.TechnicalException;
import it.mulders.columbia.shared.ui.ByteCountHelper;
import it.mulders.columbia.shared.ui.ErrorMessage;
import it.mulders.columbia.ui.MainView;
import it.mulders.columbia.vaults.Vault;
import it.mulders.columbia.vaults.VaultService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@CssImport("./styles/views/dashboard/dashboard-view.css")
@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@Slf4j
public class DashboardView extends HorizontalLayout {
    public DashboardView(final VaultService vaultService) {
        setId("dashboard-view");

        try {
            var vaults = vaultService.listVaults();
            add(vaultCounter(vaults));
            add(archiveCounter(vaults));
            add(sizeCounter(vaults));
        } catch (TechnicalException te) {
            var error = new ErrorMessage("Could not retrieve vaults");
            add(error);
            setFlexGrow(4, error);
        }
    }

    private Component vaultCounter(final List<Vault> vaults) {
        return new Counter("Number of vaults", vaults.size());
    }

    private Component archiveCounter(final List<Vault> vaults) {
        var archiveCount = vaults.stream().mapToLong(Vault::getArchiveCount).sum();
        return new Counter("Number of archives", archiveCount);
    }

    private Component sizeCounter(final List<Vault> vaults) {
        var sizeCount = vaults.stream().mapToLong(Vault::getSizeInBytes).sum();
        var formatted = ByteCountHelper.humanReadableByteCount(sizeCount, 2);
        log.debug("All vaults together contain {} bytes, formatted as {}", sizeCount, formatted);
        return new Counter( "Total stored", formatted);
    }
}
