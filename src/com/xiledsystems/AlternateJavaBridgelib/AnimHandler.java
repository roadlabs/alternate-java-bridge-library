package com.xiledsystems.AlternateJavaBridgelib;


import com.google.devtools.simple.runtime.components.android.AndroidNonvisibleComponent;
import com.google.devtools.simple.runtime.components.android.ComponentContainer;
import com.google.devtools.simple.runtime.components.android.Form;
import com.google.devtools.simple.runtime.components.android.OnResumeListener;
import com.google.devtools.simple.runtime.components.android.OnStopListener;

/*
 * 	AnimHandler - Class for handling an animation for an AnimSprite. This creates a seperate thread to
 * process the animation.
 * 
 * 
 */

public class AnimHandler extends AndroidNonvisibleComponent implements OnStopListener, OnResumeListener {
	
	private int frameCount;
	private int fps;
	private boolean animRunning=false;
	private boolean running=false;
	int currentFrame=1;
	private int framePeriod;
	private Form form;
	private boolean loop;
	private Thread animThread;
	
	
	public AnimHandler(ComponentContainer container, int frameCount, int fps) {
		
		super(container.$form());
		form = container.$form();
		this.frameCount = frameCount;
		this.fps = fps;
		
	}
	
	public void stopAnimation() {
				
		this.running = false;
		this.animRunning = false;
		animThread.stop();
		
	}
	
	public void LoopAnimation(boolean loop) {
		this.loop = loop;
	}
	
	public boolean isLoopedAnimation() {
		return this.loop;
	}
	
	public void playAnimation(final AnimSprite sprite) {
		
		if (!animRunning) {
			framePeriod = 1000/fps;
			running=true;
			animRunning=true;
			
				animThread = new Thread(new Runnable() {
					
					@Override
					public void run() {
					
						long beginTime;
						long timeDiff;
						int sleepTime;
						while (running) {
					
						
						beginTime = System.currentTimeMillis();
						// Here we need to post to the UI's thread, as a seperate thread cannot touch
						// any views.
						((Form2) form).post(new Runnable() {
							
							@Override
							public void run() {
								
								sprite.gotoFrame(currentFrame);
								
							}
						});
						// Get the time spent changing the sprite's image
						timeDiff = System.currentTimeMillis() - beginTime;
						
						// If there is time to spare between frames, go to sleep for this long
						sleepTime = (int) (framePeriod - timeDiff);
						
						if (sleepTime > 0) {
							try {
								
								Thread.sleep(sleepTime);
							} catch (InterruptedException e) {}
						}
						// to adjust if the system falls behind frames
						while (sleepTime < 0) {
							sleepTime += framePeriod;
						}
						currentFrame++;
						
						// If we have run through the animation, stop it, unless the loop boolean is set to true
						if (currentFrame>frameCount) {
							currentFrame = 1;
							if (!loop) {
								running = false;
								animRunning=false;
							}
						}
					}
				}
			});
					
			animThread.start();
			}
			
		}

	@Override
	public void onResume() {
		
		// Resume the thread, if animrunning is still true
		if (animRunning) {
			this.running=true;
		}
		
	}

	@Override
	public void onStop() {
		
		// Stop the thread from running when the activity stops. This will leave animrunning set to true.
		if (animRunning || running) {
			this.running=false;
		}
		
	}
	}


