package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.Formatter;
import java.util.IllegalFormatException;
import java.util.Locale;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;


public class StopWatch extends Label implements OnDestroyListener {

	private static final String TAG = "StopWatch";
	private static final int TICK_WHAT = 3;
	private StringBuilder mRecycle = new StringBuilder(8);
	private StringBuilder mFormatBuilder;
	private Formatter mFormatter;
	private Locale mFormatterLocale;
	private Object[] mFormatterArgs = new Object[1];
	private String mFormat;
	private boolean mLogged;
	private boolean mRunning;
	private String displayText;	
    private boolean mStarted;	
	private long initialTime;
	private long currentTime;
		
	
	/**
	 * Constructor for stopwatch component.
	 * 
	 * @param container
	 */
	public StopWatch(ComponentContainer container) {
		super(container);
						
		container.$add(this);
		
		container.$form().registerForOnDestroy(this);
		
		//timer = new ThreadTimer(container, action);
		//timer.Interval(1000);
		//timer.Enabled(false);
		setTime(SystemClock.elapsedRealtime());
				
	}
	
	/**
	 * Constructor for stopwatch component when using the GLE.
	 * 
	 * @param container
	 * @param resourceId 
	 */
	public StopWatch(ComponentContainer container, int resourceId) {
		super(container, resourceId);
								
		container.$form().registerForOnDestroy(this);
						
		//timer = new ThreadTimer(container, action);
		//timer.Interval(1000);
		//timer.Enabled(false);
		setTime(SystemClock.elapsedRealtime());
	}
	
	/**
	 * Start the stop watch.
	 * 
	 * Note: This will continue to run even if the Form loses focus. It
	 * will only stop if the form is destroyed.
	 * 
	 */
	public void start() {
		//if (!timer.Enabled()) {
		//	timer.Enabled(true);
		//}
		mStarted = true;
		updateRunning();
	}
	
	/**
	 * Stop the stop watch
	 */
	public void stop() {
		//if (timer.Enabled()) {
		//	timer.Enabled(false);
		//}
		mStarted = false;
		updateRunning();
	}
	
	/**
	 * Reset the stopwatch to the initial time. (What you set with
	 * setTime(), or 0 if it wasn't set). This does NOT affect if the
	 * stop watch is started or stopped. It just resets the time (and
	 * updates the display).
	 * 
	 */
	public void reset() {
		currentTime = initialTime = SystemClock.elapsedRealtime();
		updateText(currentTime);
	}
	
	/**
	 * Change the format of the display of the time.
	 * Default is MM:SS, or H:MM:SS is none is set.
	 * 
	 * @param format
	 */
	public void Format(String format) {
		mFormat = format;
        if (format != null && mFormatBuilder == null) {
            mFormatBuilder = new StringBuilder(format.length() * 2);
        }
	}
	
	/**
	 * 
	 * @return the format, if one was manually set.
	 */
	public String Format() {
		return mFormat;
	}
	
	private synchronized void updateText(long now) {
		long seconds = now - initialTime;
		currentTime = now - initialTime;				
        seconds /= 1000;
        String text = DateUtils.formatElapsedTime(mRecycle, seconds);

        if (mFormat != null) {
            Locale loc = Locale.getDefault();
            if (mFormatter == null || !loc.equals(mFormatterLocale)) {
                mFormatterLocale = loc;
                mFormatter = new Formatter(mFormatBuilder, loc);
            }
            mFormatBuilder.setLength(0);
            mFormatterArgs[0] = text;
            try {
                mFormatter.format(mFormat, mFormatterArgs);
                text = mFormatBuilder.toString();
            } catch (IllegalFormatException ex) {
                if (!mLogged) {
                    Log.w(TAG, "Illegal format string: " + mFormat);
                    mLogged = true;
                }
            }
        }
        Text(text);
        displayText = text;
	}
	
	/**
	 * 
	 * @return The text displayed by the stop watch, including formatting.
	 */
	public String getDisplayTime() {
		return displayText;
	}
	
	/**
	 * This allows you to get the exact current time of the stop watch,
	 * in case you want to format it differently.
	 * 
	 * @param format
	 * @return the formatted time
	 */
	public String getCurrentTime(String format) {
		//Format(format);
		long seconds = currentTime /1000;
		String text = DateUtils.formatElapsedTime(mRecycle, seconds);

        if (format != null) {
            Locale loc = Locale.getDefault();
            if (mFormatter == null || !loc.equals(mFormatterLocale)) {
                mFormatterLocale = loc;
                mFormatter = new Formatter(mFormatBuilder, loc);
            }
            mFormatBuilder.setLength(0);
            mFormatterArgs[0] = text;
            try {
                mFormatter.format(format, mFormatterArgs);
                text = mFormatBuilder.toString();
            } catch (IllegalFormatException ex) {
                if (!mLogged) {
                    Log.w(TAG, "Illegal format string: " + format);
                    mLogged = true;
                }
            }
        }
        return text;
	}
	
	/**
	 * Set the initial time of the stop watch.
	 * Default is 0.
	 * 
	 * @param time
	 */
	public void setTime(long time) {
		initialTime = time;
		currentTime = time;		
		updateText(time);
	}
	
	private void updateRunning() {
        boolean running = Visible() && mStarted;
        if (running != mRunning) {
            if (running) {
                updateText(SystemClock.elapsedRealtime());                
                mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_WHAT), 1000);
            } else {
                mHandler.removeMessages(TICK_WHAT);
            }
            mRunning = running;
        }
    }
	
		
	@Override
	public void onDestroy() {
		super.onDestroy();
		Visible(false);
		updateRunning();
	}	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message m) {
            if (mRunning) {
                updateText(SystemClock.elapsedRealtime());                
                sendMessageDelayed(Message.obtain(this, TICK_WHAT), 1000);
            }
        }
	};	
	
}
