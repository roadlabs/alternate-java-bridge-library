package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

/**
 * Button with the ability to launch events on initialization, focus
 * change, or a user click.  It is implemented using
 * {@link android.widget.Button}.
 *
 */

public final class Button extends ButtonBase {

  /**
   * Creates a new Button component.
   *
   * @param container container, component will be placed in
   */
  public Button(ComponentContainer container) {
    super(container);
  }
  
  public Button(ComponentContainer container, int resourceId) {
	    super(container, resourceId);
  }

 @Override
  public void click() {
    // Call the users Click event handler. Note that we distinguish the click() abstract method
    // implementation from the Click() event handler method.
    Click();
  }

  /**
   * Indicates a user has clicked on the button.
   */
  
  public void Click() {
    EventDispatcher.dispatchEvent(this, "Click");
  }

  @Override
  public boolean longClick() {
    // Call the users Click event handler. Note that we distinguish the longclick() abstract method
    // implementation from the LongClick() event handler method.
    return LongClick();
  }

  /**
   * Indicates a user has long clicked on the button.
   */
  
  public boolean LongClick() {
    return EventDispatcher.dispatchEvent(this, "LongClick");
  }

}
