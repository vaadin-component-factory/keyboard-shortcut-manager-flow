package org.vaadin.addons.componentfactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route(value="menubar")
public class MenuBarView extends VerticalLayout {

    public MenuBarView() {
        MenuBar menu = new MenuBar();
        MenuItem abbrechen = menu.addItem("cancel", "cancel shortcut (Shift+Esc)", e -> Notification.show("cancel clicked"));
        abbrechen.setId("cancel");
        MenuItem speichern = menu.addItem("save", "save shortcut (Ctrl+S)", e -> Notification.show("save clicked"));
        speichern.setId("save");

        org.vaadin.addons.componentfactory.KeyboardShortcutManager keyboardShortcutManager = new KeyboardShortcutManager(this);
        keyboardShortcutManager.addShortcut(
                new KeyboardShortcut("", KeyboardShortcut.Actions.helpDialog, Key.ALT, Key.F1),
                new KeyboardShortcut("#save", "", KeyboardShortcut.Actions.clickElement, Key.CONTROL, Key.KEY_S),
                new KeyboardShortcut("#cancel", "", KeyboardShortcut.Actions.clickElement, Key.SHIFT, Key.ESCAPE),
                new KeyboardShortcut(".focus-section","", KeyboardShortcut.Actions.focusPreviousElement, Key.CONTROL, Key.SHIFT, Key.ARROW_LEFT),
                new KeyboardShortcut(".focus-section","", KeyboardShortcut.Actions.focusNextElement, Key.CONTROL, Key.SHIFT, Key.ARROW_RIGHT)
        );
        keyboardShortcutManager.subscribe();

        Upload upload = new Upload();
        KeyboardShortcutManager.addSectionPriorityHint(upload);
        add(createSection(upload));

        Upload upload2 = new Upload();
        Button uploadButton = new Button("Upload 2");
        upload2.setUploadButton(uploadButton);
        KeyboardShortcutManager.addSectionPriorityHint(upload2);
        add(createSection(upload2));


        MenuBar menuBar2 = new MenuBar();
        KeyboardShortcutManager.addSectionPriorityHint(menuBar2);
        menuBar2.addItem("Menubar 2");
        add(createSection(menuBar2));


        TextField textField = new TextField("Text field");
        add(createSection(textField));

        MessageInput messageInput = new MessageInput();

        KeyboardShortcutManager.addSectionPriorityHint(messageInput);
        add(createSection(messageInput));

        EmailField emailField = new EmailField("Email field");
        add(createSection(emailField));



        NumberField numberField = new NumberField("Number field");
        numberField.setEnabled(false);
        add(createSection(numberField));

        Button button = new Button("Button");
        add(createSection(button));

        CheckboxGroup<String> checkboxGroup = new CheckboxGroup<>();
        checkboxGroup.setItems("One", "Two", "Three");
        add(createSection(checkboxGroup));

        RadioButtonGroup<String> radioButtonGroup = new RadioButtonGroup<>();
        radioButtonGroup.setItems("One", "Two", "Three");
        add(createSection(radioButtonGroup));

        ListBox<String> listBox = new ListBox<>();
        listBox.setItems("One", "Two", "Three");
        add(createSection(listBox));





        add(menu);
        add(new RouterLink("Navigate to demo view", KeyboardShortcutDemoView.class));

    }

    private Component createSection(Component component) {
        HorizontalLayout section = new HorizontalLayout(component);
        section.addClassNames("focus-section", LumoUtility.Gap.MEDIUM, LumoUtility.AlignItems.BASELINE);
        MenuBar menuBar = new MenuBar();
        menuBar.addItem("Menubar");
        section.add(new Button("Testing"), new TextField("Testing"), new Button("Testing 2"), menuBar);

        return section;
    }
}
