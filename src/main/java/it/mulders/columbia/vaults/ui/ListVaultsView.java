package it.mulders.columbia.vaults.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.mulders.columbia.shared.TechnicalException;
import it.mulders.columbia.shared.ui.BytesNumberFormat;
import it.mulders.columbia.shared.ui.ErrorMessage;
import it.mulders.columbia.ui.MainView;
import it.mulders.columbia.vaults.Vault;
import it.mulders.columbia.vaults.VaultService;

@PageTitle( "Vaults")
@Route(value = "vaults", layout = MainView.class)
public class ListVaultsView extends Div {
    private final VaultService vaultService;

    public ListVaultsView(final VaultService vaultService) {
        this.vaultService = vaultService;
        setId("list-vaults");
        try {
            add(prepareGrid());
        } catch (TechnicalException te) {
            add(new ErrorMessage("Could not retrieve vaults"));
        }
    }

    private Grid<Vault> prepareGrid() throws TechnicalException {
        var grid = new Grid<Vault>();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setItems(vaultService.listVaults());
        grid.addColumn(Vault::getArn)
                .setHeader("ARN");
        grid.addColumn(Vault::getName)
                .setHeader("Name")
                .setSortable(true);
        grid.addColumn(Vault::getArchiveCount)
                .setHeader("# Archives")
                .setSortable(true);
        grid.addColumn(new NumberRenderer<>(Vault::getSizeInBytes, new BytesNumberFormat(2)))
                .setHeader("Size")
                .setComparator(Vault::getSizeInBytes)
                .setSortable(true);
        return grid;
    }
}
