package com.xiledsystems.AlternateJavaBridgelib;

import android.content.Intent;

import com.google.devtools.simple.runtime.components.android.ActivityResultListener;
import com.google.devtools.simple.runtime.components.android.ComponentContainer;
import com.google.devtools.simple.runtime.events.EventDispatcher;

public abstract class Picker2 extends ButtonBase2 implements ActivityResultListener {
	  protected final ComponentContainer container;

	  /* Used to identify the call to startActivityForResult. Will be passed back into the
	  resultReturned() callback method. */
	  protected int requestCode;

	  public Picker2(ComponentContainer container) {
	    super(container);
	    this.container = container;
	  }

	  /**
	   *  Provides the Intent used to launch the picker activity.
	   */
	  protected abstract Intent getIntent();

	  @Override
	  public void click() {
	    BeforePicking();
	    if (requestCode == 0) { // only need to register once
	      requestCode = container.$form().registerForActivityResult(this);
	    }
	    container.$context().startActivityForResult(getIntent(), requestCode);
	  }

	  // Functions

	  /**
	   * Opens the picker, as though the user clicked on it.
	   */
	 
	  public void Open() {
	    click();
	  }

	  // Events

	  /**
	   * Simple event to raise when the component is clicked but before the
	   * picker activity is started.
	   */
	  
	  public void BeforePicking() {
	    EventDispatcher.dispatchEvent(this, "BeforePicking");
	  }

	  /**
	   * Simple event to be raised after the picker activity returns its
	   * result and the properties have been filled in.
	   */
	  
	  public void AfterPicking() {
	    EventDispatcher.dispatchEvent(this, "AfterPicking");
	  }
	}
