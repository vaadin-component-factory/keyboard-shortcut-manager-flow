import {customElement, html, LitElement, property} from 'lit-element';
import {KeyboardShortcut, KeyboardShortcutManager} from '@vaadin-component-factory/keyboard-shortcut-manager';

@customElement('keyboard-shortcut-manager')
// @ts-ignore
export class KeyboardShortcutManagerFlow extends LitElement {
    @property()
        // @ts-ignore
    target: string = 'body';
    @property()
        // @ts-ignore
    shortcuts: KeyboardShortcut[];

    static activeElement?: Element;

    render() {
        return html``;
    }

    protected firstUpdated() {
        window.addEventListener('help-dialog', () => ksm.toggleHelpDialog());

        const _jshortcuts = this.shortcuts;

        // @ts-ignore
        const ksm = new KeyboardShortcutManager({_jshortcuts, root: document.body, helpDialog: true});
        window.addEventListener("focus-next-invalid-field", () => KeyboardShortcutManagerFlow.focusNextInvalidField(this.target));
        window.addEventListener("focus-previous-invalid-field", () => KeyboardShortcutManagerFlow.focusPreviousInvalidField(this.target));
        window.addEventListener("clear-all-fields", () => KeyboardShortcutManagerFlow.clearAllFields(this.target));

        // @ts-ignore
        ksm.add(this.shortcuts);

        console.log("ksm.shortcuts: ", ksm.shortcuts);

        ksm.subscribe();
    }

    private static validateField(field: any): boolean {
        return field.getAttribute('invalid') !== null;
    }

    static focusNextInvalidField(target: string, reverse = false) {
        const root = document.querySelector(target);
        let focusField: HTMLElement | null = null;
        // @ts-ignore
        let elements = Array.from(root.querySelectorAll('*').values()) as HTMLElement[];

        elements = elements.filter((el: any) => el.checkValidity && !el.inputElement && KeyboardShortcutManagerFlow.validateField(el));

        if (elements.length) {
            if (reverse) {
                elements = elements.reverse();
            }
            const currentIndex = elements.findIndex((el) => el === KeyboardShortcutManagerFlow.activeElement);
            const nextIndex = currentIndex > -1 ? currentIndex + 1 : 0;
            focusField = nextIndex < elements.length ? elements[nextIndex] : elements[0];
            focusField?.focus();
            KeyboardShortcutManagerFlow.activeElement = focusField;
        }
    }

    static focusPreviousInvalidField(target: string) {
        KeyboardShortcutManagerFlow.focusNextInvalidField(target, true);
    }

    static clearAllFields(target: string) {
        document.querySelector(target)?.querySelectorAll('vaadin-text-field:not([disabled])').forEach((el: any) => {
            el.value = '';
            el.validate();
        });
    }



    // Remove this method to render the contents of this view inside Shadow DOM
    createRenderRoot() {
        return this;
    }
}
