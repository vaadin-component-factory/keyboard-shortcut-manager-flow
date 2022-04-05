package org.vaadin.artur.axainputtext;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.Route;

@Route("")
public class View extends VerticalLayout {
    private final VerticalLayout verticalLayout;

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

        verticalLayout = new VerticalLayout(name, address);
        verticalLayout.setId("sub");
        verticalLayout.setMargin(false);
        verticalLayout.setPadding(false);
        verticalLayout.setSpacing(false);


        TextField name2 = new TextField("Rouge name");
        name2.setInvalid(true);

        TextField address2 = new TextField("Rouge address");
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

        verticalLayout1.setSpacing(false);
        verticalLayout1.add(verticalLayout, new H6("Person 2"), name2, address2);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        KeyboardShortcutManager keyboardShortcutManager = new KeyboardShortcutManager(this);
        keyboardShortcutManager
                .addShortcut(new KeyboardShortcut(KeyboardShortcut.Actions.focusNextInvalidField, Key.ALT, Key.F8))
                .addShortcut(new KeyboardShortcut(KeyboardShortcut.Actions.clearAllFields, Key.CONTROL, Key.KEY_K))
                .addShortcut(new KeyboardShortcut(KeyboardShortcut.Actions.focusPreviousInvalidField, Key.ALT, Key.SHIFT, Key.F8));

        keyboardShortcutManager.subscribe();
    }


    private static class Person {
        private String name;
        private String address;

        public Person() {
        }

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
