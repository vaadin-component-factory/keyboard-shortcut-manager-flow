package org.vaadin.addons.componentfactory;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route(value="menubar")
public class MenuBarView extends VerticalLayout {

    public MenuBarView() {
        MenuBar menu = new MenuBar();
        MenuItem abbrechen = menu.addItem("cancel", "cancel shortcut (Shift+Esc)", e -> Notification.show("cancel clicked"));
        abbrechen.setId("cancel");
        MenuItem speichern = menu.addItem("save", "save shortcut (Ctrl+S)", e -> Notification.show("save clicked"));
        speichern.setId("save");

        org.vaadin.addons.componentfactory.KeyboardShortcutManager keyboardShortcutManager = new KeyboardShortcutManager(this);
        keyboardShortcutManager.addShortcut(new KeyboardShortcut("#save", "", KeyboardShortcut.Actions.clickElement, Key.CONTROL, Key.KEY_S),
                new KeyboardShortcut("#cancel", "", KeyboardShortcut.Actions.clickElement, Key.SHIFT, Key.ESCAPE));
        keyboardShortcutManager.subscribe();
        FlexLayout flexLayout = new FlexLayout();
        flexLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);

        add(menu);
        add(new RouterLink("Navigate to demo view", KeyboardShortcutDemoView.class));

    }
}
