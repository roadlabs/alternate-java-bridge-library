package com.xiledsystems.AlternateJavaBridgelib;

import java.util.Set;

import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.components.android.Form;
import com.google.devtools.simple.runtime.components.android.collect.Sets;
import com.google.devtools.simple.runtime.components.android.util.ViewUtil;
import com.google.devtools.simple.runtime.events.EventDispatcher;

public class Form2 extends Form {
	
	public int scrnWidth;
	public int scrnHeight;
	private final Handler androidUIHandler = new Handler();
	private final Set<OnInitializeListener> initializeListeners = Sets.newHashSet();
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		//onSreenInitialize();
	}
	
	
	// Hopefully this will overload the initialize event making a second handler to perform the screen
	// resizing on UI components
	public int getScrnWidth() {
		
		return this.scrnWidth;
		
	}
	
	public int getScrnHeight() {
		
		return this.scrnHeight;
		
	}
	
	@Override
	public void Initialize() {
		super.Initialize();
	    // Dispatch the Initialize event only after the screen's width and height are no longer zero.
	    androidUIHandler.post(new Runnable() {
	      public void run() {
	    	setScrnVars();
	        if (scrnWidth != 0 && scrnHeight != 0) {
	          
	        	for (OnInitializeListener oninitializelistener : initializeListeners) {
	        		oninitializelistener.onInitialize();
	        	}
	          
	        } else {
	          // Try again later.
	          androidUIHandler.post(this);
	        }
	      }
	    });
	  }
	
	public void registerForOnInitialize(OnInitializeListener component) {
		initializeListeners.add(component);
	}

	public void setScrnVars() {
		
		Display display = getWindowManager().getDefaultDisplay();
		scrnWidth = display.getWidth();
		scrnHeight = display.getHeight();
		
	}
	
	

}
