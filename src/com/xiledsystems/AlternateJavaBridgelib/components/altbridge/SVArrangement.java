package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ViewUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;

import android.app.Activity;
import android.view.View;


public class SVArrangement extends AndroidViewComponent implements Component, ComponentContainer {

	private final Activity context;

	  // Layout
	  private final int orientation;
	  private final ScrollView viewLayout;
	  
	  private boolean autoResize= false;
	  private double widthMultiplier;
	  private double heightMultiplier;
	  private final Form form;

	  /**
	   * Creates a new HVArrangement component.
	   *
	   * @param container  container, component will be placed in
	   * @param orientation one of
	   *     {@link ComponentConstants#LAYOUT_ORIENTATION_HORIZONTAL}.
	   *     {@link ComponentConstants#LAYOUT_ORIENTATION_VERTICAL}
	  */
	  public SVArrangement(ComponentContainer container, int orientation) {
	    super(container);
	    form = container.$form();
	    context = container.$context();

	    this.orientation = orientation;
	    viewLayout = new ScrollView(context, orientation);
	    form.registerForOnInitialize(this);
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

	  // AndroidViewComponent implementation

	  @Override
	  public View getView() {
	    return viewLayout.getLayoutManager();
	  }
	  
	  @Override
		public void onInitialize() {
			
			if (autoResize) {
				Width((int) (form.scrnWidth * widthMultiplier));
				Height((int) (form.scrnHeight * heightMultiplier));
			}
			
		}
		
		public void setMultipliers(double widthmultiplier, double heightmultiplier) {
			
			autoResize=true;
			widthMultiplier = widthmultiplier;
			heightMultiplier = heightmultiplier;
			
		}

}
