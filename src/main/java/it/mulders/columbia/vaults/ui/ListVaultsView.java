package it.mulders.columbia.vaults.ui;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.mulders.columbia.shared.TechnicalException;
import it.mulders.columbia.shared.ui.ErrorMessage;
import it.mulders.columbia.ui.MainView;
import it.mulders.columbia.vaults.Vault;
import it.mulders.columbia.vaults.VaultService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

@AllArgsConstructor
@PageTitle("Vaults")
@Route(value = "vaults", layout = MainView.class)
@Slf4j
public class ListVaultsView extends Div {
    private final VaultService vaultService;
    private final VaultDetailView detailView;

    @PostConstruct
    void prepare() {
        setId("list-vaults");
        try {
            add(prepareGrid());
            add(new VerticalLayout(detailView));
        } catch (TechnicalException te) {
            add(new ErrorMessage("Could not retrieve vaults"));
        }
    }

    private Grid<Vault> prepareGrid() throws TechnicalException {
        var grid = new Grid<Vault>();
        grid.setItems(vaultService.listVaults());
        grid.addColumn(Vault::getName)
                .setHeader("Name")
                .setSortable(true);
        grid.asSingleSelect().addValueChangeListener(this::valueChanged);
        return grid;
    }

    private void valueChanged(final ComponentValueChangeEvent<Grid<Vault>, Vault> event) {

        final Vault selected = event.getHasValue().getValue();
        if (selected != null) {
            detailView.setSelectedVault(selected);
        } else {
            detailView.clear();
        }
    }
}
