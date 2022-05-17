package org.vaadin.artur.axainputtext;

import com.vaadin.flow.component.Key;

import java.util.Arrays;

public class KeyboardShortcut {
    private String scope;
    private String secondaryScope;
    private String keyBinding;
    private Actions handler;
    private String description;

    /**
     *
     * @param scope Default is window. eg. window | #selector | .selector
     * @param handler
     * @param keyBinding
     */
    public KeyboardShortcut(String scope, Actions handler, Key... keyBinding) {
        this(handler, keyBinding);
        this.scope = scope;
    }

    public KeyboardShortcut(Actions handler, Key... keyBinding) {
        this.keyBinding = Arrays.stream(keyBinding).reduce("", (a, k) -> a + "+" + k.getKeys().get(0), String::concat);
        this.keyBinding = this.keyBinding.replaceFirst("\\+", "");
        this.handler = handler;
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

    public void setHandler(Actions handler) {
        this.handler = handler;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public enum Actions {
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

