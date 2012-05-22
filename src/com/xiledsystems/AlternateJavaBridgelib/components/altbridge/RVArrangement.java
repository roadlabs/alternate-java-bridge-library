package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ViewUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.RelativeLayout;


import com.xiledsystems.AlternateJavaBridgelib.components.Component;

public class RVArrangement extends AndroidViewComponent implements Component, ComponentContainer {

		
	  // Layout
	  private final int orientation;
	  private final RelativeLayout viewLayout;
	  
	  //AJB
	 	  
	  
	  public RVArrangement(ComponentContainer container, int orientation) {
		    super(container);
		    		    
		    this.orientation = orientation;
		    viewLayout = new RelativeLayout(container.$context());
		    		    
		    container.$add(this);
		  }
	  
	  public RVArrangement(ComponentContainer container, int orientation, int resourceId) {
		    super(container, resourceId);
		    		    
		    this.orientation = orientation;
		    viewLayout = (RelativeLayout) container.$context().findViewById(resourceId);
		    
		  }
	
	  
	// ComponentContainer implementation
	  
	  @Override
	  public Activity $context() {
	    return container.$context();
	  }

	  @Override
	  public Form $form() {
	    return container.$form();
	  }
	  
	  	  
	  public View getLayoutManager() {
		  if (resourceId!=-1) {
			  return (RelativeLayout) container.$form().findViewById(resourceId);
		  } else {
			  return viewLayout;
		  }
	  }
	  public void bringToFront(AndroidViewComponent component) {
		
		  if (resourceId!=-1) {
			  ((RelativeLayout) container.$form().findViewById(resourceId)).bringChildToFront(component.getView());
		  } else {
			  viewLayout.bringChildToFront(component.getView());
		  }
	  }
	  
	  /**
	   *  Assign an image to the background of this arrangement
	   *  
	   * @param resourceId The resource Id of the drawable to use
	   */
	  public void BackgroundImage(int resourceId) {
		  viewLayout.setBackgroundDrawable(container.$context().getResources().getDrawable(resourceId));
	  }
	  
	  /**
	   * Alternate method for setting the background image
	   * of this component. Mostly used with SpriteSheetHelper
	   * when managing multiple images in a sprite sheet.
	   * 
	   * @param drawable
	   */
	  public void Drawable(Drawable drawable) {
		  viewLayout.setBackgroundDrawable(drawable);
	  }
	  
	  /**
	   *  Assign a color to the background of this arrangement
	   *  
	   * @param color The color
	   */
	  public void BackgroundColor(int color) {
		  viewLayout.setBackgroundColor(color);
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
	  
		
	@Override
	public View getView() {
		if (resourceId!=-1) {
			return (RelativeLayout) container.$form().findViewById(resourceId);
		} else {
			return viewLayout;
		}		
	}
	
	@Override
	public void $add(AndroidViewComponent component) {
		
		component.setInRelArgmnt(true);
								
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		if (resourceId!=-1) {
			((RelativeLayout) container.$form().findViewById(resourceId)).addView(component.getView(), lp);
		} else {
			viewLayout.addView(component.getView(), lp);
		}
	}
	
	@Override
	public void postAnimEvent() {
		EventDispatcher.dispatchEvent(this, "AnimationMiddle");
		
	}


}
