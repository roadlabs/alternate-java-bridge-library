package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.util.Collections;
import java.util.Comparator;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.Convert;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.Form;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.DoubleList;


public class ButtonStateHelper {

	private final Form form;
	private DoubleList stateimgs;
	private DoubleList statecolors;
	private DoubleList stateDraws;
	private final static int STATE_NAME = 0;
	private final static int STATE_IMG = 1;
	private final static int ENABLED_STATE = android.R.attr.state_enabled;
	private final static int DISABLED_STATE = -android.R.attr.state_enabled;
	private final static int PRESSED_STATE = android.R.attr.state_pressed;
	
	public ButtonStateHelper(Form form) {
		stateimgs = new DoubleList();
		statecolors = new DoubleList();
		stateDraws = new DoubleList();
		this.form = form;
	}
	
	/**
	 * 
	 *  Set the image for the button's enabled state.
	 *  
	 * @param image The name of the image (with, or without the extension)
	 */
	public void setEnabledImage(String image) {
		int[] state = new int[] {ENABLED_STATE};
		if (!stateimgs.getList(1).contains(state)) {
			stateimgs.add(state, image);
		} else {
			int position = stateimgs.getList(1).indexOf(state);			
			stateimgs.remove(position);
			stateimgs.add(state, image);
		}		
	}
	
	/**
	 * 
	 *  Set the image for the button's enabled state. Use this
	 *  method if you are using a sprite sheet for your
	 *  images.
	 *  
	 * @param draw 
	 */
	public void setEnabledDrawable(Drawable draw) {
		int[] state = new int[] {ENABLED_STATE};
		if (!stateDraws.getList(1).contains(state)) {
			stateDraws.add(state, draw);
		} else {
			int position = stateDraws.getList(1).indexOf(state);			
			stateDraws.remove(position);
			stateDraws.add(state, draw);
		}		
	}
	
	/**
	 * 
	 *  Set the color of the button's background when it's in the enabled state.
	 *  
	 * @param color
	 */
	public void setEnabledColor(int color) {
		int[] state = new int[] {ENABLED_STATE};
		if (!statecolors.getList(1).contains(ENABLED_STATE)) {
			statecolors.add(state, color);
		} else {
			int position = statecolors.getList(1).indexOf(state);			
			statecolors.remove(position);
			statecolors.add(state, color);
		}
	}
	
	/**
	 * 
	 *  Set the image for the button's disabled state.
	 *  
	 * @param image The name of the image (with, or without the extension)
	 */
	public void setDisabledImage(String image) {
		int[] state = new int[] {DISABLED_STATE};
		if (!stateimgs.getList(1).contains(state)) {
			stateimgs.add(state, image);
		} else {
			int position = stateimgs.getList(1).indexOf(state);			
			stateimgs.remove(position);
			stateimgs.add(state, image);
		}
	}
	
	/**
	 * 
	 *  Set the image for the button's disabled state. Use this
	 *  method if you are using a sprite sheet for your
	 *  images.
	 *  
	 * @param draw 
	 */
	public void setDisabledDrawable(Drawable draw) {
		int[] state = new int[] {DISABLED_STATE};
		if (!stateDraws.getList(1).contains(state)) {
			stateDraws.add(state, draw);
		} else {
			int position = stateDraws.getList(1).indexOf(state);			
			stateDraws.remove(position);
			stateDraws.add(state, draw);
		}
	}
	
	/**
	 * 
	 *  Set the color of the button's background when it's in the disabled state.
	 *  
	 * @param color
	 */
	public void setDisabledColor(int color) {
		int[] state = new int[] {DISABLED_STATE};
		if (!statecolors.getList(1).contains(DISABLED_STATE)) {
			statecolors.add(state, color);
		} else {
			int position = statecolors.getList(1).indexOf(state);			
			statecolors.remove(position);
			statecolors.add(state, color);
		}
	}
	
	/**
	 * 
	 *  Set the image for the button's pressed state.
	 *  
	 * @param image The name of the image (with, or without the extension)
	 */
	public void setPressedImage(String image) {
		int[] state = new int[] {PRESSED_STATE};
		if (!stateimgs.getList(1).contains(state)) {
			stateimgs.add(0, state, image);
		} else {
			int position = stateimgs.getList(1).indexOf(state);			
			stateimgs.remove(position);
			stateimgs.add(0, state, image);
		}
	}
	
	/**
	 * 
	 *  Set the image for the button's pressed state. Use this
	 *  method if you are using a sprite sheet for your
	 *  images.
	 *  
	 * @param draw 
	 */
	public void setPressedDrawable(Drawable draw) {
		int[] state = new int[] {PRESSED_STATE};
		if (!stateDraws.getList(1).contains(state)) {
			stateDraws.add(0, state, draw);
		} else {
			int position = stateDraws.getList(1).indexOf(state);			
			stateDraws.remove(position);
			stateDraws.add(0, state, draw);
		}
	}
	
	/**
	 * 
	 *  Set the color of the button's background when it's in the pressed state.
	 *  
	 * @param color
	 */
	public void setPressedColor(int color) {
		int[] state = new int[] {PRESSED_STATE};
		if (!statecolors.getList(1).contains(state)) {
			statecolors.add(state, color);
		} else {
			int position = statecolors.getList(1).indexOf(state);			
			statecolors.remove(position);
			statecolors.add(state, color);
		}
	}
	
	public StateListDrawable getImageDrawables() {
		int count = stateDraws.size();
		StateListDrawable drawstate = new StateListDrawable();
		for (int i = 0; i < count; i++) {
			drawstate.addState((int[]) stateDraws.get(i)[STATE_NAME], (Drawable) stateDraws.get(i)[STATE_IMG]);
		}
		
		return drawstate;
	}
	
	public StateListDrawable getImageStates() {
		int count = stateimgs.size();
		StateListDrawable drawstate = new StateListDrawable();		
		for (int i = 0; i < count; i++) {
			int resid = form.getResources().getIdentifier(stateimgs.get(i)[STATE_IMG].toString(), "drawable", form.getPackageName());
			drawstate.addState((int[]) stateimgs.get(i)[STATE_NAME], form.getResources().getDrawable(resid));
		}
		return drawstate;
	}
	
	public StateListDrawable getColorStates() {
		int count = statecolors.size();
		StateListDrawable drawstate = new StateListDrawable();		
		for (int i = 0; i < count; i++) {			
			drawstate.addState((int[]) statecolors.get(i)[STATE_NAME], form.getResources().getDrawable(Convert.Int(statecolors.get(i)[STATE_IMG])));
		}
		return drawstate;
	}
	
		
}
