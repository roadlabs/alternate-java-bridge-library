package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;

/**
 * Base class for all non-visible components.
 *
 */

public abstract class AndroidNonvisibleComponent implements Component {

  protected final ComponentContainer container;
  protected final SvcComponentContainer sContainer;

  /**
   * Creates a new AndroidNonvisibleComponent.
   *
   * @param container  container, component will be placed in
   */
  protected AndroidNonvisibleComponent(ComponentContainer container) {
    this.container = container;
    this.sContainer = null;
  }
  
  protected AndroidNonvisibleComponent(SvcComponentContainer sContainer) {
	    this.sContainer = sContainer;
	    this.container = null;
	  }

  // Component implementation

  @Override
  public HandlesEventDispatching getDispatchDelegate() {
	  if (container==null) {
		  return sContainer.$formService();
	  } else {
		  return container.$form();
	  }
  }
}
