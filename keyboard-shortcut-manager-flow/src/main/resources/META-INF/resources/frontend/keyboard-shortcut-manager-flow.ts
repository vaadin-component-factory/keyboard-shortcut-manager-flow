import { LitElement } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import {
  KeyboardShortcut,
  KeyboardShortcutManager,
  KeyboardShortcutUtils,
  TargetElement
} from '@vaadin-component-factory/keyboard-shortcut-manager';

export enum Actions {
  helpDialog = 'help-dialog',
  focusNextInvalidField = 'focus-next-invalid-field',
  focusPreviousInvalidField = 'focus-previous-invalid-field',
  clearAllFields = 'clear-all-fields',
  focusElement = 'focus-element'
}

@customElement('keyboard-shortcut-manager-flow')
export class KeyboardShortcutManagerFlow extends LitElement {
  @property({ type: Boolean }) helpDialog = true;
  @property({ type: Array }) shortcuts?: KeyboardShortcut[];

  private ksm?: KeyboardShortcutManager;
  static activeElement?: Element;

  protected firstUpdated() {
    this.ksm = new KeyboardShortcutManager({ helpDialog: this.helpDialog });

    if (this.shortcuts) {
      this.ksm.add(this.shortcuts);
      console.debug('KSM.shortcuts: ', this.ksm.shortcuts);
    }

    this.ksm.subscribe();
    this.addActionListeners();
  }

  private addActionListeners() {
    this.shortcuts?.forEach(shortcut => {
      if (shortcut.scope) {
        const scope = shortcut.scope as TargetElement;
        if (scope) {
          scope.addEventListener(`${shortcut.handler}`, () => {
            const scope = shortcut.scope === window ? document : (shortcut.scope as HTMLElement);
            const handler = shortcut.handler.toString().includes(Actions.focusElement)
              ? Actions.focusElement
              : shortcut.handler;
            switch (handler) {
              case Actions.helpDialog:
                this.ksm?.toggleHelpDialog();
                break;
              case Actions.focusNextInvalidField:
                KeyboardShortcutUtils.focusNextInvalidField(scope);
                break;
              case Actions.focusPreviousInvalidField:
                KeyboardShortcutUtils.focusPreviousInvalidField(scope);
                break;
              case Actions.clearAllFields:
                KeyboardShortcutManagerFlow.clearAllFields(scope);
                break;
              case Actions.focusElement:
                const targetId = shortcut.handler.toString().replace(Actions.focusElement, '');
                KeyboardShortcutManagerFlow.focusElement(targetId);
                break;
            }
          });
        } else {
          console.warn('Scope element not found.');
        }
      }
    });
  }

  private static focusElement(targetId: string) {
    const element = KeyboardShortcutUtils.querySelectorDeep(`#${targetId}`);
    if (element) element.focus();
    else console.warn(`Element with id ${targetId} not found.`);
  }

  private static clearAllFields(scope: Document | HTMLElement = document) {
    KeyboardShortcutUtils.getVaadinInputFields(scope).forEach((el: any) => {
      el.value = '';
      el.invalid = !el.validate();
    });
  }

  createRenderRoot() {
    return this;
  }
}
