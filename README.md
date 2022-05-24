# Component Factory Keyboard Shortcut Manager for Vaadin 23

[Live Demo ↗](https://incubator.app.fi/keyboard-shortcut-manager-demo/keyboard-shortcut-manager)

[keyboard-shortcut-manager](https://www.npmjs.com/package/@vaadin-component-factory/keyboard-shortcut-manager) is A modern library for managing keyboard shortcuts in a Vaadin application.

![KSM](https://user-images.githubusercontent.com/3392815/170082654-03648e3d-bc66-4a78-a044-2166f265a7d2.gif)

## Basic Usage

```java
KeyboardShortcutManager keyboardShortcutManager = new KeyboardShortcutManager(this);
KeyboardShortcut[] shortcuts = new KeyboardShortcut[] {
    new KeyboardShortcut("", KeyboardShortcut.Actions.helpDialog, Key.CONTROL, Key.SHIFT, Key.SLASH),
    new KeyboardShortcut("", KeyboardShortcut.Actions.focusNextInvalidField, Key.ALT, Key.F8),
    new KeyboardShortcut("", KeyboardShortcut.Actions.focusPreviousInvalidField, Key.ALT, Key.SHIFT, Key.F8),
    new KeyboardShortcut("scope-element-1", KeyboardShortcut.Actions.clearAllFields, Key.CONTROL, Key.KEY_K),
    new KeyboardShortcut("focus-element", "scope-element-2", KeyboardShortcut.Actions.focusElement, Key.CONTROL, Key.KEY_F)
};

keyboardShortcutManager.addShortcut(shortcuts);
keyboardShortcutManager.subscribe();
```

# How to run the demo?

To run the demo first change your active directory to the demo folder:

```
cd keyboard-shortcut-manager-flow-demo/
```

The demo can then be run by executing:

```
mvn jetty:run
```

After server startup, you'll be able find the demo at [http://localhost:8080](http://localhost:8080)

## License & Author

This Add-on is distributed under Apache 2. For license terms, see LICENSE.txt.

Component Factory Keyboard Shortcut Manager is written by Vaadin Ltd.

## Setting up for development:

Clone the project in GitHub (or fork it if you plan on contributing):

```
git clone git@github.com:vaadin-component-factory/keyboard-shortcut-manager-flow.git
```

To install project to your maven repository run:

```
mvn install
```
