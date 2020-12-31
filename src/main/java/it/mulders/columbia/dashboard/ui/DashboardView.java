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

import java.util.List;

@CssImport("./styles/views/dashboard/dashboard-view.css")
@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
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
        var counter = new Counter("Number of vaults", vaults.size());
        counter.setId("num-vaults");
        return counter;
    }

    private Component archiveCounter(final List<Vault> vaults) {
        var archiveCount = vaults.stream().mapToLong(Vault::getArchiveCount).sum();
        var counter = new Counter("Number of archives", archiveCount);
        counter.setId("num-archives");
        return counter;
    }

    private Component sizeCounter(final List<Vault> vaults) {
        var sizeCount = vaults.stream().mapToLong(Vault::getSizeInBytes).sum();
        var formatted = ByteCountHelper.humanReadableByteCount(sizeCount, 2);
        var counter = new Counter( "Total stored", formatted);
        counter.setId("archive-size");
        return counter;
    }
}
