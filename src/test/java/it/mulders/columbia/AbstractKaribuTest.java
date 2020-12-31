package it.mulders.columbia;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.github.mvysny.kaributesting.v10.spring.MockSpringServlet;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import kotlin.jvm.functions.Function0;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import static java.util.stream.Collectors.joining;

/**
 * Base-class for tests that leverage Karibu-testing for interacting with the Vaadin UI.
 */
public abstract class AbstractKaribuTest {
    public static final Routes routes = new Routes().autoDiscoverViews("it.mulders.columbia");

    @Autowired
    protected ApplicationContext context;

    // If this method was annotated @BeforeEach, it would run too early: _before_ mocked beans where set up.
    protected void prepareKaribu() {
        final Function0<UI> uiFactory = UI::new;
        MockVaadin.setup(uiFactory, new MockSpringServlet(routes, context, uiFactory));
    }

    protected String getText(final Component component) {
        return component.getChildren()
                .filter(Div.class::isInstance)
                .map(Div.class::cast)
                .map(Div::getText)
                .collect(joining(" "));
    }

    @AfterEach
    void tearDown() {
        MockVaadin.tearDown();
    }
}
