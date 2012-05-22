package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.os.SystemClock;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;

public class TimeClock extends AndroidViewComponent implements OnChronometerTickListener, OnDestroyListener {

	private final Chronometer view;
	private boolean sendEvent;
	private boolean started;
	private StringBuilder mRecycle = new StringBuilder(8);
	
	
	public TimeClock(ComponentContainer container) {
		super(container);
		
		view = new Chronometer(container.$context());
		view.setOnChronometerTickListener(this);		
		
		container.$add(this);
		
		container.$form().registerForOnDestroy(this);
		
	}
	
	public TimeClock(ComponentContainer container, int resourceId) {
		super(container, resourceId);
		
		view = null;
		Chronometer c = (Chronometer) container.$context().findViewById(resourceId);
		c.setOnChronometerTickListener(this);		
		
		container.$form().registerForOnDestroy(this);
		
	}
	
	/**
	 * This sets the base time for the time clock.
	 * 
	 * @param time the base time in ms
	 */
	public void BaseTime(long time) {
		if (view != null) {
			view.setBase(time);
		} else {
			Chronometer c = (Chronometer) container.$context().findViewById(resourceId);
			c.setBase(time);
		}
	}
	
	/**
	 * This returns the current base time for the time clock.
	 * 
	 * @return the current base time in ms
	 */
	public long BaseTime() {
		if (view != null) {
			return view.getBase();
		} else {
			Chronometer c = (Chronometer) container.$context().findViewById(resourceId);
			return c.getBase();
		}
	}
	
	/**
	 * This returns the actual text that is displayed
	 * in the TimeClock. This is useful for getting
	 * what the current time is in the TimeClock (as when
	 * start() is called, it only updates the display, and
	 * doesn't update the base time).
	 * 
	 * @return String representing the displays time
	 */
	// This should just get removed, actually.
	private String DisplayTime() {
		long time;
		if (view != null) {
			time = SystemClock.elapsedRealtime() - view.getBase();
		} else {
			Chronometer c = (Chronometer) container.$context().findViewById(resourceId);
			time = SystemClock.elapsedRealtime() - c.getBase();
		}
		return DateUtils.formatElapsedTime(mRecycle, time);
	}
	
	/**
	 * Starts the time clock. Note that the android Chronometer which is the
	 * base of this component, will tick the time up on the display, but
	 * it will not affect the base time you set.
	 * 
	 */
	public void start() {
		if (view != null) {
			view.start();
		} else {
			Chronometer c = (Chronometer) container.$context().findViewById(resourceId);
			c.start();
		}
		started = true;
	}
	
	/**
	 * Stops the time clock.
	 */
	public void stop() {
		if (view != null) {
			view.stop();
		} else {
			Chronometer c = (Chronometer) container.$context().findViewById(resourceId);
			c.stop();
		}
		started = false;
	}
	
	/**
	 * Use this to easily reset the TimeClock's display to 0.
	 * This is the same as doing BaseTime(SystemClock.elapsedRealtime())
	 * 
	 */
	public void reset() {
		if (view != null) {
			view.setBase(SystemClock.elapsedRealtime());
		} else {
			Chronometer c = (Chronometer) container.$context().findViewById(resourceId);
			c.setBase(SystemClock.elapsedRealtime());
		}
	}
	
	/**
	 * Use this method to set the format of the time displayed.
	 * 
	 * @param format
	 */
	public void Format(String format) {
		if (view != null) {
			view.setFormat(format);			
		} else {
			Chronometer c = (Chronometer) container.$context().findViewById(resourceId);
			c.setFormat(format);
		}
	}
	
	/**
	 * 
	 * @return the string format of how the time is displayed
	 * 
	 */
	public String Format() {
		if (view != null) {
			return view.getFormat();
		} else {
			Chronometer c = (Chronometer) container.$context().findViewById(resourceId);
			return c.getFormat();
		}
	}
	
	/**
	 * Set this to true if you want the "Tick" event to be thrown
	 * upon each tick of the chronometer. This is off by default.
	 * 
	 * @param enable
	 */
	public void EnableEvent(boolean enable) {
		this.sendEvent = enable;
	}
	
	@Override
	public View getView() {
		
		return view;
	}

	@Override
	public void postAnimEvent() {
		
		EventDispatcher.dispatchEvent(this, "AnimationMiddle");
		
	}

	@Override
	public void onChronometerTick(Chronometer chronometer) {
		
		if (sendEvent) {
			EventDispatcher.dispatchEvent(this, "Tick");
		}		
	}
	
	@Override
	public void onDestroy() {
		if (started) {
			if (view != null) {
				view.stop();
			} else {
				Chronometer c = (Chronometer) container.$context().findViewById(resourceId);
				c.stop();
			}
			started = false;
		}
	}

	@Override
	protected void postAnimEvent(String event) {
		EventDispatcher.dispatchEvent(this, event);
	}

}
