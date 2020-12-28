package it.mulders.columbia.dashboard.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@CssImport("./styles/views/dashboard/counter.css")
class Counter extends VerticalLayout {
    Counter(final String label, final long value) {
        this(label, Long.toString(value));
    }

    Counter(final String label, final int value) {
        this(label, Integer.toString(value));
    }

    Counter(final String label, final String value) {
        setClassName("counter");
        add(label(label));
        add(value(value));
        setWidth("25%");
    }

    private Component label(final String label) {
        var result = new Div(new Text(label));
        result.setClassName("label");
        return result;
    }

    private Component value(final String value) {
        var result = new Div(new Text(value));
        result.setClassName("value");
        return result;
    }
}
