package it.mulders.columbia.shared.ui;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;

@CssImport( "./styles/views/shared/error-message.css")
public class ErrorMessage extends Div {
    public ErrorMessage(final String message) {
        this.add(new Text(message));
        setClassName("error-message");
    }
}
