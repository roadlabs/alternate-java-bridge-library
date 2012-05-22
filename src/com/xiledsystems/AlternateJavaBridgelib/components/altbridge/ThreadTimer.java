	package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.util.Log;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

public class ThreadTimer extends AndroidNonvisibleComponent implements OnResumeListener, OnDestroySvcListener, OnStopListener, OnDestroyListener {
	
	private final static String TAG = "ThreadTimer";
	private Thread thread;
	private boolean running=false;
	private boolean isRunning=false;
	private int interval=1000;
	private boolean autoToggle=false;	
	private final Runnable threadRunner;
	private boolean started;
	
	
	/**
	 * A timer which uses a seperate thread to process. This is ideal
	 * for heavy processing, without taxing the UI thread. This should
	 * be used for more complex computations. If you need to do something
	 * to the UI, you have to use the post(Runnable) method found in Form.
	 * 
	 * Throws a "Timer" event (thats where you put the code you want to run
	 * in the thread)
	 * 
	 * @param container The form/formservice this ThreadTimer is in
	 */
	public ThreadTimer(ComponentContainer container) {
		super(container);
		//thread = new Thread		
		threadRunner = new Runnable() {			
			@Override
			public void run() {				
				int sleepTime;
				long beginTime;
				long timeDiff;				
				while (running) {					
					// Here we setup a loop to keep running the dipatched event.
					
					beginTime = System.currentTimeMillis();
					
					dispatchTimerEvent();
					
					timeDiff = System.currentTimeMillis() - beginTime;
					
					sleepTime = (int) (interval - timeDiff);
					
					if (sleepTime > 0) {
						try {							
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {							
						}
					}					
				}								
			}			
		};		
		container.$form().registerForOnResume(this);
		container.$form().registerForOnStop(this);
	}
	
		
	/**
	 * A timer which uses a seperate thread to process. This is ideal
	 * for heavy processing, without taxing the UI thread. This should
	 * be used for more complex computations. If you need to do something
	 * to the UI, you have to use the post(Runnable) method found in Form.
	 * 
	 * @param container The form/formservice this ThreadTimer is in
	 */
	public ThreadTimer(SvcComponentContainer container) {
		super(container);
				
		threadRunner = new Runnable() {			
			@Override
			public void run() {				
				int sleepTime;
				long beginTime;
				long timeDiff;				
				while (running) {					
					// Here we setup a loop to keep running the dipatched event.
					if (Thread.interrupted()) {
						Log.e(TAG, "Thread Interrupted pre-sleep.");
						running = false;
						return;
					}
					beginTime = System.currentTimeMillis();
					
					dispatchTimerEvent();
					
					timeDiff = System.currentTimeMillis() - beginTime;
					
					sleepTime = (int) (interval - timeDiff);
					
					if (sleepTime > 0) {
						try {							
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {							
						}
					}					
				}								
			}			
		};
		
		container.$formService().registerForOnDestroy(this);
	}
	
	public ThreadTimer(ComponentContainer container, final Runnable action) {
		super(container);
		container.$form().registerForOnDestroy(this);
		threadRunner = new Runnable() {			
			@Override
			public void run() {				
				int sleepTime;
				long beginTime;
				long timeDiff;				
				while (running) {					
					// Here we setup a loop to keep running the dipatched event.
					if (Thread.interrupted()) {
						Log.e(TAG, "Thread Interrupted pre-sleep.");
						running = false;
						return;
					}
					beginTime = System.currentTimeMillis();
					
					runAction(action);
					
					timeDiff = System.currentTimeMillis() - beginTime;
					
					sleepTime = (int) (interval - timeDiff);
					
					if (sleepTime > 0) {
						try {							
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {							
						}
					}					
				}								
			}						
		};		
		container.$form().registerForOnResume(this);
		container.$form().registerForOnStop(this);
	}
	
	private void runAction(Runnable action) {
		container.$form().post(action);
	}
	
	/**
	 * 
	 * @return The interval of the timer in ms.
	 */
	public int Interval() {
		return this.interval;
	}
	
	/**
	 * This will set the timer to automatically stop when the form loses focus,
	 * and resume it when the form gains focus back.
	 * 
	 * Note: If this isn't set to true, you may end up leaking the reference to the
	 * Activity it runs in (so even if you think the activity is closed, it isn't, and
	 * stays in memory)
	 * 
	 * @param toggle Whether or not to automatically turn thread on/off (default is false)
	 */
	public void AutoToggle(boolean toggle) {
		this.autoToggle = toggle;
	}
	
	/**
	 * 
	 * @return Whether this thread timer has autotoggle enabled
	 */
	public boolean isAutoToggle() {
		return this.autoToggle;
	}
	
	/**
	 * 
	 * @param interval The interval in ms. (This is the time between timer fires)
	 */
	public void Interval(int interval) {
		this.interval = interval;
	}
	
	/**
	 * 
	 * @return Whether the timer is running or not.
	 */
	public boolean Enabled() {
		return this.running;
	}
	
	/**
	 * 
	 * @param enabled Start/stop the timer
	 */
	public void Enabled(boolean enabled) {
		// This was all added in an effort to safely shut down a thread.
		// After some testing, this is what seemed to work the best
		// in all scenarios (including the thread getting shutdown
		// while it's sleeping). Whatever is processed before the sleep
		// will run, then the thread will see the exception, and stop.
		// If the boolean passed is the same as what is already set
		// within, we do nothing.
		
		if (enabled != running) {
			started = enabled;
			if (running && enabled) {
				boolean retry = true;
				while (retry) {
					try {						
						thread.join();					
						retry=false;
						isRunning=false;
					} catch (InterruptedException e) {					
						Thread.State state = thread.getState();						
						if (state.equals(Thread.State.RUNNABLE)) {
							thread.interrupt();
							retry = false;
						}				
					}
				}			
			}
			this.running = enabled;
			if (running) {
				if (thread!=null) {
					Thread.State state = thread.getState();				
					
					if (!state.equals(Thread.State.NEW)) {
						thread = new Thread(threadRunner);
					}
				} else {
					thread = new Thread(threadRunner);
				}
				thread.start();			
				isRunning=true;
			} else {
				if (isRunning) {
					boolean retry = true;
					while (retry) {
						try {					
							thread.interrupt();
							thread.join();						
							retry=false;
							isRunning=false;
						} catch (InterruptedException e) {						
							Thread.State state = thread.getState();						
							if (state.equals(Thread.State.RUNNABLE)) {
								thread.interrupt();
								retry = false;
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * This is useful if you are just interested in running a one-off
	 * process in a seperate thread (this will run once, then destroy
	 * itself).
	 * 
	 * This is also useful as you don't need to instantiate ThreadTimer
	 * to use this method. Just use ThreadTimer.runOneTimeThread(run);
	 * 
	 * @param run The runnable to run in the thread.
	 */
	public static void runOneTimeThread(Runnable run) {
		  Thread thread = new Thread(run);
		  thread.start();	  
	}
	
	/**
	 * Use this method if you don't want the thread to loop.
	 * This will fire the Timer event only once. Note that once
	 * this is run, it cannot be stopped using Enabled(false).
	 * The thread will destroy itself automatically once it's done
	 * 
	 */
	public void OneTimeRun() {
		Thread thread = new Thread(new Runnable() {			
			@Override
			public void run() {
				dispatchTimerEvent();
			}
		});
		thread.start();		
	}
	
	private void dispatchTimerEvent() {
		
		EventDispatcher.dispatchEvent(this, "Timer");
		
	}

	@Override
	public void onStop() {		
		if (autoToggle) {
			this.running = false;
			boolean retry = true;
			while (retry) {
				try {
					thread.join();
					retry=false;
				} catch (InterruptedException e) {					
				}
			}
		}		
	}

	@Override
	public void onResume() {		
		if (autoToggle) {
			
			if (thread == null) {				
				thread = new Thread(threadRunner);
				if (started) {
					this.running = true;
					thread.start();
				}
				return;
			}
			this.running = true;
			Thread.State state = thread.getState();
			if (state.equals(Thread.State.TERMINATED) || state.equals(Thread.State.WAITING)) {
				thread = new Thread(threadRunner);
			}
			try {
				thread.start();
			} catch (IllegalThreadStateException e) {
				// Thread already started, just continue.
			}
		}		
	}

	@Override
	public void onDestroy() {		
		if (running) {
			this.running = false;
			boolean retry = true;
			while (retry) {
				try {
					thread.join();
					retry = false;
				} catch (InterruptedException e) {					
				}
			}
		}		
	}
}
