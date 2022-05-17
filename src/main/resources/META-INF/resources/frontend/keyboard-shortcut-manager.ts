import {customElement, html, LitElement, property} from 'lit-element';
import {
  KeyboardShortcut,
  KeyboardShortcutManager,
  KeyboardShortcutUtils
} from '@vaadin-component-factory/keyboard-shortcut-manager';

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
    const ksm = new KeyboardShortcutManager({ _jshortcuts, root: document.body, helpDialog: true });
    window.addEventListener('focus-next-invalid-field', () => KeyboardShortcutUtils.focusNextInvalidField());
    window.addEventListener('focus-previous-invalid-field', () => KeyboardShortcutUtils.focusPreviousInvalidField());
    window.addEventListener('clear-all-fields', () => KeyboardShortcutManagerFlow.clearAllFields(this.target));
    window.addEventListener('focus-element', () => KeyboardShortcutManagerFlow.focusElement(this.target));

    // @ts-ignore
    ksm.add(this.shortcuts);

    console.log('ksm.shortcuts: ', ksm.shortcuts);

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

  static focusElement(target: string) {
    console.log(document.getElementById(target));
    document.getElementById(target)?.focus();
  }

  static clearAllFields(target: string) {
    const exclude = ['a', 'button'];
    const notDisabledFields = KeyboardShortcutUtils.InputFields.split(',')
      .map((f) => f.replace(/\s+/g, ''))
      .filter((field) => !exclude.includes(field))
      .map((field) => `${field}:not([disabled])`)
      .join();
    document
      .querySelector(target)
      ?.querySelectorAll(notDisabledFields)
      .forEach((el: any) => {
        el.value = '';
        el.validate();
      });
  }

  // Remove this method to render the contents of this view inside Shadow DOM
  createRenderRoot() {
    return this;
  }
}
