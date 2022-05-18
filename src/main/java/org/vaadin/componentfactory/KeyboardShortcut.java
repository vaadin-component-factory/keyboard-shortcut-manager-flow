package org.vaadin.componentfactory;

import com.vaadin.flow.component.Key;

import java.util.Arrays;

public class KeyboardShortcut {
    private String description;
    private String scope;
    private String handler;
    private String keyBinding;

    /**
     * @param description Description of the shortcut.
     * @param scope String id of scope element. Default scope is <b>window</b>. eg. "element-id".
     * @param handler
     * @param keyBinding
     */
    public KeyboardShortcut(String focusElement, String scope, Actions handler, Key... keyBinding) {
        this(scope, handler.getEvt(), keyBinding);
        this.handler = KeyboardShortcut.getActionHandler(handler, focusElement);
        this.description = KeyboardShortcut.getActionDescription(handler, scope, focusElement);
    }

    public KeyboardShortcut(String description, String scope, String handler, Key... keyBinding) {
        this(scope, handler, keyBinding);
        this.description = description;
    }

    public KeyboardShortcut(String scope, String handler, Key... keyBinding) {
        this(handler, keyBinding);
        this.scope = scope;
    }

    public KeyboardShortcut(String scope, Actions handler, Key... keyBinding) {
        this(KeyboardShortcut.getActionDescription(handler, scope), scope, handler.getEvt(), keyBinding);
    }

    public KeyboardShortcut(String handler, Key... keyBinding) {
        this.keyBinding = Arrays.stream(keyBinding).reduce("", (a, k) -> a + "+" + k.getKeys().get(0), String::concat);
        this.keyBinding = this.keyBinding.replaceFirst("\\+", "");
        this.handler = handler;
    }

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

    private static String getActionDescription(Actions handler, String scope, String decription) {
        String newDescription = decription;
        if (handler.equals(Actions.helpDialog)) {
            newDescription = "Open help Dialog.";
        } else if (handler.equals(Actions.focusNextInvalidField)) {
            newDescription = "Focus next invalid field.";
        } else if (handler.equals(Actions.focusPreviousInvalidField)) {
            newDescription = "Focus previous invalid field.";
        } else if (handler.equals(Actions.clearAllFields)) {
            newDescription = "Clear all fields.";
        } else if (!decription.isEmpty() && handler.getEvt().indexOf(Actions.focusElement.getEvt()) >= 0) {
            newDescription = "Focus element: #";
            newDescription += decription;
        }
        return newDescription;
    }

    private static String getActionHandler(Actions handler, String decription) {
        String newHandler = handler.getEvt();
        if (handler.equals(Actions.focusElement)) {
            newHandler += decription;
        }
        return newHandler;
    }

    private static String getActionDescription(Actions handler, String scope) {
        return KeyboardShortcut.getActionDescription(handler, scope, "");
    }

    public enum Actions {
        helpDialog("help-dialog"),
        focusNextInvalidField("focus-next-invalid-field"),
        focusPreviousInvalidField("focus-previous-invalid-field"),
        clearAllFields("clear-all-fields"),
        focusElement("focus-element");

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

