package it.mulders.columbia.vaults.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import it.mulders.columbia.shared.ui.StorageNumberFormat;
import it.mulders.columbia.inventory.RetrieveInventoryCommand;
import it.mulders.columbia.vaults.Vault;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.NumberFormat;

@AllArgsConstructor
@Component
@Slf4j
public class VaultDetailView extends FormLayout {
    private static final NumberFormat ITEMS = NumberFormat.getInstance();
    private static final NumberFormat STORAGE = new StorageNumberFormat(2);

    private final ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    void prepare() {
        this.setResponsiveSteps(
                new ResponsiveStep("49em", 2, ResponsiveStep.LabelsPosition.TOP)
        );
    }

    public void setSelectedVault(final Vault vault) {
        getChildren().forEach(this::remove);
        this.addFormItem(item(vault.getArn()), "ARN");
        this.addFormItem(item(vault.getName()), "Name");
        this.addFormItem(item(vault.getArchiveCount(), ITEMS), "Number of archives");
        this.addFormItem(item(vault.getArchiveCount(), STORAGE), "Vault size");

        var fetchButton = new Button("Fetch inventory");
        fetchButton.addClickListener(event -> this.fetchButtonClicked(vault));
        var actions = new HorizontalLayout();
        actions.add(fetchButton);
        this.add(actions, 2);
    }

    public void clear() {
        getChildren().forEach(this::remove);
        add(new Div(new Text("No Vault selected")));
    }

    private void fetchButtonClicked(final Vault vault) {
        applicationEventPublisher.publishEvent(new RetrieveInventoryCommand(this, vault));
        var message = String.format("Inventory of vault %s requested.", vault.getName());
        var notification = new Notification(message, 3000);
        notification.setPosition(Notification.Position.BOTTOM_CENTER);
        notification.open();
    }

    private TextField item(final String value) {
        var item = new TextField();
        item.setValue(value);
        item.setReadOnly(true);
        item.setClassName("large");
        item.setWidth("100%");
        return item;
    }

    private TextField item(final Long value) {
        return item(value, NumberFormat.getInstance());
    }

    private TextField item(final Long value, final NumberFormat format) {
        return item(format.format(value));
    }
}
