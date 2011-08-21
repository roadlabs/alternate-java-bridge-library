package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

/**
 * Base class for all non-visible components.
 *
 */

public abstract class AndroidNonvisibleComponent implements Component {

  protected final Form form;

  /**
   * Creates a new AndroidNonvisibleComponent.
   *
   * @param container  container, component will be placed in
   */
  protected AndroidNonvisibleComponent(Form form) {
    this.form = form;
  }

  // Component implementation

  @Override
  public HandlesEventDispatching getDispatchDelegate() {
    return form;
  }
}
