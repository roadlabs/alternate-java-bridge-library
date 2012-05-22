package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;

/**
 * A vertical arrangement of components
 *
 */


public class VerticalArrangement extends HVArrangement {

  public VerticalArrangement(ComponentContainer container) {
    super(container, ComponentConstants.LAYOUT_ORIENTATION_VERTICAL);
  }
  
  public VerticalArrangement(ComponentContainer container, int resourceId) {
	    super(container, ComponentConstants.LAYOUT_ORIENTATION_VERTICAL, resourceId);
	  }

}
