package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class RadioCheckBoxGroup extends AndroidViewComponent implements OnCheckedChangeListener {

	private RadioGroup view;
	
	public RadioCheckBoxGroup(ComponentContainer container) {
		super(container);
		view = new RadioGroup(container.$context());
		view.setOnCheckedChangeListener(this);		
	}
	
	public RadioCheckBoxGroup(ComponentContainer container, int resourceId) {
		super(container, resourceId);		
		view = null;
		RadioGroup g = (RadioGroup) container.$context().findViewById(resourceId);
		g.setOnCheckedChangeListener(this);
	}	
	
	
	/**
	 * 
	 * This method is called when a radio checkbox is changed in the group.
	 * 
	 */
	public void Changed(int id) {
		EventDispatcher.dispatchEvent(this, "Changed", id);
	}
	
	/**
	 * 
	 *  Resets the group so that none are checked.
	 *  
	 */
	public void clearAll() {
		if (view != null) {
			view.clearCheck();
		} else {
			RadioGroup g = (RadioGroup) container.$context().findViewById(resourceId);
			g.clearCheck();
		}
		
	}
	
	/**
	 * 
	 * @return True if none of the radio checkbox's in this group
	 * are checked.
	 */
	public boolean isClear() {
		int x;
		if (view != null) {
			x = view.getChildCount();
		} else {
			RadioGroup g = (RadioGroup) container.$context().findViewById(resourceId);
			x = g.getChildCount();
		}
		RadioGroup v;
		if (view != null) {
			v = view;
		} else {
			RadioGroup g = (RadioGroup) container.$context().findViewById(resourceId);
			v = g;
		}
		for (int i = 0; i < x; i++) {
			if (((RadioButton)v.getChildAt(i)).isChecked()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This method returns the id of the selected RadioCheckBox in the group
	 * 
	 * @return
	 */
	public int SelectedRadio() {
		if (view != null) {
			return view.getCheckedRadioButtonId();
		} else {
			RadioGroup g = (RadioGroup) container.$context().findViewById(resourceId);
			return g.getCheckedRadioButtonId();
		}
	}
	
	@Override
	public View getView() {
		if (view != null) {
			return view;
		} else {
			RadioGroup g = (RadioGroup) container.$context().findViewById(resourceId);
			return g;
		}
		
	}

	@Override
	public void postAnimEvent() {
		EventDispatcher.dispatchEvent(this, "AnimationMiddle");
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Changed(checkedId);
	}

	
}
