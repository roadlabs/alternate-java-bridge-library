package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;


public class Switcher extends AndroidViewComponent implements OnCheckedChangeListener {
	
	private final Switch view;
	
	
	/**
	 * Constructor for Switcher component. 
	 * THIS COMPONENT REQUIRES API 14 (ICS). This will
	 * NOT work on devices with an android OS less than
	 * 4.0.
	 * 
	 * @param container
	 */
	public Switcher(ComponentContainer container) {
		super(container);
		
		view = new Switch(container.$context());
		view.setOnCheckedChangeListener(this);
	}
	
	/**
	 * Constructor for Switcher component when using GLE. 
	 * THIS COMPONENT REQUIRES API 14 (ICS). This will
	 * NOT work on devices with an android OS less than
	 * 4.0.
	 * 
	 * @param container
	 * @param resourceId
	 */
	public Switcher(ComponentContainer container, int resourceId) {
		super(container, resourceId);
		
		view = null;
		Switch s = (Switch) container.$context().findViewById(resourceId);
		s.setOnCheckedChangeListener(this);
	}
	
	/**
	 * Set the text that is displayed when the switcher is
	 * in the switched on state.
	 * 
	 * @param text
	 */
	public void TextOn(String text) {
		if (view == null) {
			((Switch)container.$context().findViewById(resourceId)).setTextOn(text);
		} else {
			view.setTextOn(text);
		}
	}
	
	/**
	 * 
	 * @return The text that is displayed when switched on
	 */
	public String TextOn() {
		if (view == null) {
			return ((Switch)container.$context().findViewById(resourceId)).getTextOn().toString();
		} else {
			return view.getTextOn().toString();
		}
	}
	
	/**
	 * Set the text that is displayed when the switcher is switched off.
	 * 
	 * @param text
	 */
	public void TextOff(String text) {
		if (view == null) {
			((Switch)container.$context().findViewById(resourceId)).setTextOff(text);
		} else {
			view.setTextOff(text);
		}
	}
	
	/**
	 * 
	 * @return The text displayed when the switcher is off.
	 */
	public String TextOff() {
		if (view == null) {
			return ((Switch)container.$context().findViewById(resourceId)).getTextOff().toString();
		} else {
			return view.getTextOff().toString();
		}
	}
	
	/**
	 * Set if the switcher is on or off.
	 * 
	 * @param on
	 */
	public void SwitchedOn(boolean on) {
		if (view == null) {
			((Switch)container.$context().findViewById(resourceId)).setChecked(on);
		} else {
			view.setChecked(on);			
		}
	}
	
	/**
	 * 
	 * @return whether the switcher is on or off
	 */
	public boolean SwitchedOn() {
		if (view == null) {
			return ((Switch)container.$context().findViewById(resourceId)).isChecked();
		} else {
			return view.isChecked();
		}
	}

	@Override
	public View getView() {	
		if (view == null) {
			return container.$context().findViewById(resourceId);
		} else {
			return view;
		}
	}

	@Override
	public void postAnimEvent() {
		EventDispatcher.dispatchEvent(this, "AnimationMiddle");
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		EventDispatcher.dispatchEvent(this, "Changed", isChecked);		
	}

}
