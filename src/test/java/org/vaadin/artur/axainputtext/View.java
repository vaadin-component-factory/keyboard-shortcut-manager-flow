package org.vaadin.artur.axainputtext;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.Route;

@Route("")
@CssImport(value = "view-styles.css")
public class View extends VerticalLayout {
    private final VerticalLayout person1Container;
    private final VerticalLayout person2Container;
    private TextField address2;

    public View() {
        this.setId("main");
        H3 shortcutManagerH3 = new H3("VCF Keyboard Shortcut Manager");
        shortcutManagerH3.getStyle().set("margin", "0");
        add(shortcutManagerH3);
        setSpacing(false);
        getStyle().set("background", "#F0F0F0");
        VerticalLayout verticalLayout1 = new VerticalLayout();
        verticalLayout1.addClassName("shadow-s");
        verticalLayout1.getStyle().set("background", "#FFFFFF");
        addAndExpand(verticalLayout1);

        TextField name = new TextField("name");
        TextField address = new TextField("address");

        person1Container = new VerticalLayout(new H6("Person 1"), name, address);
        person1Container.setId("person-1");
        person1Container.addClassName("person");
        person1Container.setMargin(false);
        person1Container.setPadding(false);
        person1Container.setSpacing(false);

        TextField name2 = new TextField("name");
        name2.setInvalid(true);

        address2 = new TextField("address");
        address2.setId("address-2");
        address2.setInvalid(true);

        Binder<Person> binder = new Binder<>();
        binder.forField(name)
                .asRequired(new StringLengthValidator("Must be at least 3 Characters long", 3, 10))
                .bind(Person::getName, Person::setName);
        binder.forField(address).asRequired(new StringLengthValidator("Must be at least 3 Characters long", 3, 10))
                .bind(Person::getAddress, Person::setAddress);
        binder.validate();

        Binder<Person> binder2 = new Binder<>();
        binder2.forField(name2)
                .asRequired(new StringLengthValidator("Must be at least 3 Characters long", 3, 10))
                .bind(Person::getName, Person::setName);
        binder2.forField(address2).asRequired(new StringLengthValidator("Must be at least 3 Characters long", 3, 10))
                .bind(Person::getAddress, Person::setAddress);
        binder2.validate();

        person2Container = new VerticalLayout(new H6("Person 2"), name2, address2);
        person2Container.setId("person-2");
        person2Container.addClassName("person");
        person2Container.setMargin(false);
        person2Container.setPadding(false);
        person2Container.setSpacing(false);
        person2Container.getElement();

        verticalLayout1.setSpacing(false);
        verticalLayout1.getElement().setAttribute("tabindex", "0");
        verticalLayout1.add(person1Container, person2Container);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        KeyboardShortcutManager keyboardShortcutManager = new KeyboardShortcutManager(this);
        keyboardShortcutManager
                .addShortcut(new KeyboardShortcut("Open help Dialog.", "", KeyboardShortcut.Actions.helpDialog.toString(), Key.CONTROL, Key.SHIFT, Key.SLASH))
                .addShortcut(new KeyboardShortcut("Focus next invalid field.", "", KeyboardShortcut.Actions.focusNextInvalidField, Key.ALT, Key.F8))
                .addShortcut(new KeyboardShortcut("Focus previous invalid field.", "", KeyboardShortcut.Actions.focusPreviousInvalidField, Key.ALT, Key.SHIFT, Key.F8))
                .addShortcut(new KeyboardShortcut("Clear all fields (only Person 1).", "person-1", KeyboardShortcut.Actions.clearAllFields, Key.CONTROL, Key.KEY_K))
                .addShortcut(new KeyboardShortcut("Focus element: #" + address2.getId().get(), "person-2", KeyboardShortcut.Actions.focusElement + address2.getId().get(), Key.CONTROL, Key.KEY_F));

        keyboardShortcutManager.subscribe();
    }


    private static class Person {
        private String name;
        private String address;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
