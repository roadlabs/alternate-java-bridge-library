package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;

/**
 * Base class for all non-visible components.
 *
 */

public abstract class AndroidNonvisibleComponent implements Component {

  protected final Form form;
  protected final FormService formservice;

  /**
   * Creates a new AndroidNonvisibleComponent.
   *
   * @param container  container, component will be placed in
   */
  protected AndroidNonvisibleComponent(Form form) {
    this.form = form;
    this.formservice = null;
  }
  
  protected AndroidNonvisibleComponent(FormService formservice) {
	    this.formservice = formservice;
	    this.form = null;
	  }

  // Component implementation

  @Override
  public HandlesEventDispatching getDispatchDelegate() {
	  if (form==null) {
		  return formservice;
	  } else {
		  return form;
	  }
  }
}
