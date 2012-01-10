package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

public class ThreadTimer extends AndroidNonvisibleComponent implements OnResumeListener, OnStopListener {
	
	private Thread thread;
	private boolean running=false;
	private boolean isRunning=false;
	private int interval=1000;
	private boolean autoToggle=false;	
	
	public ThreadTimer(ComponentContainer container) {
		super(container.$form());
				
		thread = new Thread(new Runnable() {
			
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
						} catch (InterruptedException e) {}
					}
					
				}								
			}			
		});
		form.registerForOnResume(this);
		form.registerForOnStop(this);
	}
	
	public int Interval() {
		return this.interval;
	}
	
	public void AutoToggle(boolean toggle) {
		this.autoToggle = toggle;
	}
	
	public boolean isAutoToggle() {
		return this.autoToggle;
	}
	
	public void Interval(int interval) {
		this.interval = interval;
	}
	
	public boolean Enabled() {
		return this.running;
	}
	
	public void Enabled(boolean enabled) {
		
		this.running = enabled;
		if (running) {
			thread.start();
			isRunning=true;
		} else {
			if (isRunning) {
				boolean retry = true;
				while (retry) {
					try {
						thread.join();
						retry=false;
						isRunning=false;
					} catch (InterruptedException e) {
						
					}
				}
			}
		}
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
			this.running = true;
			thread.start();
		}
		
	}

}
