package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ViewUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;
import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;


import com.xiledsystems.AlternateJavaBridgelib.components.Component;

public class RVArrangement extends AndroidViewComponent implements Component, ComponentContainer {

		private final Activity context;

	  // Layout
	  private final int orientation;
	  private final RelativeLayout viewLayout;
	  
	  //AJB
	 
	  
	  
	  
	  
	  public RVArrangement(ComponentContainer container, int orientation) {
		    super(container);
		    
		    context = container.$context();

		    this.orientation = orientation;
		    viewLayout = new RelativeLayout(context);
		    		    
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
	  
	  	  
	  public void bringToFront(AndroidViewComponent component) {
		
		  viewLayout.bringChildToFront(component.getView());		  
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
		
		return viewLayout;
		
	}
	
	@Override
	public void $add(AndroidViewComponent component) {
		
		component.setInRelArgmnt(true);
								
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		viewLayout.addView(component.getView(), lp);
	}

}
