package it.mulders.columbia.about.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import it.mulders.columbia.ui.MainView;

@Route(value = "about", layout = MainView.class)
@PageTitle("About")
public class AboutView extends Div {
    public AboutView() {
        setId("about-view");
        add(new Text("Content placeholder"));
    }
}
