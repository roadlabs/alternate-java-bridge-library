package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SliderBar extends AndroidViewComponent implements OnSeekBarChangeListener {

	private final SeekBar view;
		
	
	/**
	 * 
	 * Non GLE component constructor
	 * 
	 * @param container The container to put the SliderBar into
	 */
	protected SliderBar(ComponentContainer container) {
		super(container);
		view = new SeekBar(container.$context());
		view.setOnSeekBarChangeListener(this);		
		container.$add(this);		
	}
	
	/**
	 * 
	 * GLE component constructor.
	 * 
	 * @param container Use this
	 * @param resourceId The resource id of the sliderbar you placed in the GLE
	 */
	protected SliderBar(ComponentContainer container, int resourceId) {
		super(container, resourceId);
		view = null;		
		SeekBar bar = (SeekBar) container.$form().findViewById(resourceId);
		bar.setOnSeekBarChangeListener(this);		
	}
	
	/**
	 * 
	 * @return The int of the max position (all the way to the right)
	 */
	public int PositionMax() {
		if (resourceId != -1) {
			return ((SeekBar) container.$form().findViewById(resourceId)).getMax() + 1;
		} else {
			return view.getMax() + 1;
		}
	}
	
	/**
	 * 
	 * @param max integer representing the max of the bar (default is 100)
	 */
	public void PositionMax(int max) {
		if (resourceId != -1) {
			((SeekBar) container.$form().findViewById(resourceId)).setMax(max+1);
		} else {
			view.setMax(max + 1);
		}
	}
	
	/**
	 * 
	 * @return the position of the bar
	 */
	public int Position() {
		if (resourceId != -1) {
			return ((SeekBar) container.$form().findViewById(resourceId)).getProgress() + 1;
		} else {
			return view.getProgress() + 1;
		}
	}
	
	/**
	 * 
	 * @param position the position of the bar
	 */
	public void Position(int position) {
		if (position<1) {
			position = 1;
		}
		if (resourceId!= -1) {
			((SeekBar) container.$form().findViewById(resourceId)).setProgress(position-1);
		} else {
			view.setProgress(position-1);
		}
	}	
	
	@Override
	public View getView() {		
		if (resourceId != -1) {
			return (SeekBar) container.$form().findViewById(resourceId);
		} else {
			return view;
		}
	}

	@Override
	public void postAnimEvent() {
		
		EventDispatcher.dispatchEvent(this, "AnimationMiddle");

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		
		EventDispatcher.dispatchEvent(this, "PositionChanged", fromUser);
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

}
