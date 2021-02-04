package it.mulders.columbia.vaults.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import it.mulders.columbia.shared.ui.BytesNumberFormat;
import it.mulders.columbia.vaults.Vault;

import java.text.NumberFormat;

public class VaultDetailView extends FormLayout {

    public VaultDetailView(final Vault vault) {
        this.add(detailRow("ARN", vault.getArn()));
        this.add(detailRow("Name", vault.getName()));
        this.add(detailRow("Number of archives", vault.getArchiveCount()));
        this.add(detailRow("Vault size", vault.getSizeInBytes(), new BytesNumberFormat(2)));
    }

    private Component detailRow( final String label, final Long value) {
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
}
