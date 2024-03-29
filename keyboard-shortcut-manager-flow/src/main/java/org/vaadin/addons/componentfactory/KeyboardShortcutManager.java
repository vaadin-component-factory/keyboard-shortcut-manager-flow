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
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.littemplate.LitTemplate;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.upload.Upload;
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
@NpmPackage(value = "@vaadin-component-factory/keyboard-shortcut-manager", version = "23.3.1")
public class KeyboardShortcutManager extends LitTemplate {

    private final Component component;
    private Boolean helpDialog = true;
    private List<KeyboardShortcut> keyboardShortcuts = new ArrayList<>();
    public static final String KSM_SECTION_PRIORITY_HINT = "ksm-priority";

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
        if (component == null) {
            throw new IllegalStateException("Trying to subscribe KSM while component is null");
        }
        // If the current UI is present, add KSM
        if (UI.getCurrent() != null) {
            UI.getCurrent().add(this);
        }

        // Add KSM to the UI on attach
        component.addAttachListener(e -> {
            e.getUI().add(this);
            getElement().callJsFunction("onAttach");
        });
        // ... and remove on detach
        component.addDetachListener(e -> {
            getElement().callJsFunction("onDetach");
        });
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

    /**
     * For use with the focusNextElement / focusPreviousElement actions: certain components aren't automatically
     * recognized as the first in a section. Use this method to add a "hint" that the component in question
     * should indeed be considered as the next or previous one.
     * @param component
     */
    public static void addSectionPriorityHint(Component component) {
        Objects.requireNonNull(component, "Keyboard shortcut manager section priority can't be set for a null component");
        if (component instanceof Upload) {
            component.getElement().executeJs("$0.shadowRoot.querySelector(\"[part='upload-button']\").setAttribute(\"theme\",\"ksm-priority\");", component.getElement());
            return;
        } else if (component instanceof MessageInput) {
            component.getElement().executeJs("$0.shadowRoot.querySelector(\"[slot='textarea']\").setAttribute(\"theme\",\"ksm-priority\");", component.getElement());
        }
        component.getElement().getThemeList().add(KSM_SECTION_PRIORITY_HINT);
    }

}
