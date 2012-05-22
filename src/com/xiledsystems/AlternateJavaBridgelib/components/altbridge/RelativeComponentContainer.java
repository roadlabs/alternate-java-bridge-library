package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.app.Activity;

public interface RelativeComponentContainer {
	/**
	   * Returns the activity context (which can be retrieved from the root
	   * container - aka the form).
	   *
	   * @return  activity context
	   */
	  Activity $context();

	  /**
	   * Returns the form that ultimately contains this container.
	   *
	   * @return  form
	   */
	  Form $form();

	  /**
	   * Adds a component to a container.
	   *
	   * <p/>After this method is finished executing, the given component's view
	   * must have LayoutParams, even if the component cannot be added to the
	   * container until later.
	   *
	   * @param component  component associated with view
	   */
	  void $addview(AndroidViewComponent component, int x, int y);

	  void setChildWidth(AndroidViewComponent component, int width);

	  void setChildHeight(AndroidViewComponent component, int height);

}
