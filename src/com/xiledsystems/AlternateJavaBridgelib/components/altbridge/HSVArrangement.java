package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ViewUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;

public class HSVArrangement extends AndroidViewComponent implements Component, ComponentContainer {

	private final Activity context;

	  
	  // Layout
	  private final int orientation;
	  private final HorizontalScrollView viewLayout;

	  /**
	   * Creates a new HVArrangement component.
	   *
	   * @param container  container, component will be placed in
	   * @param orientation one of
	   *     {@link ComponentConstants#LAYOUT_ORIENTATION_HORIZONTAL}.
	   *     {@link ComponentConstants#LAYOUT_ORIENTATION_VERTICAL}
	  */
	  public HSVArrangement(ComponentContainer container, int orientation) {
	    super(container);
	    
	    context = container.$context();

	    this.orientation = orientation;
	    viewLayout = new HorizontalScrollView(context, orientation, ComponentConstants.EMPTY_HV_ARRANGEMENT_WIDTH,
	    		ComponentConstants.EMPTY_HV_ARRANGEMENT_HEIGHT);
	    
	    container.$add(this);
	  }
	  
	  public HSVArrangement(ComponentContainer container, int orientation, int resourceId) {
		    super(container);
		    
		    context = container.$context();

		    this.orientation = orientation;
		    viewLayout = new HorizontalScrollView(context, orientation, ComponentConstants.EMPTY_HV_ARRANGEMENT_WIDTH,
		    		ComponentConstants.EMPTY_HV_ARRANGEMENT_HEIGHT, resourceId);
		    
		    container.$add(this);
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
	  
	  /**
	   *  Assign an image to the background of this arrangement
	   *  
	   * @param resourceId The resource Id of the drawable to use
	   */
	  public void BackgroundImage(int resourceId) {
		  viewLayout.getLayoutManager().setBackgroundDrawable(container.$context().getResources().getDrawable(resourceId));
	  }
	  
	  /**
	   * Alternate method for setting the background image
	   * of this component. Mostly used with SpriteSheetHelper
	   * when managing multiple images in a sprite sheet.
	   * 
	   * @param drawable
	   */
	  public void Drawable(Drawable drawable) {
		  viewLayout.getLayoutManager().setBackgroundDrawable(drawable);
	  }
	  
	  /**
	   *  Assign a color to the background of this arrangement
	   *  
	   * @param color The color
	   */
	  public void BackgroundColor(int color) {
		  viewLayout.getLayoutManager().setBackgroundColor(color);
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

