package com.xiledsystems.AlternateJavaBridgelib.components.util;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.AndroidNonvisibleComponent;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.Form;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import android.os.Handler;
import android.util.Log;


public class UIEvent extends AndroidNonvisibleComponent {
	
	private static final String TAG = "UIEvent";	
	
	
	/**
	 * A helper component to run methods in the UI thread when working with
	 * ThreadTimers (so you can do things to the UI).
	 * 
	 * @param form The Form this UIEvent resides in. Always "this"
	 */
	public UIEvent(Form form) {
		super(form);
		
	}
	
	/**
	 * Use this method to fire an event to be run in the UI thread. Whatever
	 * you set the eventName to, is what you will need to capture in your form
	 * just like any other component. The only difference is that with this, it
	 * ensures the method is run in the UI thread, and also gives you the freedom
	 * to name the Event anything you want allowing you to use one instance of this
	 * class along with several threads to send multiple different events.
	 * 
	 * @param eventName The name to pass for the event.
	 */
	public void fireEvent(final String eventName) {
		if (eventName != null && !eventName.equals("")) {
			final UIEvent dis = this;
			Runnable eventRunner = new Runnable() {				
				@Override
				public void run() {
					EventDispatcher.dispatchEvent(dis, eventName);
				}
			};			
			Handler handler = new Handler();
			handler.post(eventRunner);
		} else {
			Log.e(TAG, "The event name must be at least one character.");
		}
	}	
	
}
