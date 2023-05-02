package org.vaadin.addons.componentfactory;


import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("test")
public class TestNavigationView extends VerticalLayout {

    public TestNavigationView() {

        add(new Span("Nothing to see here"), new RouterLink("Back to demo view", KeyboardShortcutDemoView.class));
    }
}
