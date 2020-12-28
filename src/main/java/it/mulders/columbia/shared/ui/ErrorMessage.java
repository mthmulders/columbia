package it.mulders.columbia.shared.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@CssImport("./styles/views/shared/error-message.css")
public class ErrorMessage extends HorizontalLayout {
    public ErrorMessage(final String message) {
        this.add(VaadinIcon.EXCLAMATION_CIRCLE_O.create());
        this.add(new Text(message));
        setClassName("error-message");
    }
}
