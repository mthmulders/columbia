package it.mulders.columbia.vaults.ui;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Emphasis;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.mulders.columbia.shared.TechnicalException;
import it.mulders.columbia.shared.ui.BytesNumberFormat;
import it.mulders.columbia.shared.ui.ErrorMessage;
import it.mulders.columbia.ui.MainView;
import it.mulders.columbia.vaults.Vault;
import it.mulders.columbia.vaults.VaultService;
import lombok.extern.slf4j.Slf4j;

import java.text.NumberFormat;

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
            var formLayout = new FormLayout();
            formLayout.add(detailRow("ARN", selected.getArn()));
            formLayout.add(detailRow("Name", selected.getName()));
            formLayout.add(detailRow("Number of archives", selected.getArchiveCount()));
            formLayout.add(detailRow("Vault size", selected.getSizeInBytes(), new BytesNumberFormat(2)));
            detailView.add(formLayout);
        }
    }

    private Component detailRow(final String label, final Long value) {
        return detailRow(label, value, NumberFormat.getInstance());
    }

    private Component detailRow(final String label, final Long value, final NumberFormat format) {
        return detailRow(label, format.format(value));
    }

    private Component detailRow(final String label, final String value) {
        var detail = new TextField(label);
        detail.setValue(value);
        detail.setReadOnly(true);
        detail.setClassName("large");
        return detail;
    }

    private Component prepareDetailView() {
        detailView.add(new Div(new Text("No Vault selected")));
        return detailView;
    }
}
