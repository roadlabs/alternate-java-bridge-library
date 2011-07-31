package com.xiledsystems.AlternateJavaBridgelib;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

import com.google.devtools.simple.runtime.components.android.ButtonBase;
import com.google.devtools.simple.runtime.components.android.ComponentContainer;
import com.google.devtools.simple.runtime.events.EventDispatcher;

public final class Button2 extends ButtonBase2 {

	  /**
	   * Creates a new Button component.
	   *
	   * @param container container, component will be placed in
	   */
	
			
	  public Button2(ComponentContainer container) {
	    super(container);
	   
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