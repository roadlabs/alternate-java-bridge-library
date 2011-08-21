package com.xiledsystems.AlternateJavaBridgelib.components;

/**
 * Interface indicating that this object can handle event dispatching.
 *
 */

public interface HandlesEventDispatching {
  public boolean canDispatchEvent(Component component, String eventName);

  public boolean dispatchEvent(Component component, String componentName, String eventName,
      Object[] args);
}
