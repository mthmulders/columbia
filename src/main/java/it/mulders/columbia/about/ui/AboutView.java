package it.mulders.columbia.about.ui;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.mulders.columbia.about.SystemStatusHelper;
import it.mulders.columbia.ui.MainView;
import lombok.AllArgsConstructor;

import java.util.List;

@PageTitle("About")
@Route(value = "about", layout = MainView.class)
public class AboutView extends Div {
    @AllArgsConstructor(staticName = "of")
    static class Item {
        final String label;
        final String value;
    }

    private final SystemStatusHelper systemStatusHelper;

    public AboutView(final SystemStatusHelper systemStatusHelper) {
        this.systemStatusHelper = systemStatusHelper;
        setId("about-view");
        add(prepareGrid());
    }

    private Grid<Item> prepareGrid() {
        var grid = new Grid<Item>();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setItems(buildItems());
        grid.addColumn(i -> i.label).setHeader("");
        grid.addColumn(i -> i.value).setHeader("");
        return grid;
    }

    private List<Item> buildItems() {
        var status = systemStatusHelper.systemStatus();
        return List.of(
            Item.of("Application version", status.getApplicationVersion()),
            Item.of("Available memory", status.getAvailableMem()),
            Item.of("Database", status.getDatabaseInfo()),
            Item.of("Free memory", status.getFreeMem()),
            Item.of("Java version", status.getJavaVersion()),
            Item.of("Operating system", status.getOs())
        );
    }
}
