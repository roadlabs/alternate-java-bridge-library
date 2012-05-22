package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ViewUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.app.Activity;
import android.view.View;

/**
 * A container for components that arranges them in tabular form.
 *
 */

public class TableArrangement extends AndroidViewComponent
    implements Component, ComponentContainer {
  
  // Layout
  private final TableLayout viewLayout;

  //AJB
  private boolean autoResize= false;
  private double widthMultiplier;
  private double heightMultiplier;
  
  /**
   * Creates a new TableArrangement component.
   *
   * @param container  container, component will be placed in
  */
  public TableArrangement(ComponentContainer container) {
    super(container);
    
    viewLayout = new TableLayout(container.$context(), 2, 2);

    container.$form().registerForOnInitialize(this);
    container.$add(this);
  }
  
  /**
   * Creates a new TableArrangement component.
   *
   * @param container  container, component will be placed in
  */
  public TableArrangement(ComponentContainer container, int resourceId) {
    super(container);
    
    viewLayout = new TableLayout(container.$context(), 2, 2, resourceId);

    container.$form().registerForOnInitialize(this);
    container.$add(this);
  }

  /**
   * Columns property getter method.
   *
   * @return  number of columns in this layout
   */
  
  public int Columns() {
    return viewLayout.getNumColumns();
  }

  /**
   * Columns property setter method.
   *
   * @param numColumns  number of columns in this layout
   */
  
  public void Columns(int numColumns) {
    viewLayout.setNumColumns(numColumns);
  }

  /**
   * Rows property getter method.
   *
   * @return  number of rows in this layout
   */
  
  public int Rows() {
    return viewLayout.getNumRows();
  }

  /**
   * Rows property setter method.
   *
   * @param numRows  number of rows in this layout
   */
  
  public void Rows(int numRows) {
    viewLayout.setNumRows(numRows);
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

  @Override
  public void $add(AndroidViewComponent component) {
    viewLayout.add(component);
  }

  @Override
  public void setChildWidth(AndroidViewComponent component, int width) {
    ViewUtil.setChildWidthForTableLayout(component.getView(), width);
  }

  @Override
  public void setChildHeight(AndroidViewComponent component, int height) {
    ViewUtil.setChildHeightForTableLayout(component.getView(), height);
  }

  // AndroidViewComponent implementation

  @Override
  public View getView() {
    return viewLayout.getLayoutManager();
  }
  
  @Override
	public void onInitialize() {
		
		if (autoResize) {
			Width((int) (container.$form().scrnWidth * widthMultiplier));
			Height((int) (container.$form().scrnHeight * heightMultiplier));
		}
		
	}
	
	public void setMultipliers(double widthmultiplier, double heightmultiplier) {
		
		autoResize=true;
		widthMultiplier = widthmultiplier;
		heightMultiplier = heightmultiplier;
		
	}

	@Override
	public void postAnimEvent() {
		EventDispatcher.dispatchEvent(this, "AnimationMiddle");
		
	}

	  
}
