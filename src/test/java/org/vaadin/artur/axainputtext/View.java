package org.vaadin.artur.axainputtext;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("")
public class View extends VerticalLayout {

    public View() {
        this.setId("meow");
        TextField name = new TextField("name");
        TextField address = new TextField("address");
        address.setInvalid(true);

        VerticalLayout verticalLayout = new VerticalLayout(name, address);
        verticalLayout.setId("wow");


        TextField name2 = new TextField("Rouge name");
        name2.setInvalid(true);

        TextField address2 = new TextField("Rouge address");
        address2.setInvalid(true);

        add(verticalLayout, name2, address2);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        KeyboardShortcutManager keyboardShortcutManager = new KeyboardShortcutManager(this);
        keyboardShortcutManager
                .addShortcut(new KeyboardShortcut(KeyboardShortcut.Actions.focusNextInvalidField, Key.ALT, Key.F8))
                .addShortcut(new KeyboardShortcut(KeyboardShortcut.Actions.focusPreviousInvalidField, Key.ALT, Key.SHIFT, Key.F8));

        System.out.println("this.getUI().get() = " + this.getId());

        keyboardShortcutManager.subscribe();
    }
}
