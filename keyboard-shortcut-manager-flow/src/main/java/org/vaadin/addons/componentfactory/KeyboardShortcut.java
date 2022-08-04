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

import com.vaadin.flow.component.Key;

import java.util.Arrays;

public class KeyboardShortcut {
    private String description;
    private String scope;
    private String handler;
    private String keyBinding;

    /**
     * Platform Independent Modifier.
     * <ul>
     * <li>Mac = <code>Meta</code> (⌘)</li>
     * <li>Windows/Linux = <code>Control</code></li>
     * </ul>
     */
    public static Key MOD = Key.of("MOD");

    /**
     * @param selector   Id/selector for focus element actions.
     * @param scope      String id of scope element. Default scope is <b>window</b>.
     *                   eg. "element-id".
     * @param handler    Action to be used in this shortcut.
     * @param keyBinding Key binding descriptions.
     */
    public KeyboardShortcut(String selector, String scope, Actions handler, Key... keyBinding) {
        this(scope, handler.getEvt(), keyBinding);
        this.handler = KeyboardShortcut.getActionHandler(handler, selector);
        this.description = KeyboardShortcut.getActionDescription(handler, selector);
    }

    /**
     * @param description Description of the shortcut.
     * @param scope       String id of scope element. Default scope is
     *                    <b>window</b>. eg. "element-id".
     * @param handler     String name of custom event dispatched by the keyboard
     *                    shortcut.
     * @param keyBinding  Key binding descriptions.
     */
    public KeyboardShortcut(String description, String scope, String handler, Key... keyBinding) {
        this(scope, handler, keyBinding);
        this.description = description;
    }

    /**
     * @param scope      String id of scope element. Default scope is
     *                   <b>window</b>. eg. "element-id".
     * @param handler    String name of custom event dispatched by the keyboard
     *                   shortcut.
     * @param keyBinding Key binding descriptions.
     */
    public KeyboardShortcut(String scope, String handler, Key... keyBinding) {
        this(handler, keyBinding);
        this.scope = scope;
    }

    /**
     * @param scope      String id of scope element. Default scope is
     *                   <b>window</b>. eg. "element-id".
     * @param handler    Action to be used in this shortcut.
     * @param keyBinding Key binding descriptions.
     */
    public KeyboardShortcut(String scope, Actions handler, Key... keyBinding) {
        this(KeyboardShortcut.getActionDescription(handler), scope, handler.getEvt(), keyBinding);
    }

    /**
     * @param handler    String name of custom event dispatched by the keyboard
     *                   shortcut.
     * @param keyBinding Key binding descriptions.
     */
    public KeyboardShortcut(String handler, Key... keyBinding) {
        this.keyBinding = Arrays.stream(keyBinding).reduce("", (a, k) -> a + "+" + k.getKeys().get(0), String::concat);
        this.keyBinding = this.keyBinding.replaceFirst("\\+", "");
        this.handler = handler;
    }

    /**
     * @param handler    Action to be used in this shortcut.
     * @param keyBinding Key binding descriptions.
     */
    public KeyboardShortcut(Actions handler, Key... keyBinding) {
        this(handler.getEvt(), keyBinding);
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getKeyBinding() {
        return keyBinding;
    }

    public void setKeyBinding(String keyBinding) {
        this.keyBinding = keyBinding;
    }

    public String getHandler() {
        return handler.toString();
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public void setHandler(Actions handler) {
        this.setHandler(handler.getEvt());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private static String getActionDescription(Actions handler, String decription) {
        String newDescription = decription;
        if (handler.equals(Actions.helpDialog)) {
            newDescription = "Open help Dialog.";
        } else if (handler.equals(Actions.focusNextInvalidField)) {
            newDescription = "Focus next invalid field.";
        } else if (handler.equals(Actions.focusPreviousInvalidField)) {
            newDescription = "Focus previous invalid field.";
        } else if (handler.equals(Actions.clearAllFields)) {
            newDescription = "Clear all fields.";
        } else if (!decription.isEmpty() && handler.getEvt().indexOf(Actions.clickElement.getEvt()) >= 0) {
            newDescription = "Click element: #";
            newDescription += decription;
        } else if (!decription.isEmpty() && handler.getEvt().indexOf(Actions.focusElement.getEvt()) >= 0) {
            newDescription = "Focus element: #";
            newDescription += decription;
        } else if (!decription.isEmpty() && handler.getEvt().indexOf(Actions.focusNextElement.getEvt()) >= 0) {
            newDescription = "Focus next element in: ";
            newDescription += decription;
        } else if (!decription.isEmpty() && handler.getEvt().indexOf(Actions.focusPreviousElement.getEvt()) >= 0) {
            newDescription = "Focus previous element in: ";
            newDescription += decription;
        }
        return newDescription;
    }

    private static String getActionDescription(Actions handler) {
        return KeyboardShortcut.getActionDescription(handler, "");
    }

    private static String getActionHandler(Actions handler, String decription) {
        String newHandler = handler.getEvt();
        if (handler.equals(Actions.clickElement)
                || handler.equals(Actions.focusElement)
                || handler.equals(Actions.focusNextElement)
                || handler.equals(Actions.focusPreviousElement)) {
            newHandler += decription;
        }
        return newHandler;
    }

    public enum Actions {
        helpDialog("help-dialog"),
        focusNextInvalidField("focus-next-invalid-field"),
        focusPreviousInvalidField("focus-previous-invalid-field"),
        clearAllFields("clear-all-fields"),
        clickElement("click-element"),
        focusElement("focus-element"),
        focusNextElement("focus-next-element"),
        focusPreviousElement("focus-previous-element");

        private final String evt;

        Actions(String evt) {
            this.evt = evt;
        }

        public String getEvt() {
            return evt;
        }

        @Override
        public String toString() {
            return evt;
        }
    }
}
