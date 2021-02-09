package it.mulders.columbia.jobs.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.renderer.BasicRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.mulders.columbia.jobs.entity.InventoryRetrievalJobEntity;
import it.mulders.columbia.jobs.repository.InventoryRetrievalJobRepository;
import it.mulders.columbia.shared.TechnicalException;
import it.mulders.columbia.shared.ui.ErrorMessage;
import it.mulders.columbia.shared.ui.SpringDataProvider;
import it.mulders.columbia.ui.MainView;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@PageTitle("Background Jobs")
@Route(value = "background-jobs", layout = MainView.class)
@Slf4j
public class ListBackgroundJobsView extends Div {
    private final InventoryRetrievalJobRepository repository;

    @PostConstruct
    void prepare() {
        setId("list-background-jobs");
        try {
            add(prepareGrid());
        } catch ( TechnicalException te) {
            add(new ErrorMessage("Could not retrieve vaults"));
        }
    }

    static class StatusRenderer extends BasicRenderer<InventoryRetrievalJobEntity, InventoryRetrievalJobEntity.Status> {
        protected StatusRenderer(final ValueProvider<InventoryRetrievalJobEntity, InventoryRetrievalJobEntity.Status> valueProvider) {
            super(valueProvider);
        }

        @Override
        protected String getFormattedValue(final InventoryRetrievalJobEntity.Status status) {
            return switch (status) {
                case IN_PROGRESS -> "In Progress";
                case DONE -> "Done";
            };
        }
    }

    protected Grid<InventoryRetrievalJobEntity> prepareGrid() throws TechnicalException {
        var formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy, HH:mm:ss");

        var grid = new Grid<InventoryRetrievalJobEntity>();
        grid.setItems(new SpringDataProvider<>(repository));
        grid.addColumn(new LocalDateTimeRenderer<>(InventoryRetrievalJobEntity::getStarted, formatter))
                .setHeader("Initiated")
                .setSortProperty("started");
        grid.addColumn(new StatusRenderer(InventoryRetrievalJobEntity::getStatus))
                .setHeader("Status")
                .setSortable(false);
        grid.addColumn(InventoryRetrievalJobEntity::getVaultName)
                .setHeader("Vault")
                .setSortable(false);
        return grid;
    }
}
