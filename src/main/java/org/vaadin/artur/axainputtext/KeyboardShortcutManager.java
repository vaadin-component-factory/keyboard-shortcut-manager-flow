package org.vaadin.artur.axainputtext;

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
@Tag("keyboard-shortcut-manager")
@JsModule("./keyboard-shortcut-manager.ts")
@NpmPackage(value = "@vaadin-component-factory/keyboard-shortcut-manager", version = "1.0.7-legacy-1")
@JsModule("@vaadin-component-factory/keyboard-shortcut-manager")
public class KeyboardShortcutManager extends LitTemplate {

    private final Component component;
    private final String target;
    private List<KeyboardShortcut> keyboardShortcuts = new ArrayList<>();

    /**
     * Creates a new KeyboardShortcutManager.
     */
    public KeyboardShortcutManager(Component component) {
        // You can initialise any data required for the connected UI components here.
        this.component = component;
        if (!component.getId().isPresent()) {
            component.setId(component.getElement().getTag().toLowerCase().replaceAll("[^0-9_A-Za-z]+", ""));
        }

        this.target = "#" + component.getId().get();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        getElement().setProperty("target", target);
    }

    public void subscribe() {
        getElement().setPropertyJson("shortcuts", listToJson(keyboardShortcuts));
        component.getUI().ifPresent(ui -> ui.add(this));
    }

    public KeyboardShortcutManager addShortcut(KeyboardShortcut keyboardShortcut) {
        keyboardShortcuts.add(keyboardShortcut);
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
