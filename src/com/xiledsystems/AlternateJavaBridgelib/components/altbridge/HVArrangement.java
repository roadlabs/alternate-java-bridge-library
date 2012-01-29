package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ViewUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.app.Activity;
import android.view.View;

/**
 * A container for components that arranges them linearly, either
 * horizontally or vertically.
 *
 */


public class HVArrangement extends AndroidViewComponent implements Component, ComponentContainer {
  private final Activity context;

  // Layout
  private final int orientation;
  private final LinearLayout viewLayout;
  
  //AJB
  
  private final Form form;

  /**
   * Creates a new HVArrangement component.
   *
   * @param container  container, component will be placed in
   * @param orientation one of
   *     {@link ComponentConstants#LAYOUT_ORIENTATION_HORIZONTAL}.
   *     {@link ComponentConstants#LAYOUT_ORIENTATION_VERTICAL}
  */
  public HVArrangement(ComponentContainer container, int orientation) {
    super(container);
    form = container.$form();
    context = container.$context();

    this.orientation = orientation;
    viewLayout = new LinearLayout(context, orientation,
        ComponentConstants.EMPTY_HV_ARRANGEMENT_WIDTH,
        ComponentConstants.EMPTY_HV_ARRANGEMENT_HEIGHT);

    
    container.$add(this);
  }
  
  public HVArrangement(ComponentContainer container, int orientation, int resourceId) {
	    super(container);
	    form = container.$form();
	    context = container.$context();

	    this.orientation = orientation;
	    viewLayout = new LinearLayout(form, orientation,
	        ComponentConstants.EMPTY_HV_ARRANGEMENT_WIDTH,
	        ComponentConstants.EMPTY_HV_ARRANGEMENT_HEIGHT, resourceId);

	    
	    //container.$add(this);
	  }

  // ComponentContainer implementation

  @Override
  public Activity $context() {
    return context;
  }

  @Override
  public Form $form() {
    return container.$form();
  }

  @Override
  public void $add(AndroidViewComponent component) {
    viewLayout.add(component);
  }

  @Override
  public void setChildWidth(AndroidViewComponent component, int width) {
    if (orientation == ComponentConstants.LAYOUT_ORIENTATION_HORIZONTAL) {
      ViewUtil.setChildWidthForHorizontalLayout(component.getView(), width);
    } else {
      ViewUtil.setChildWidthForVerticalLayout(component.getView(), width);
    }
  }

  @Override
  public void setChildHeight(AndroidViewComponent component, int height) {
    if (orientation == ComponentConstants.LAYOUT_ORIENTATION_HORIZONTAL) {
      ViewUtil.setChildHeightForHorizontalLayout(component.getView(), height);
    } else {
      ViewUtil.setChildHeightForVerticalLayout(component.getView(), height);
    }
  }

  // AndroidViewComponent implementation

  @Override
  public View getView() {
    return viewLayout.getLayoutManager();
  }
  
  @Override
	public void postAnimEvent() {
		EventDispatcher.dispatchEvent(this, "AnimationMiddle");
		
	}
}
