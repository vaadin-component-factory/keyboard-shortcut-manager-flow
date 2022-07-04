import { LitElement, PropertyValues } from 'lit';
import { customElement, property } from 'lit/decorators.js';
import { KeyboardShortcut, KeyboardShortcutManager, KeyboardShortcutUtils, TargetElement } from '@vaadin-component-factory/keyboard-shortcut-manager';

export enum Actions {
  helpDialog = 'help-dialog',
  focusNextInvalidField = 'focus-next-invalid-field',
  focusPreviousInvalidField = 'focus-previous-invalid-field',
  clearAllFields = 'clear-all-fields',
  clickElement = 'click-element',
  focusElement = 'focus-element',
  focusNextElement = 'focus-next-element',
  focusPreviousElement = 'focus-previous-element'
}

@customElement('keyboard-shortcut-manager-flow')
export class KeyboardShortcutManagerFlow extends LitElement {
  @property({ type: Boolean }) helpDialog = true;
  @property({ type: Array }) shortcuts?: KeyboardShortcut[];
  private ksm?: KeyboardShortcutManager;
  private firstUpdate = false;

  protected firstUpdated() {
    this.ksm = new KeyboardShortcutManager({ helpDialog: this.helpDialog });
    this.setShortcuts(this.shortcuts);
    this.ksm.subscribe();
    this.addActionListeners();
    this.firstUpdate = true;
  }

  protected updated(props: PropertyValues) {
    if (props.has('shortcuts') && this.shortcuts && !this.firstUpdate) {
      this.setShortcuts(this.shortcuts);
    } else {
      this.firstUpdate = false;
    }
  }

  private parseHandler(handler: string) {
    let parsedHandler = handler;
    if (handler.toString().includes(Actions.clickElement)) parsedHandler = Actions.clickElement;
    else if (handler.toString().includes(Actions.focusElement)) parsedHandler = Actions.focusElement;
    else if (handler.toString().includes(Actions.focusNextElement)) parsedHandler = Actions.focusNextElement;
    else if (handler.toString().includes(Actions.focusPreviousElement)) parsedHandler = Actions.focusPreviousElement;
    return parsedHandler;
  }

  /**
   * Add event listeners for Actions.
   */
  private addActionListeners() {
    this.shortcuts?.forEach(shortcut => {
      const scope = (shortcut.scope ?? window) as TargetElement;
      scope.addEventListener(`${shortcut.handler}`, () => {
        const scope = shortcut.scope === window ? document : (shortcut.scope as HTMLElement);
        const handler = this.parseHandler(shortcut.handler as string);
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
          case Actions.clickElement: {
            const targetId = shortcut.handler.toString().replace(Actions.clickElement, '');
            KeyboardShortcutManagerFlow.clickElement(targetId);
            break;
          }
          case Actions.focusElement: {
            const targetId = shortcut.handler.toString().replace(Actions.focusElement, '');
            KeyboardShortcutManagerFlow.focusElement(targetId);
            break;
          }
          case Actions.focusNextElement: {
            const selector = shortcut.handler.toString().replace(Actions.focusNextElement, '');
            KeyboardShortcutManagerFlow.focusNextElement(selector);
            break;
          }
          case Actions.focusPreviousElement: {
            const selector = shortcut.handler.toString().replace(Actions.focusPreviousElement, '');
            KeyboardShortcutManagerFlow.focusPreviousElement(selector);
            break;
          }
        }
      });
      this.initActionElements(shortcut);
    });
  }

  /**
   * Make elements used in Actions focusable.
   */
  private initActionElements(shortcut: KeyboardShortcut) {
    requestAnimationFrame(() => {
      const handler = this.parseHandler(shortcut.handler as string);
      if (handler.includes('element')) {
        switch (handler) {
          case Actions.clickElement: {
            const targetId = shortcut.handler.toString().replace(Actions.clickElement, '');
            const element = KeyboardShortcutUtils.querySelectorDeep(`#${targetId}`);
            if (element) KeyboardShortcutManagerFlow.setFocusable(element);
            break;
          }
          case Actions.focusElement: {
            const targetId = shortcut.handler.toString().replace(Actions.focusElement, '');
            const element = KeyboardShortcutUtils.querySelectorDeep(`#${targetId}`);
            if (element) KeyboardShortcutManagerFlow.setFocusable(element);
            break;
          }
          case Actions.focusNextElement: {
            const selector = shortcut.handler.toString().replace(Actions.focusNextElement, '');
            const elements = KeyboardShortcutUtils.querySelectorAllDeep(selector);
            elements.forEach(element => KeyboardShortcutManagerFlow.setFocusable(element));
            break;
          }
          case Actions.focusPreviousElement: {
            const selector = shortcut.handler.toString().replace(Actions.focusPreviousElement, '');
            const elements = KeyboardShortcutUtils.querySelectorAllDeep(selector);
            elements.forEach(element => KeyboardShortcutManagerFlow.setFocusable(element));
            break;
          }
        }
      }
    });
  }

  private setShortcuts(shortcuts?: KeyboardShortcut[]) {
    if (this.ksm && shortcuts) {
      this.ksm.shortcuts = [];
      this.ksm.add(shortcuts);
      console.debug('ksm.shortcuts', this.ksm.shortcuts);
    }
  }

  private static focusElement(targetId: string, click = false) {
    const element = KeyboardShortcutUtils.querySelectorDeep(`#${targetId}`);
    if (element) {
      element.focus();
      if (click) element.click();
    } else {
      console.warn(`Element with id "${targetId}" not found.`);
    }
  }

  private static clickElement(targetId: string) {
    KeyboardShortcutManagerFlow.focusElement(targetId, true);
  }

  private static focusNextElement(groupSelector: string, previous = false) {
    const focused = KeyboardShortcutUtils.getActiveElement();
    const groupElements = KeyboardShortcutUtils.querySelectorAllDeep(groupSelector) as HTMLElement[];
    groupElements.forEach(element => KeyboardShortcutManagerFlow.setFocusable(element));
    if (groupElements.length) {
      const currentGroup = groupElements.filter(element => element.contains(focused))[0];
      let firstInputField: HTMLElement;
      if (currentGroup) {
        const last = groupElements.length - 1;
        const currentIndex = groupElements.indexOf(currentGroup);
        const absNextIndex = currentIndex + (previous ? -1 : 1);
        let nextIndex = absNextIndex <= last ? absNextIndex : 0;
        if (previous) nextIndex = absNextIndex >= 0 ? absNextIndex : last;
        const nextGroup = groupElements[nextIndex];
        firstInputField = KeyboardShortcutUtils.getInputFields(nextGroup)[0];
      } else {
        const nextGroup = groupElements[0];
        firstInputField = KeyboardShortcutUtils.getInputFields(nextGroup)[0];
      }
      if (firstInputField) firstInputField.focus();
    } else {
      console.warn(`Elements with selector "${groupSelector}" not found.`);
    }
  }

  private static focusPreviousElement(groupSelector: string) {
    KeyboardShortcutManagerFlow.focusNextElement(groupSelector, true);
  }

  private static clearAllFields(scope: Document | HTMLElement = document) {
    KeyboardShortcutUtils.getVaadinInputFields(scope).forEach((el: any) => {
      el.value = '';
      el.invalid = !el.validate();
    });
  }

  private static setFocusable(element: HTMLElement) {
    if (element.tabIndex < 0 && !element.getAttribute('tabindex')) {
      element.setAttribute('tabindex', '-1');
    }
  }

  createRenderRoot() {
    return this;
  }
}
