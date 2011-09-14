package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;

/**
 * A horizontal arrangement of components
 *
 */

public class HorizontalArrangement extends HVArrangement {
  public HorizontalArrangement(ComponentContainer container) {
    super(container, ComponentConstants.LAYOUT_ORIENTATION_HORIZONTAL);
  }

}
