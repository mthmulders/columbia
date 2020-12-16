package it.mulders.columbia.dashboard.ui;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import it.mulders.columbia.ui.MainView;

@CssImport( "./styles/views/dashboard/dashboard-view.css")
@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
public class DashboardView extends HorizontalLayout {
    public DashboardView() {
        setId("dashboard-view");
    }
}
