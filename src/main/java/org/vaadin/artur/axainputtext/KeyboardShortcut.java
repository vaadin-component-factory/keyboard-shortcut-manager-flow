package org.vaadin.artur.axainputtext;

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
    public KeyboardShortcut(String description, String scope, Actions handler, Key... keyBinding) {
        this(description, scope, handler.getEvt(), keyBinding);
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
        this(scope, handler.getEvt(), keyBinding);
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

    public enum Actions {
        helpDialog("help-dialog"),
        focusNextInvalidField("focus-next-invalid-field"),
        focusPreviousInvalidField("focus-previous-invalid-field"),
        focusElement("focus-element"),
        clearAllFields("clear-all-fields");

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

