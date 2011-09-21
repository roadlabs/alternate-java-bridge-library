package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import java.io.IOException;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.MediaUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;


/**
 * Not-so simple image-based Sprite.
 * 
 * This has been heavily modified from the app inventor bridge source. It includes a listener to automatically
 * resize the component. It adds the onTouch listener as well. It also gives you the ability to better
 * manage the memory hold. Use AutoToggle(true) to enable this feature. It will automatically disable the
 * sprite's timer when the activity it's in is paused, then re-enables it upon resume.
 * SpriteSheet animation has also been built into this component. It runs in it's own thread for as best
 * performance as possible. In order to use, you must set UseSheetAnim(true, numberofframes) , then use
 * Picture("picnamehere") to set the sprite up for it. Then you can use playAnimation(framespersecond) to
 * play the animation. use StopAnimation to stop it. If you want the animation to loop, you must
 * also set LoopAnimation(true).
 *
 */

public class ImageSprite extends Sprite implements OnStopListener, OnTouchListener, OnResumeListener, OnInitializeListener {
  
  private Drawable drawable;
  private int widthHint = LENGTH_PREFERRED;
  private int heightHint = LENGTH_PREFERRED;
  private String picturePath = "";  // Picture property
  private boolean rotates;
  private boolean sheetAnimation=false;
  private boolean autoToggle=true;
  private Matrix mat;
  public int currentFrame=1;
  private Bitmap unrotatedBitmap;
  private Bitmap rotatedBitmap;
  private BitmapDrawable rotatedDrawable;
  private double cachedRotationHeading;
  private boolean rotationCached;
  private int spriteWidth;
  private int spriteHeight;
  private int frameCount;
  private Rect sourceRect;
  private double fps;
  private boolean animRunning=false;
  private boolean running=false;
  private double framePeriod;
  private boolean loop;
  private Thread animThread;
  private Handler androidUIhandler = new Handler();
  private boolean autoResize= false;
  private double widthMultiplier;
  private double heightMultiplier;
  private final Form form;
  private boolean customAnim=false;
  private int customAnimFrames[];
  
    
  /**
   * Constructor for ImageSprite.
   *
   * @param container
   */
  public ImageSprite(ComponentContainer container) {
    super(container);
    form = container.$form();
    mat = new Matrix();
    rotates = true;
    rotationCached = false;
    
    form.registerForOnResume(this);
    form.registerForOnStop(this);
    form.registerForOnInitialize(this);
    frameCount=1;
    sourceRect = new Rect(0, 0, 0, 0);
    fps=10;
    
  }  
  
  public boolean isAnimRunning() {
	  return running;
  }
  	
	public boolean isLoopedAnimation() {
		return this.loop;
	}
	
	public void setCustomAnimFrames(int[] frames) {
		
		
		if (frames.length>1) {
			customAnimFrames = frames;
		} else {
			Toast.makeText(form.$context(), "Need more than one frame to animate!", Toast.LENGTH_LONG);
		}		
	}
	
	public void useCustomAnim(boolean customanim) {
		this.customAnim = customanim;
	}
	/**
	   * 
	   *  Set the image name for the sprite sheet animation, and the fps 
	   *  , or speed of the animation. Doing this negates any image
	   *  that was set with the Picture() method. 
	   *   
	   * @param picName a String ArrayList of the image filenames
	   * 
	   * @param framecount The amount of frames in the sprite sheet
	   * 
	   * @param fps frames per second; affects the speed of the
	   * animation
	   */

  public void SetSheetAnimandFPS(String picName, int framecount, int fps) {
	  this.rotates = false;
	  this.sheetAnimation = true;
	  this.frameCount = framecount;
	  setAnimBackground(picName);
  }
  
  private void setAnimBackground(String picName) {
		
	  try {
		  drawable = MediaUtil.getDrawable(form, picName);
		  // we'll need the bitmap for the drawable in order to rotate it
		  unrotatedBitmap = ((BitmapDrawable) drawable).getBitmap();
		  spriteWidth = unrotatedBitmap.getWidth() / this.frameCount;
		  spriteHeight = unrotatedBitmap.getHeight();
		  sourceRect.right = spriteWidth;
		  sourceRect.bottom = spriteHeight;
		 
	  } catch (IOException ioe) {
		  Log.e("ImageSprite", "Unable to load " + picName);
		  drawable = null;
		  unrotatedBitmap = null;
	  }

	  registerChange();	 
		
	}
  
  public int getFrame() {
	  
	  return currentFrame+1;	  
  }
  
  public void setFPS(double fps) {
	  this.fps = fps;
  }  

  public void setX(double x) {
	  xLeft = x;
  }
  
  public void setY(double y) {
	  yTop = y;
  }
  
  public void onDraw(android.graphics.Canvas canvas) {
	if (sheetAnimation) {
		
		if (unrotatedBitmap != null && visible) {
			int xinit = (int) Math.round(xLeft);
			int yinit = (int) Math.round(yTop);
			// The source Rect declares where in our image the currant frame is.
			// The dest Rect declares where on the canvas it should draw the bitmap
			// We can use this to stretch the image however you want.
	    	Rect destRect = new Rect(xinit, yinit, xinit + Width(), yinit + Height());
	    	canvas.drawBitmap(unrotatedBitmap, sourceRect, destRect, null);	    	
	    	    	
	    } 
		
	}  else {		
		
		if (unrotatedBitmap != null && visible) {
			int xinit = (int) Math.round(xLeft);
			int yinit = (int) Math.round(yTop);
			int w = Width();
			int h = Height();
			// 			If the sprite doesn't rotate,  use the original drawable
			// 	otherwise use the bitmapDrawable
			if (!rotates) {
				drawable.setBounds(xinit, yinit, xinit + w, yinit + h);
				drawable.draw(canvas);
			} else {
				// compute the new rotated image if the heading has changed
				if (!rotationCached || (cachedRotationHeading != Heading())) {
					// Set up the matrix for the rotation transformation
					// Rotate around the center of the sprite image (w/2, h/2)
					// TODO(user): Add a way for the user to specify the center of rotation.
					mat.setRotate((float) -Heading(), w / 2, h / 2);
					// Next create the rotated bitmap
					// Careful: We use getWidth and getHeight of the unrotated bitmap, rather than the
					// Width and Height of the sprite.  Doing the latter produces an illegal argument
					// exception in creating the bitmap, if the user sets the Width or Height of the
					// sprite to be larger than the image size.
					rotatedBitmap = Bitmap.createBitmap(
						unrotatedBitmap,
						0, 0,
						unrotatedBitmap.getWidth(), unrotatedBitmap.getHeight(),
						mat, true);
					// make a drawable for the rotated image and cache the heading
					rotatedDrawable = new BitmapDrawable(rotatedBitmap);
					cachedRotationHeading = Heading();
				}
				// Position the drawable:
				// We want the center of the image to remain fixed under the rotation.
				// To do this, we have to take account of the fact that, since the original
				// and the rotated bitmaps are rectangular, the offset of the center point from (0,0)
				// in the rotated bitmap will in general be different from the offset
				// in the unrotated bitmap.  Namely, rather than being 1/2 the width and height of the
				// unrotated bitmap, the offset is 1/2 the width and height of the rotated bitmap.
				// So when we display on the canvas, we  need to displace the upper left away
				// from (xinit, yinit) to take account of the difference in the offsets.
				rotatedDrawable.setBounds(
						xinit + w / 2 - rotatedBitmap.getWidth() / 2,
						yinit + h / 2 - rotatedBitmap.getHeight() / 2 ,
						// 	add in the width and height of the rotated bitmap
						// to get the other right and bottom edges
						xinit + w / 2 + rotatedBitmap.getWidth() / 2,
						yinit + h / 2 + rotatedBitmap.getHeight() / 2);
				rotatedDrawable.draw(canvas);
			} 			
		}		
	}
  }
  
  public void gotoFrame(int frame) {
	  
	  if (sheetAnimation) {
		  frame--;
		  if (frame>= frameCount) {
			  Toast.makeText(form.$context(), "Frame doesn't exist!", Toast.LENGTH_LONG);
		  } else {
			  currentFrame = frame;
			  sourceRect.left = currentFrame * spriteWidth;
			  sourceRect.right = sourceRect.left + spriteWidth;
			  registerChange();
		  }
	  }
  }
  
  public void upFrame() {
	  
	  if (sheetAnimation && currentFrame<frameCount-1) {
		  currentFrame++;
		  sourceRect.left = currentFrame * spriteWidth;
		  sourceRect.right = sourceRect.left + spriteWidth;
		  registerChange();
	  }	  
  }
  
  public void downFrame() {
	  
	  if (sheetAnimation && currentFrame>0) {
		  currentFrame--;
		  sourceRect.left = currentFrame * spriteWidth;
		  sourceRect.right = sourceRect.left + spriteWidth;
		  registerChange();
	  }
	  
  }
  
  public void curFrame(int frame) {
	  
	  if (frame>frameCount) {
		  this.currentFrame = 1;
	  } else {
		  this.currentFrame = frame;
	  }
  }
  
  public void startAnimation() {	  
	  	  
	  if (sheetAnimation && !animRunning) {
		  
		  framePeriod = 1000/fps;
		  running=true;
		  animRunning=true;
		  
		  
		  animThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
				
					long beginTime;
					long timeDiff;
					int sleepTime;
					int curframe; 
					int x = 0;
					while (running) {				
					// we declare our internal frame counter here. We cannot directly modify the
					// currentFrame variable, so we do it later by running the curFrame() method
					if (!customAnim) {
						curframe = currentFrame;
					 
					beginTime = System.currentTimeMillis();
					
					// Here we need to post to the UI's thread, as a seperate thread cannot touch
					// any views. While this may seem to defeat the purpose, the timing for the animation
					// is handled in a seperate thread, rather than the UI thread (which has other
					// stuff to process).
					
					androidUIhandler.post(new Runnable() {
						
						@Override
						public void run() {
							
							gotoFrame(currentFrame);							
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
					
					// increase frame to go to next frame of animation
					
					curframe++;	
									
					// If the current frame is higher than the framecount, go back to the first frame
					// If the animation is not set to loop, then it stops here.
					if (curframe>frameCount) {
						curframe = 1;
						if (!loop) {
							running = false;
							animRunning=false;
						}
					}
					// 	This increases the currentframe which resides outside of the thread.
					curFrame(curframe);
					} else {
							
							curframe = customAnimFrames[x];
							beginTime = System.currentTimeMillis();
							curFrame(curframe);
							androidUIhandler.post(new Runnable() {
								
								@Override
								public void run() {
									
									gotoFrame(currentFrame);							
								}
							});
							
							timeDiff = System.currentTimeMillis() - beginTime;
							sleepTime = (int) (framePeriod - timeDiff);
							
							if (sleepTime > 0) {
								try {
									
									Thread.sleep(sleepTime);
								} catch (InterruptedException e) {}
							}
							
							while (sleepTime < 0) {
								sleepTime += framePeriod;
							}
							
							x++;
							if (x>=customAnimFrames.length) {
								x = 0;
								if (!loop) {
									running = false;
									animRunning = false;
								}
							}
						}
					}
			}
		});				
		animThread.start();		  
	  }	  	  
  }

  /**
   * Returns the path of the sprite's picture
   *
   * @return  the path of the sprite's picture
   */
  
  public String Picture() {
    return picturePath;
  }
  
  
  /**
   * Specifies the path of the sprite's picture
   *
   * <p/>See {@link MediaUtil#determineMediaSource} for information about what
   * a path can be.
   *
   * @param path  the path of the sprite's picture
   */
  
  public void Picture(String path) {
	  picturePath = (path == null) ? "" : path;
	  		  
		  try {
			  drawable = MediaUtil.getDrawable(form, picturePath);
			  // we'll need the bitmap for the drawable in order to rotate it
			  unrotatedBitmap = ((BitmapDrawable) drawable).getBitmap();			  
			 
		  } catch (IOException ioe) {
			  Log.e("ImageSprite", "Unable to load " + picturePath);
			  drawable = null;
			  unrotatedBitmap = null;
		  }
	
    registerChange();
  }

  // The actual width/height of an ImageSprite whose Width/Height property is set to Automatic or
  // Fill Parent will be the width/height of the image.

  @Override
  
  public int Height() {
    if (heightHint == LENGTH_PREFERRED || heightHint == LENGTH_FILL_PARENT) {
      // Drawable.getIntrinsicWidth/Height gives weird values, but Bitmap.getWidth/Height works.
      // If drawable is a BitmapDrawable (it should be), we can get the Bitmap.
      if (drawable instanceof BitmapDrawable) {
        return ((BitmapDrawable) drawable).getBitmap().getHeight();
      }
      return (drawable != null) ? drawable.getIntrinsicHeight() : 0;
    }
    return heightHint;
  }

  @Override
  
  public void Height(int height) {
    heightHint = height;
    registerChange();
  }

  @Override
  
  public int Width() {
    if (widthHint == LENGTH_PREFERRED || widthHint == LENGTH_FILL_PARENT) {
      // Drawable.getIntrinsicWidth/Height gives weird values, but Bitmap.getWidth/Height works.
      // If drawable is a BitmapDrawable (it should be), we can get the Bitmap.
      if (drawable instanceof BitmapDrawable) {
        return ((BitmapDrawable) drawable).getBitmap().getWidth();
      }
      return (drawable != null) ? drawable.getIntrinsicWidth() : 0;
    }
    return widthHint;
  }

  @Override
  
  public void Width(int width) {
    widthHint = width;
    registerChange();
  }

  /**
   * Rotates property getter method.
   *
   * @return  {@code true} indicates that the image rotates to match the sprite's heading
   * {@code false} indicates that the sprite image doesn't rotate.
   */
  
  public boolean Rotates() {
    return rotates;
  }

  /**
   * Rotates property setter method
   *
   * @param rotates  {@code true} indicates that the image rotates to match the sprite's heading
   * {@code false} indicates that the sprite image doesn't rotate.
   */
  
    public void Rotates(boolean rotates) {
    this.rotates = rotates;
    registerChange();
  }
    
    public void LoopAnimation(boolean loop) {
		this.loop = loop;
	}
    
    public void stopAnimation() {
		
		this.running = false;
		this.animRunning = false;
		boolean retry = true;
		while (retry) {
			try {
				animThread.join();
				retry=false;
			} catch (InterruptedException e) {
				
			}
		}
		
	}
    
    public void AutoToggle(boolean autotoggle) {
    	this.autoToggle = autotoggle;
    }
    
    @Override
	public void onResume() {

		// We can now turn the timer back on
		if (autoToggle) {
			if (animRunning) {
				this.running=true;				
			}
			Enabled(true);
		}
		
	}

	@Override
	public void onStop() {

		// Turn the sprite's timer off to avoid memory leaks (this can be shut off by using AutoToggle(false);
		if (autoToggle) {
			if (animRunning || running) {
				this.running=false;
			}
			Enabled(false);
		}		
		
	}

	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction()==MotionEvent.ACTION_DOWN) {
			EventDispatcher.dispatchEvent(this, "DownState");
		}
		if (event.getAction()==MotionEvent.ACTION_UP) {
			EventDispatcher.dispatchEvent(this, "UpState");
		}
		return false;
	}
	
	 @Override
		public void onInitialize() {
			
			if (autoResize) {
				this.Width((int) (form.scrnWidth * widthMultiplier));
				this.Height((int) (form.scrnHeight * heightMultiplier));
			}
			
		}
		
		public void setMultipliers(double widthmultiplier, double heightmultiplier) {
			
			autoResize=true;
			this.widthMultiplier = widthmultiplier;
			this.heightMultiplier = heightmultiplier;			
		}		    
}
