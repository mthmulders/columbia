package it.mulders.columbia.vaults.ui;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
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
import lombok.extern.slf4j.Slf4j;

@PageTitle("Vaults")
@Route(value = "vaults", layout = MainView.class)
@Slf4j
public class ListVaultsView extends Div {
    private final VaultService vaultService;
    private final VerticalLayout detailView = new VerticalLayout();

    public ListVaultsView(final VaultService vaultService) {
        this.vaultService = vaultService;
        setId("list-vaults");
        try {
            add(prepareGrid());
            add(prepareDetailView());
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
        detailView.getChildren().forEach(detailView::remove);

        final Vault selected = event.getHasValue().getValue();
        if (selected != null) {
            detailView.add(new VaultDetailView(selected));
        } else {
            prepareDetailView();
        }
    }

    private Component prepareDetailView() {
        detailView.add(new Div(new Text("No Vault selected")));
        return detailView;
    }
}
