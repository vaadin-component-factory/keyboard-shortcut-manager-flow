package org.vaadin.componentfactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.littemplate.LitTemplate;

import elemental.json.Json;
import elemental.json.JsonArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A Designer generated component for the keyboard-shortcut-manager template.
 * <p>
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("keyboard-shortcut-manager-flow")
@JsModule("./keyboard-shortcut-manager-flow.ts")
@NpmPackage(value = "@vaadin-component-factory/keyboard-shortcut-manager", version = "23.0.4")
@JsModule("@vaadin-component-factory/keyboard-shortcut-manager")
public class KeyboardShortcutManager extends LitTemplate {

    private final Component component;
    private Boolean helpDialog = true;
    private List<KeyboardShortcut> keyboardShortcuts = new ArrayList<>();

    /**
     * Creates a new KeyboardShortcutManager.
     */
    public KeyboardShortcutManager(Component component) {
        this.component = component;
    }

    public KeyboardShortcutManager(Component component, Boolean helpDialog) {
        this(component);
        this.helpDialog = helpDialog;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getElement().setProperty("helpDialog", helpDialog);
    }

    public void subscribe() {
        getElement().setPropertyJson("shortcuts", listToJson(keyboardShortcuts));
        component.getUI().ifPresent(ui -> ui.add(this));
    }

    public KeyboardShortcutManager addShortcut(KeyboardShortcut ...keyboardShortcuts) {
        for (KeyboardShortcut s : keyboardShortcuts) {
            this.keyboardShortcuts.add(s);
        }
        return this;
    }

    public static JsonArray listToJson(List<?> list) {
        Objects.requireNonNull(list, "Cannot convert null to JSON");

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return (JsonArray) Json.instance().parse(objectMapper.writeValueAsString(list));
        } catch (JsonProcessingException var2) {
            throw new RuntimeException("Error converting list to JSON", var2);
        }
    }
}
