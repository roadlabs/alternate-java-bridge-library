package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;


public class Toggle extends AndroidViewComponent implements OnCheckedChangeListener {
	
	private ToggleButton view;
	
	/**
	 * Constructor for Toggle Button component.
	 * 
	 * @param container
	 */
	public Toggle(ComponentContainer container) {
		super(container);		
		view = new ToggleButton(container.$context());
		view.setOnCheckedChangeListener(this);
	}
	
	/**
	 * Constructor for Toggle Button component when using
	 * the GLE.
	 * 
	 * @param container
	 * @param resourceId
	 */
	public Toggle(ComponentContainer container, int resourceId) {
		super(container, resourceId);		
		view = null;		
		((ToggleButton)container.$context().findViewById(resourceId)).setOnCheckedChangeListener(this);
	}
	
	/**
	 * Set the text that is displayed when the toggler is
	 * in the on state.
	 * 
	 * @param text
	 */
	public void TextOn(String text) {
		if (view == null) {
			((ToggleButton)container.$context().findViewById(resourceId)).setTextOn(text);
		} else {
			view.setTextOn(text);
		}
	}
	
	/**
	 * 
	 * @return The text that is displayed when toggled on
	 */
	public String TextOn() {
		if (view == null) {
			return ((ToggleButton)container.$context().findViewById(resourceId)).getTextOn().toString();
		} else {
			return view.getTextOn().toString();
		}
	}
	
	/**
	 * Set the text that is displayed when the toggler is toggled off.
	 * 
	 * @param text
	 */
	public void TextOff(String text) {
		if (view == null) {
			((ToggleButton)container.$context().findViewById(resourceId)).setTextOff(text);
		} else {
			view.setTextOff(text);
		}
	}
	
	/**
	 * 
	 * @return The text displayed when the toggler is off.
	 */
	public String TextOff() {
		if (view == null) {
			return ((ToggleButton)container.$context().findViewById(resourceId)).getTextOff().toString();
		} else {
			return view.getTextOff().toString();
		}
	}
	
	/**
	 * Set if the toggler is on or off.
	 * 
	 * @param on
	 */
	public void ToggledOn(boolean toggledOn) {
		if (view == null) {
			((ToggleButton)container.$context().findViewById(resourceId)).setChecked(toggledOn);
		} else {
			view.setChecked(toggledOn);			
		}
	}
	
	/**
	 * 
	 * @return whether the toggler is on or off
	 */
	public boolean ToggledOn() {
		if (view == null) {
			return ((ToggleButton)container.$context().findViewById(resourceId)).isChecked();
		} else {
			return view.isChecked();
		}
	}

	@Override
	public View getView() {
		if (view == null) {
			return ((ToggleButton)container.$context().findViewById(resourceId));
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
