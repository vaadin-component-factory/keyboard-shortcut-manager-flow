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

interface EventRegistration {
  handler: any;
  listener: () => void;
}

@customElement('keyboard-shortcut-manager-flow')
export class KeyboardShortcutManagerFlow extends LitElement {
  @property({ type: Boolean }) helpDialog = true;
  @property({ type: Array }) shortcuts?: KeyboardShortcut[];
  private ksm?: KeyboardShortcutManager;
  private firstUpdate = false;

  private listenersByScope = new Map<any, EventRegistration[]>();

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
    const that = this;
    this.ksm?.shortcuts?.forEach((shortcut: KeyboardShortcut) => {
      let scope = (shortcut.scope || window) as TargetElement;
      let listener = function() {
        const scope = shortcut.scope === window ? document : (shortcut.scope as HTMLElement);
        const handler = that.parseHandler(shortcut.handler as string);
        const handlerNoIndex = `${shortcut.handler}`.split('_')[0];
        switch (handler) {
          case Actions.helpDialog:
            that.ksm?.toggleHelpDialog();
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
      }.bind(this);


      scope?.addEventListener(`${shortcut.handler}`, listener);
      if (scope) {
        const pair: EventRegistration = {
          handler: shortcut.handler,
          listener: listener
        };
        if (this.listenersByScope.has(scope)) {
          let listenerArray = this.listenersByScope.get(scope);
          if (listenerArray == null) {
            return;
          }
          listenerArray.push(pair);
        } else {
          let listenerArray = [pair];
          this.listenersByScope.set(scope, listenerArray);
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
    let groupElements = KeyboardShortcutUtils.querySelectorAllDeep(groupSelector) as HTMLElement[];
    const currentGroup = groupElements.filter(element => element.contains(focused))[0];
    while (groupElements.length > 0) {
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
      if (!firstInputField) {
        // Didn't find a focusable element in the next group -> remove it from the set and try again
        groupElements.splice(groupElements.indexOf(nextGroup), 1);
        continue;
      }
      let tabIndexAdded = false;
      if (this.shouldReceiveTabIndex(firstInputField)) {
        this.addTabIndex(firstInputField);
        tabIndexAdded = true;
      }
      firstInputField.focus();
      if (tabIndexAdded) {
        this.removeTabIndex(firstInputField);
      }
      break;
    }
    if (groupElements.length == 0) {
      console.warn(`Focusable element groups with selector "${groupSelector}" not found.`);
    }

  }

  private static getSortedFocusableChildren(scope: HTMLElement) {
    let focusableElements = KeyboardShortcutUtils.getFocusableElements(scope);
    focusableElements = focusableElements.filter((el: any)=> KeyboardShortcutUtils.isFocusable(el));
    let implementationSpecificNodes = focusableElements.filter( (el: any) => {
      if (this.isPriority(el)) {
        return false;
      }
      return document.body.compareDocumentPosition(el) & Node.DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC;
    } );
    focusableElements = focusableElements.filter((el: any) => !implementationSpecificNodes.includes(el));
    if (!focusableElements && implementationSpecificNodes) {
      return implementationSpecificNodes;
    }
    focusableElements.sort((a: Node, b: Node) => {
      let aPriority = this.isPriority(a);
      let bPriority = this.isPriority(b);
      if (aPriority && bPriority) {
        return a.compareDocumentPosition(b) ? 1 : -1;
      }
      if (aPriority) {
        return -1;
      }
      if (bPriority) {
        return 1;
      }
      let compare = a.compareDocumentPosition(b);
      let order = compare & Node.DOCUMENT_POSITION_PRECEDING;
      return order ? 1 : -1;
    });
    return focusableElements;
  }

  private static isPriority(el: any): boolean {
    const theme = el.getAttribute("theme");
    return theme && theme.includes("ksm-priority");

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

  onAttach() {
    this.ksm?.subscribe();
  }

  onDetach() {
    for (const [scope, listenersArray] of this.listenersByScope) {
      for (var i: number = 0; i < listenersArray.length; i++) {
        const pair: EventRegistration = listenersArray[i];
        scope.removeEventListener(pair.handler as any, pair.listener);
      }
    }
    this.ksm?.unsubscribe();
  }
}