package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;


import java.util.ArrayList;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnInitializeListener;
import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;
import com.xiledsystems.AlternateJavaBridgelib.components.VisibleComponent;

import android.view.View;

/**
 * Underlying base class for all components with views; not accessible to Simple programmers.
 * <p>
 * Provides implementations for standard properties and events.
 *
 */

public abstract class AndroidViewComponent extends VisibleComponent implements OnInitializeListener {

  protected final ComponentContainer container;

  private int lastSetWidth = LENGTH_UNKNOWN;
  private int lastSetHeight = LENGTH_UNKNOWN;

  private int column = ComponentConstants.DEFAULT_ROW_COLUMN;
  private int row = ComponentConstants.DEFAULT_ROW_COLUMN;
  
  
  
  // For animationdrawable implementation
  public boolean looping;
  public ArrayList<String> filelist;
  public int fps;
  public int curFrame;

  /**
   * Creates a new AndroidViewComponent.
   *
   * @param container  container, component will be placed in
   */
  protected AndroidViewComponent(ComponentContainer container) {
    this.container = container;
    
  }

  /**
   * Returns the {@link View} that is displayed in the UI.
   */
  public abstract View getView();

  /**
   * Returns true iff the component is visible.
   * @return  true iff the component is visible
   */
  
  public boolean Visible() {
    return getView().getVisibility() == View.VISIBLE;
  }

  /**
   * Specifies whether the component should be visible
   * @param  visible desired state
   */
  
  public void Visible(boolean visible) {
    // The principle of least astonishment suggests we not offer the
    // Android option INVISIBLE.
    getView().setVisibility(visible ? View.VISIBLE : View.GONE);
  }

  /**
   * Returns the component's horizontal width, measured in pixels.
   *
   * @return  width in pixels
   */
  @Override
  
  public int Width() {
    return getView().getWidth();
  }

  /**
   * Specifies the component's horizontal width, measured in pixels.
   *
   * @param  width in pixels
   */
  @Override
  
  public void Width(int width) {
    container.setChildWidth(this, width);
    lastSetWidth = width;
  }

  /**
   * Copy the width from another component to this one.  Note that we don't use
   * the getter method to get the property value from the source because the
   * getter returns the computed width whereas we want the width that it was
   * last set to.  That's because we want to preserve values like
   * LENGTH_FILL_PARENT and LENGTH_PREFERRED
   *
   * @param sourceComponent the component to copy from
   */
  
  public void CopyWidth(AndroidViewComponent sourceComponent) {
    Width(sourceComponent.lastSetWidth);
  }

  /**
   * Returns the component's vertical height, measured in pixels.
   *
   * @return  height in pixels
   */
  @Override
  
  public int Height() {
    return getView().getHeight();
  }

  /**
   * Specifies the component's vertical height, measured in pixels.
   *
   * @param  height in pixels
   */
  @Override
 
  public void Height(int height) {
    container.setChildHeight(this, height);
    lastSetHeight = height;
  }

  /**
   * Copy the height from another component to this one.  Note that we don't use
   * the getter method to get the property value from the source because the
   * getter returns the computed width whereas we want the width that it was
   * last set to.  That's because we want to preserve values like
   * LENGTH_FILL_PARENT and LENGTH_PREFERRED
   *
   * @param sourceComponent the component to copy from
   */
  
  public void CopyHeight(AndroidViewComponent sourceComponent) {
    Height(sourceComponent.lastSetHeight);
  }

  /**
   * Column property getter method.
   *
   * @return  column property used by the table arrangement
   */
  
  public int Column() {
    return column;
  }

  /**
   * Column property setter method.
   *
   * @param column  column property used by the table arrangement
   */
  
  public void Column(int column) {
    this.column = column;
  }

  /**
   * Row property getter method.
   *
   * @return  row property used by the table arrangement
   */
  
  public int Row() {
    return row;
  }

  /**
   * Row property setter method.
   *
   * @param row  row property used by the table arrangement
   */
  
  public void Row(int row) {
    this.row = row;
  }

  // Component implementation

  @Override
  public HandlesEventDispatching getDispatchDelegate() {
    return container.$form();
  }
  
 
}
