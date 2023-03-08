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
    this.ksm?.shortcuts?.forEach((shortcut: KeyboardShortcut) => {
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
      let tabIndexAdded = false;
      if (this.shouldReceiveTabIndex(element)) {
        this.addTabIndex(element);
        tabIndexAdded = true;
      }
      element.focus();
      if (click) {
        element.click();
      }
      if (tabIndexAdded) {
        this.removeTabIndex(element);
      }
    } else {
      console.warn(`Element with id "${selector}" not found.`);
    }
  }

  private static shouldReceiveTabIndex(target: HTMLElement | null) {
    if (!target || !target.parentNode) {
          return false;
    }
    const focusables = Array.from(
            target.parentNode.querySelectorAll(
                '[tabindex], button, input, select, textarea, object, iframe, a[href], area[href]',
            ),
        ).filter((element) => {
          const part = element.getAttribute('part');
          return !(part && part.includes('body-cell'));
        });
    const isNonFocusableElement = !focusables.includes(target);
    return (isNonFocusableElement
            && !(target as any).disabled
            && !(target.offsetParent == null)
            && getComputedStyle(target).visibility !== 'hidden'
    );
  }

  private static addTabIndex(target: HTMLElement | null) {
    if (target == null) {
      return;
    }
    target.setAttribute('tabindex', '0');
  }

  private static removeTabIndex(target: HTMLElement | null) {
    if (target == null) {
      return;
    }
    target.removeAttribute('tabindex');
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

      let tabIndexAdded = false;
      if (this.shouldReceiveTabIndex(firstInputField)) {
        this.addTabIndex(firstInputField);
        tabIndexAdded = true;
      }
      firstInputField.focus();
      if (tabIndexAdded) {
        this.removeTabIndex(firstInputField);
      }
    } else {
      console.warn(`Elements with selector "${groupSelector}" not found.`);
    }
  }

  private static getSortedFocusableChildren(scope: HTMLElement) {
    let focusableElements = KeyboardShortcutUtils.getFocusableElements(scope);
    focusableElements = focusableElements.filter(el => KeyboardShortcutUtils.isFocusable(el));
    focusableElements.sort((a, b) => {
      let order = a.compareDocumentPosition(b) & Node.DOCUMENT_POSITION_PRECEDING;
      return order ? 1 : -1;
    });
    return focusableElements;
  }

  private static focusPreviousElement(groupSelector: string) {
    KeyboardShortcutManagerFlow.focusNextElement(groupSelector, true);
  }

  private static clearAllFields(scope: Document | HTMLElement = document) {
    const allFields = [...KeyboardShortcutUtils.getInputFields(scope), ...KeyboardShortcutUtils.getVaadinInputFields(scope)];
    allFields.forEach((el: any) => {
      const isButtonInput = el instanceof HTMLInputElement && el.type === 'button';
      if (el.value && !isButtonInput) el.value = '';
      if (el.validate) el.invalid = !el.validate();
      else if (el.checkValidity) el.invalid = !el.checkValidity();
    });
  }

  createRenderRoot() {
    return this;
  }
}
