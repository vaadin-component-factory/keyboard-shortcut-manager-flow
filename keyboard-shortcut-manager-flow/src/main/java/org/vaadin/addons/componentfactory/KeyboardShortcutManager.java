package org.vaadin.addons.componentfactory;

/*
 * #%L
 * keyboard-shortcut-manager-flow
 * %%
 * Copyright (C) 2020 Vaadin Ltd
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
@NpmPackage(value = "@vaadin-component-factory/keyboard-shortcut-manager", version = "23.1.12")
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
        component.getUI().get().add(this);
    }

    public KeyboardShortcutManager addShortcut(KeyboardShortcut... keyboardShortcuts) {
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
