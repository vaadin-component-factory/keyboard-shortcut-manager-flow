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
    let parsedHandler = handler.split('_')[0];
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
    this.ksm?.shortcuts?.forEach(shortcut => {
      let scope = (shortcut.scope || window) as TargetElement;
      scope?.addEventListener(`${shortcut.handler}`, () => {
        const scope = shortcut.scope === window ? document : (shortcut.scope as HTMLElement);
        const handler = this.parseHandler(shortcut.handler as string);
        const handlerNoIndex = `${shortcut.handler}`.split('_')[0];
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
            const selector = handlerNoIndex.replace(Actions.clickElement, '');
            KeyboardShortcutManagerFlow.clickElement(selector);
            break;
          }
          case Actions.focusElement: {
            const selector = handlerNoIndex.replace(Actions.focusElement, '');
            KeyboardShortcutManagerFlow.focusElement(selector);
            break;
          }
          case Actions.focusNextElement: {
            const selector = handlerNoIndex.replace(Actions.focusNextElement, '');
            KeyboardShortcutManagerFlow.focusNextElement(selector);
            break;
          }
          case Actions.focusPreviousElement: {
            const selector = handlerNoIndex.replace(Actions.focusPreviousElement, '');
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
      let elements: HTMLElement | HTMLElement[] | null = [];
      const handler = this.parseHandler(shortcut.handler as string);
      if (handler.includes('element')) {
        switch (handler) {
          case Actions.clickElement: {
            const selector = shortcut.handler.toString().replace(Actions.clickElement, '');
            elements = KeyboardShortcutUtils.querySelectorDeep(selector);
            break;
          }
          case Actions.focusElement: {
            const selector = shortcut.handler.toString().replace(Actions.focusElement, '');
            elements = KeyboardShortcutUtils.querySelectorDeep(selector);
            break;
          }
          case Actions.focusNextElement: {
            const selector = shortcut.handler.toString().replace(Actions.focusNextElement, '');
            elements = KeyboardShortcutUtils.querySelectorAllDeep(selector);
            break;
          }
          case Actions.focusPreviousElement: {
            const selector = shortcut.handler.toString().replace(Actions.focusPreviousElement, '');
            elements = KeyboardShortcutUtils.querySelectorAllDeep(selector);
            break;
          }
        }
        if (elements) {
          KeyboardShortcutUtils.setFocusable(elements);
        }
      }
    });
  }

  private setShortcuts(shortcuts?: KeyboardShortcut[]) {
    if (this.ksm && shortcuts) {
      shortcuts.forEach((s, i) => (s.handler = `${s.handler}_${i}`));
      this.ksm.shortcuts = [];
      this.ksm.add(shortcuts);
      console.debug('ksm.shortcuts', this.ksm.shortcuts);
    }
  }

  private static focusElement(selector: string, click = false) {
    const element = KeyboardShortcutUtils.querySelectorDeep(selector);
    if (element) {
      element.focus();
      if (click) element.click();
    } else {
      console.warn(`Element with id "${selector}" not found.`);
    }
  }

  private static clickElement(selector: string) {
    KeyboardShortcutManagerFlow.focusElement(selector, true);
  }

  private static focusNextElement(groupSelector: string, previous = false) {
    const focused = document.activeElement;
    const groupElements = KeyboardShortcutUtils.querySelectorAllDeep(groupSelector) as HTMLElement[];
    if (groupElements.length) {
      const currentGroup = groupElements.filter(element => element.contains(focused))[0];
      let nextGroup: HTMLElement;
      if (currentGroup) {
        const last = groupElements.length - 1;
        const currentIndex = groupElements.indexOf(currentGroup);
        const absNextIndex = currentIndex + (previous ? -1 : 1);
        let nextIndex = absNextIndex <= last ? absNextIndex : 0;
        if (previous) nextIndex = absNextIndex >= 0 ? absNextIndex : last;
        nextGroup = groupElements[nextIndex];
      } else {
        nextGroup = groupElements[0];
      }
      const focusableElements = KeyboardShortcutManagerFlow.getSortedFocusableChildren(nextGroup);
      const firstInputField = focusableElements[0];
      firstInputField.focus();
    } else {
      console.warn(`Elements with selector "${groupSelector}" not found.`);
    }
  }

  private static getSortedFocusableChildren(scope: HTMLElement) {
    const focusableElements = KeyboardShortcutUtils.getFocusableElements(scope);
    const childern = Array.from(scope.children) as HTMLElement[];
    const getChildrenIndex = (a: HTMLElement) => {
      let indexA = childern.indexOf(a);
      if (indexA < 0) {
        if (a.parentElement) indexA = childern.indexOf(a.parentElement);
        if (indexA < 0 && a.getRootNode() !== document) {
          const host = (a.getRootNode() as ShadowRoot).host as HTMLElement;
          indexA = childern.indexOf(host);
        }
      }
      return indexA;
    };
    focusableElements.sort((a, b) => {
      const indexA = getChildrenIndex(a);
      const indexB = getChildrenIndex(b);
      return indexA - indexB;
    });
    return focusableElements;
  }

  private static focusPreviousElement(groupSelector: string) {
    KeyboardShortcutManagerFlow.focusNextElement(groupSelector, true);
  }

  private static clearAllFields(scope: Document | HTMLElement = document) {
    KeyboardShortcutUtils.getVaadinInputFields(scope).forEach((el: any) => {
      el.value = '';
      if (el.validate) el.invalid = !el.validate();
      else if (el.checkValidity) el.invalid = !el.checkValidity();
    });
  }

  createRenderRoot() {
    return this;
  }
}
