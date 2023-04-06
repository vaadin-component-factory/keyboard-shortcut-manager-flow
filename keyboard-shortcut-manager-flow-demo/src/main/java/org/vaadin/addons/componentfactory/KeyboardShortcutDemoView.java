package org.vaadin.addons.componentfactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.vaadin.componentfactory.lookupfield.LookupField;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("")
@CssImport(value = "demo-styles.css")
public class KeyboardShortcutDemoView extends VerticalLayout {
    private final int personCount = 3;
    private HashMap<String, TextField> names = new HashMap<String, TextField>();
    private HashMap<String, TextField> addresses = new HashMap<String, TextField>();

    public KeyboardShortcutDemoView() {
        this.setId("main");
        H3 shortcutManagerH3 = new H3("VCF Keyboard Shortcut Manager");
        shortcutManagerH3.getStyle().set("margin", "0");
        add(shortcutManagerH3);
        setSpacing(false);
        getStyle().set("background", "#F0F0F0");
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.addClassName("shadow-s");
        verticalLayout.getStyle().set("background", "#FFFFFF");
        addAndExpand(verticalLayout);

        for (int i = 1; i <= personCount; i++) {
            TextField name = new TextField("name");
            TextField address = new TextField("address");
            VerticalLayout personContainer = new VerticalLayout(new H6("Person " + i));
            List<Person> people = Arrays.asList(
                    new Person("Nicolaus Copernicus", "123 Street Rd."),
                    new Person("Galileo Galilei", "456 Highway Ave."),
                    new Person("Johannes Kepler", "789 Road St."));

            personContainer.setId("person-" + i);
            personContainer.addClassName("person");
            personContainer.setMargin(false);
            personContainer.setPadding(false);
            personContainer.setSpacing(false);

            String nameId = "name-" + i;
            String addressId = "address-" + i;
            name.setId(nameId);
            address.setId(addressId);
            names.put(nameId, name);
            addresses.put(addressId, address);

            Binder<Person> binder = new Binder<>();
            binder.forField(name)
                    .asRequired(new StringLengthValidator("Must be at least 3 Characters long", 3, 10))
                    .bind(Person::getName, Person::setName);
            binder.forField(address).asRequired(new StringLengthValidator("Must be at least 3 Characters long", 3, 10))
                    .bind(Person::getAddress, Person::setAddress);
            binder.validate();

            if (i == 1) {
                name.setEnabled(false);
            }

            if (i == personCount) {
                LookupField<Person> lookupField = new LookupField<>(Person.class);
                lookupField.setDataProvider(DataProvider.ofCollection(people));
                lookupField.setHeader("Person Search");
                lookupField.setGridWidth("900px");
                personContainer.add(lookupField);
            }

            personContainer.add(name);
            personContainer.add(address);

            if (i == personCount - 1) {
                Grid<Person> grid = new Grid<>();
                grid.setItems(people);
                grid.addColumn(Person::getName).setHeader("Name");
                grid.addColumn(Person::getAddress).setHeader("Address");
                grid.setAllRowsVisible(true);
                grid.getStyle()
                        .set("margin-top", "var(--lumo-space-m)")
                        .set("max-width", "400px");
                personContainer.add(grid);
            }

            verticalLayout.add(personContainer);
        }

        Button button = new Button("Submit");
        button.setId("submit");
        button.addClickListener(e -> {
            Notification.show("Button clicked.", 2000, Notification.Position.BOTTOM_START);
        });

        verticalLayout.add(button);
        verticalLayout.setSpacing(false);

        verticalLayout.add(new RouterLink("Navigate to another view", MenuBarView.class));

        KeyboardShortcutManager keyboardShortcutManager = createKeyboardShortcutManager();
        keyboardShortcutManager.subscribe();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
    }

    private KeyboardShortcutManager createKeyboardShortcutManager() {
        KeyboardShortcutManager keyboardShortcutManager = new KeyboardShortcutManager(this);
        KeyboardShortcut[] shortcuts = new KeyboardShortcut[] {
                new KeyboardShortcut("", KeyboardShortcut.Actions.helpDialog, Key.ALT, Key.F1),
                new KeyboardShortcut("", KeyboardShortcut.Actions.focusNextInvalidField, Key.ALT, Key.F8),
                new KeyboardShortcut("", KeyboardShortcut.Actions.focusPreviousInvalidField, Key.ALT, Key.SHIFT,
                        Key.F8),
                new KeyboardShortcut("#person-1", KeyboardShortcut.Actions.clearAllFields, KeyboardShortcut.MOD,
                        Key.KEY_K),
                new KeyboardShortcut("#submit", "", KeyboardShortcut.Actions.clickElement,
                        KeyboardShortcut.MOD, Key.KEY_B),
                new KeyboardShortcut("#submit", "", KeyboardShortcut.Actions.clickElement,
                        Key.CONTROL, Key.SHIFT, Key.KEY_B),
                new KeyboardShortcut("#address-2", "#person-2", KeyboardShortcut.Actions.focusElement,
                        KeyboardShortcut.MOD, Key.KEY_F),
                new KeyboardShortcut(".person", "", KeyboardShortcut.Actions.focusNextElement,
                        Key.CONTROL, Key.SHIFT, Key.ARROW_RIGHT),
                new KeyboardShortcut(".person", "", KeyboardShortcut.Actions.focusPreviousElement,
                        Key.CONTROL, Key.SHIFT, Key.ARROW_LEFT)
        };

        keyboardShortcutManager.addShortcut(shortcuts);
        return keyboardShortcutManager;
    }

    public static class Person {
        private String name;
        private String address;

        public Person(String name, String address) {
            setName(name);
            setAddress(address);
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
