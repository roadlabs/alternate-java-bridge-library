package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.Form;
import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.DonutUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.FileUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.MediaUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.PaintUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.SdkLevel;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ViewUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.util.BoundingBox;
import com.xiledsystems.AlternateJavaBridgelib.components.util.ErrorMessages;

/**
 * A two-dimensional touch-sensitive rectangular panel on which drawing can
 * be done and sprites can be moved.
 *
 */

public final class AnimCanvas extends AndroidViewComponent implements ComponentContainer, OnResumeListener, OnStopListener {
  
  private final CanvasView view;

  // Android can't correctly give the width and height of a canvas until
  // something has been drawn on it.
  private boolean drawn;
  // AJB change - vars for resizing component
    
  private ArrayList<String> picList;

  // Variables behind properties
  private int paintColor;
  private final Paint paint;
  private int backgroundColor;
  private final Paint backgroundPaint;
  private String backgroundImagePath = "";
  private Drawable backgroundDrawable;
  private AnimationDrawable animBackground;
  private int textAlignment;
  private int fps=100;
  private boolean animRunning=false;
  private boolean firstrun=true;
  private boolean autoToggle=true;
  
  

  private static final float DEFAULT_LINE_WIDTH = 2;

  // Keep track of enclosed sprites
  private final List<Sprite> sprites;

  // Handle touches and drags
  private final MotionEventParser motionEventParser;

  /**
   * Parser for Android {@link android.view.MotionEvent} sequences, which calls
   * the appropriate event handlers.  Specifically:
   * <ul>
   * <li> If a {@link android.view.MotionEvent#ACTION_DOWN} is followed by one
   * or more {@link android.view.MotionEvent#ACTION_MOVE} events, a sequence of
   * {@link Sprite#Dragged(float, float, float, float, float, float)}
   * calls are generated for sprites that were touched, and the final
   * {@link android.view.MotionEvent#ACTION_UP} is ignored.
   *
   * <li> If a {@link android.view.MotionEvent#ACTION_DOWN} is followed by an
   * {@link android.view.MotionEvent#ACTION_UP} event either immediately or
   * after {@link android.view.MotionEvent#ACTION_MOVE} events that take it no
   * further than {@link #TAP_THRESHOLD} pixels horizontally or vertically from
   * the start point, it is interpreted as a touch, and a single call to
   * {@link Sprite#Touched(float, float)} for each touched sprite is
   * generated.
   * </ul>
   *
   * After the {@code Dragged()} or {@code Touched()} methods are called for
   * any applicable sprites, a call is made to
   * {@link Canvas_backup#Dragged(float, float, float, float, float, float, boolean)}
   * or {@link Canvas_backup#Touched(float, float, boolean)}, respectively.  The
   * additional final argument indicates whether it was preceded by one or
   * more calls to a sprite, i.e., whether the locations on the canvas had a
   * sprite on them ({@code true}) or were empty of sprites {@code false}).
   *
   *
   */
  class MotionEventParser {
    /**
     * The number of pixels right, left, up, or down, a sequence of drags must
     * move from the starting point to be considered a drag (instead of a
     * touch).
     */
    public static final int TAP_THRESHOLD = 30;

    /**
     * The width of a finger.  This is used in determining whether a sprite is
     * touched.  Specifically, this is used to determine the horizontal extent
     * of a bounding box that is tested for collision with each sprite.  The
     * vertical extent is determined by {@link #FINGER_HEIGHT}.
     */
    public static final int FINGER_WIDTH = 24;

    /**
     * The width of a finger.  This is used in determining whether a sprite is
     * touched.  Specifically, this is used to determine the vertical extent
     * of a bounding box that is tested for collision with each sprite.  The
     * horizontal extent is determined by {@link #FINGER_WIDTH}.
     */
    public static final int FINGER_HEIGHT = 24;

    private static final int HALF_FINGER_WIDTH = FINGER_WIDTH / 2;
    private static final int HALF_FINGER_HEIGHT = FINGER_HEIGHT / 2;

    /**
     * The set of sprites encountered in a touch or drag sequence.  Checks are
     * only made for sprites at the endpoints of each drag.
     */
    private final List<Sprite> draggedSprites = new ArrayList<Sprite>();

    // startX and startY hold the coordinates of where a touch/drag started
    private static final int UNSET = -1;
    private float startX = UNSET;
    private float startY = UNSET;

    // lastX and lastY hold the coordinates of the previous step of a drag
    private float lastX = UNSET;
    private float lastY = UNSET;

    private boolean drag = false;

    void parse(MotionEvent event) {
      int width = Width();
      int height = Height();

      // Coordinates less than 0 can be returned if a move begins within a
      // view and ends outside of it.  Because negative coordinates would
      // probably confuse the user (as they did me) and would not be useful,
      // we replace any negative values with zero.
      float x = Math.max(0, (int) event.getX());
      float y = Math.max(0, (int) event.getY());

      // Also make sure that by adding or subtracting a half finger that
      // we don't go out of bounds.
      BoundingBox rect = new BoundingBox(
          Math.max(0, (int) x - HALF_FINGER_HEIGHT),
          Math.max(0, (int) y - HALF_FINGER_WIDTH),
          Math.min(width - 1, (int) x + HALF_FINGER_WIDTH),
          Math.min(height - 1, (int) y + HALF_FINGER_HEIGHT));

      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          draggedSprites.clear();
          startX = x;
          startY = y;
          lastX = x;
          lastY = y;
          drag = false;
          for (Sprite sprite : sprites) {
            if (sprite.Enabled() && sprite.Visible() && sprite.intersectsWith(rect)) {
              draggedSprites.add(sprite);
              if (sprite instanceof ImageSprite) {
            	  ((ImageSprite) sprite).requestDownEvent();
              }
            }
          } 
          
          break;

        case MotionEvent.ACTION_MOVE:
          // Ensure that this was preceded by an ACTION_DOWN
          if (startX == UNSET || startY == UNSET || lastX == UNSET || lastY == UNSET) {
            Log.w("Canvas", "In Canvas.MotionEventParser.parse(), " +
                "an ACTION_MOVE was passed without a preceding ACTION_DOWN: " + event);
          }

          // If the new point is near the start point, it may just be a tap
          if (Math.abs(x - startX) < TAP_THRESHOLD && Math.abs(y - startY) < TAP_THRESHOLD) {
            break;
          }
          // Otherwise, it's a drag.
          drag = true;

          // Update draggedSprites by adding any that are currently being
          // touched.
          for (Sprite sprite : sprites) {
            if (!draggedSprites.contains(sprite)
                && sprite.Enabled() && sprite.Visible()
                && sprite.intersectsWith(rect)) {
              draggedSprites.add(sprite);
            }
          }

          // Raise a Dragged event for any affected sprites
          boolean handled = false;
          for (Sprite sprite : draggedSprites) {
            if (sprite.Enabled() && sprite.Visible()) {
              sprite.Dragged(startX, startY, lastX, lastY, x, y);
              handled = true;
            }
          }

          // Last argument indicates whether a sprite handled the drag
          Dragged(startX, startY, lastX, lastY, x, y, handled);
          lastX = x;
          lastY = y;
          break;

        case MotionEvent.ACTION_UP:
          // If we never strayed far from the start point, it's a tap.  (If we
          // did stray far, we've already handled the movements in the ACTION_MOVE
          // case.)
          if (!drag) {
            // It's a tap
            handled = false;
            for (Sprite sprite : draggedSprites) {
              if (sprite.Enabled() && sprite.Visible()) {
                sprite.Touched(startX, startY);
                handled = true;
                if (sprite instanceof ImageSprite) {
                	((ImageSprite) sprite).requestUpEvent();
                }
              }
            }
            // Last argument indicates that one or more sprites handled the tap
            Touched(startX, startY, handled);
          }

          // Prepare for next drag
          drag = false;
          startX = UNSET;
          startY = UNSET;
          lastX = UNSET;
          lastY = UNSET;
          break;
      }
    }
  }

  /**
   * Panel for drawing and manipulating sprites.
   *
   */
  private final class CanvasView extends SurfaceView implements SurfaceHolder.Callback {
    private android.graphics.Canvas canvas;
    public Bitmap bitmap;
    
    private CanvasThread thread;

    public CanvasView(Context context) {
      super(context);
      bitmap = Bitmap.createBitmap(ComponentConstants.CANVAS_PREFERRED_WIDTH,
                                   ComponentConstants.CANVAS_PREFERRED_HEIGHT,
                                   Bitmap.Config.ARGB_8888);
      canvas = new android.graphics.Canvas(bitmap);
      getHolder().addCallback(this);
      
    }

    class CanvasThread extends Thread {
    	
    	private SurfaceHolder surfaceHolder;    	
    	private boolean _run = false;    	
    	
    	public CanvasThread(SurfaceHolder surface, CanvasView canvas) {
    		surfaceHolder = surface;    		    		
    	}
    	
    	public void setRunning(boolean run) {
    		_run = run;
    	}
    	
    	 public void doDraw(android.graphics.Canvas canvas0) {    		 	
    	    	canvas0.drawBitmap(bitmap, 0, 0, null);
    	    	for (Sprite sprite : sprites) {
    	    		sprite.onDraw(canvas0);    	    		
    	    	}    	        	    	
    	    	drawn = true;    	    	
    	    }
    	
    	@Override
    	public void run() {
    		android.graphics.Canvas c;
    		double lastrun = 0;
    		while (_run) {
    			if (lastrun+25<System.currentTimeMillis()) {
    				c = null;
    				try {
    					c = surfaceHolder.lockCanvas(null);
    					synchronized (surfaceHolder) {
    						doDraw(c);	    						    						
    					}
    				} finally {
    					if (c != null) {
    						surfaceHolder.unlockCanvasAndPost(c);
    						lastrun = System.currentTimeMillis();
    					}
    				}
    			}
    		}
    	}
    }
    

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      int preferredWidth;
      int preferredHeight;
      if (backgroundDrawable != null) {
        // Drawable.getIntrinsicWidth/Height gives weird values, but Bitmap.getWidth/Height works.
        // If backgroundDrawable is a BitmapDrawable (it should be), we can get the Bitmap.
        if (backgroundDrawable instanceof BitmapDrawable) {
          Bitmap bitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();
          preferredWidth = bitmap.getWidth();
          preferredHeight = bitmap.getHeight();
        } else {
          preferredWidth = backgroundDrawable.getIntrinsicWidth();
          preferredHeight = backgroundDrawable.getIntrinsicHeight();
        }
      } else {
        preferredWidth = ComponentConstants.CANVAS_PREFERRED_WIDTH;
        preferredHeight = ComponentConstants.CANVAS_PREFERRED_HEIGHT;
      }
      setMeasuredDimension(getSize(widthMeasureSpec, preferredWidth),
          getSize(heightMeasureSpec, preferredHeight));
      
    }

    private int getSize(int measureSpec, int preferredSize) {
      int result;
      int specMode = MeasureSpec.getMode(measureSpec);
      int specSize = MeasureSpec.getSize(measureSpec);

      if (specMode == MeasureSpec.EXACTLY) {
        // We were told how big to be
        result = specSize;
      } else {
        // Use the preferred size.
        result = preferredSize;
        if (specMode == MeasureSpec.AT_MOST) {
          // Respect AT_MOST value if that was what is called for by measureSpec
          result = Math.min(result, specSize);
        }
      }

      return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
      // The following call results in the Form not grabbing our events and
      // handling dragging on its own, which it wants to do to handle scrolling.
      // Its effect only lasts long as the current set of motion events
      // generated during this touch and drag sequence.  Consequently, it needs
      // to be called here, so that it happens for each touch-drag sequence.
      container.$form().dontGrabTouchEventsForComponent();
      motionEventParser.parse(event);
      return true;
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		view.bitmap = Bitmap.createScaledBitmap(view.bitmap, width, height, true);
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread = new CanvasThread(getHolder(), this);
		thread.setRunning(true);
		thread.start();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
				
			}
		}
		
	}
  }

  public AnimCanvas(ComponentContainer container) {
    super(container);
    
    // Create view and add it to its designated container.
    view = new CanvasView(container.$context());
    container.$add(this);
        
    paint = new Paint();
    backgroundPaint = new Paint();

    // Set default properties.
    paint.setStrokeWidth(DEFAULT_LINE_WIDTH);
    PaintColor(Component.COLOR_BLACK);
    BackgroundColor(Component.COLOR_WHITE);
    TextAlignment(Component.ALIGNMENT_NORMAL);
    FontSize(Component.FONT_DEFAULT_SIZE);

    sprites = new ArrayList<Sprite>();
    motionEventParser = new MotionEventParser();
    picList = new ArrayList<String>();
  }

  private void clearViewCanvas() {
    // We avoid drawing the default background color over an explicit background image.
    if (backgroundDrawable == null && animBackground == null) {
      // There is no background image.
      // Fill the view.canvas with the background color.
      view.canvas.drawPaint(backgroundPaint);
    } else {
      // There is a background image. It has already been set on the view.
      // Fill the view.canvas with transparent.
      view.canvas.drawColor(0, PorterDuff.Mode.CLEAR);
    }
    view.invalidate();
  }
  
  /**
   * 
   *  Set the list of image names for the animation, and the fps 
   *  , or speed of the animation. Doing this negates any image
   *  that was set with the BackgroundImage() method. 
   *   
   * @param piclist a String ArrayList of the image filenames
   * 
   * @param fps frames per second, affects the speed of the
   * animation
   */
  
  public void setAnimListandFPS(ArrayList<String> piclist, int fps) {
	  this.picList = piclist;
	  this.fps = 1000/fps;
	  setAnimBackground();
	  
  }
  
  /**
   * 
   *  Start the animation
   */
  
  public void startAnimation() {
	  if (!animRunning) {
		  if (firstrun) {
			  ViewUtil.setBackgroundImage(view, animBackground);		  
			  clearViewCanvas();
			  firstrun=false;
		  }
		  animBackground.start();
		  animRunning=true;
	  }
  }
  
  /**
   * 
   *  Stop the animation
   */
  
  public void stopAnimation() {
	  if (animRunning) {
		  animBackground.stop();
		  animRunning=false;
	  }
  }

  @Override
  public View getView() {
    return view;
  }

  // Methods related to getting the dimensions of this Canvas

  /**
   * Returns whether the layout associated with this view has been computed.
   * If so, {@link #Width()} and {@link #Height()} will be properly initialized.
   *
   * @return {@code true} if it is safe to call {@link #Width()} and {@link
   * #Height()}, {@code false} otherwise
   */
  public boolean ready() {
    return drawn;
  }

  // Implementation of container methods

  /**
   * Adds a sprite to this Canvas by placing it in {@link #sprites}.
   *
   * @param sprite the sprite to add
   */
  public void addSprite(Sprite sprite) {
    sprites.add(sprite);
  }

  /**
   * Removes a sprite from this Canvas.
   *
   * @param sprite the sprite to remove
   */
  public void removeSprite(Sprite sprite) {
    sprites.remove(sprite);
  }

  @Override
  public Activity $context() {
    return container.$context();
  }

  @Override
  public Form $form() {
    return container.$form();
  }

  @Override
  public void $add(AndroidViewComponent component) {
    throw new UnsupportedOperationException("Canvas.$add() called");
  }

  @Override
  public void setChildWidth(AndroidViewComponent component, int width) {
    throw new UnsupportedOperationException("Canvas.setChildWidth() called");
  }

  @Override
  public void setChildHeight(AndroidViewComponent component, int height) {
    throw new UnsupportedOperationException("Canvas.setChildHeight() called");
  }

  // Methods executed when a child sprite has changed its location or appearance

  /**
   * Indicates that a sprite has changed, triggering invalidation of the view
   * and a check for collisions.
   *
   * @param sprite the sprite whose location, size, or appearance has changed
   */
  void registerChange(Sprite sprite) {
    view.invalidate();
    findSpriteCollisions(sprite);
  }


  // Methods for detecting collisions

  /**
   * Checks if the given sprite now overlaps with or abuts any other sprite
   * or has ceased to do so.  If there is a sprite that is newly in collision
   * with it, {@link Sprite#CollidedWith(Sprite)} is called for each sprite
   * with the other sprite as an argument.  If two sprites that had been in
   * collision are no longer colliding,
   * {@link Sprite#NoLongerCollidingWith(Sprite)} is called for each sprite
   * with the other as an argument.   Collisions are only recognized between
   * sprites that are both
   * {@link com.google.devtools.simple.runtime.components.android.Sprite#Visible()}
   * and
   * {@link com.google.devtools.simple.runtime.components.android.Sprite#Enabled()}.
   *
   * @param movedSprite the sprite that has just changed position
   */
  protected void findSpriteCollisions(Sprite movedSprite) {
    for (Sprite sprite : sprites) {
      if (sprite != movedSprite) {
        // Check whether we already raised an event for their collision.
        if (movedSprite.CollidingWith(sprite)) {
          // If they no longer conflict, note that.
          if (!movedSprite.Visible() || !movedSprite.Enabled() ||
              !sprite.Visible() || !sprite.Enabled() ||
              !Sprite.colliding(sprite, movedSprite) ||
              !collidingcheck(sprite, movedSprite)) {
            movedSprite.NoLongerCollidingWith(sprite);
            sprite.NoLongerCollidingWith(movedSprite);
          } else {
            // If they still conflict, do nothing.
          }
        } else {
          // Check if they now conflict.
          if (movedSprite.Visible() && movedSprite.Enabled() &&
              sprite.Visible() && sprite.Enabled() &&
              (Sprite.colliding(sprite, movedSprite) || 
               collidingcheck(sprite, movedSprite))) {
            // If so, raise two CollidedWith events.
            movedSprite.CollidedWith(sprite);
            sprite.CollidedWith(movedSprite);
          } else {
            // If they still don't conflict, do nothing.
          }
        }
      }
    }
  }
  
  private boolean collidingcheck(Sprite sprite, Sprite movedSprite) {
	  
	  if (sprite.isCircleCollision() && movedSprite.isCircleCollision()) {
		  return Sprite.circlecolliding(sprite, movedSprite);
	  }
	  if (sprite.isCircleCollision() && !movedSprite.isCircleCollision()) {
		  Sprite circle = sprite;
		  Sprite rect = movedSprite;
		  double circledistanceX = Math.abs(circle.X() - rect.X() - rect.Width()/2);
		  double circledistanceY = Math.abs(circle.Y() - rect.Y() - rect.Height()/2);
		  if (circledistanceX > (rect.Width()/2 + circle.getCollisionRadius())) {
			  return false;
		  }
		  if (circledistanceY > (rect.Height()/2 + circle.getCollisionRadius())) {
			  return false;
		  }
		  if (circledistanceX <= (rect.Width()/2)) {
			  return true;
		  }
		  if (circledistanceY <= (rect.Height()/2)) {
			  return true;
		  }
		  double cornerDistance_sq = (circledistanceX - rect.Width()/2)* (circledistanceX - rect.Width()/2) +
		  (circledistanceY - rect.Height()/2) * (circledistanceY - rect.Height()/2);
		  return (cornerDistance_sq <= (circle.getCollisionRadius()*circle.getCollisionRadius()));
		  
	  }
	  if (!sprite.isCircleCollision() && movedSprite.isCircleCollision()) {
		  Sprite circle = movedSprite;
		  Sprite rect = sprite;
		  double circledistanceX = Math.abs(circle.X() - rect.X() - rect.Width()/2);
		  double circledistanceY = Math.abs(circle.Y() - rect.Y() - rect.Height()/2);
		  if (circledistanceX > (rect.Width()/2 + circle.getCollisionRadius())) {
			  return false;
		  }
		  if (circledistanceY > (rect.Height()/2 + circle.getCollisionRadius())) {
			  return false;
		  }
		  if (circledistanceX <= (rect.Width()/2)) {
			  return true;
		  }
		  if (circledistanceY <= (rect.Height()/2)) {
			  return true;
		  }
		  double cornerDistance_sq = (circledistanceX - rect.Width()/2)* (circledistanceX - rect.Width()/2) +
		  (circledistanceY - rect.Height()/2) * (circledistanceY - rect.Height()/2);
		  return (cornerDistance_sq <= (circle.getCollisionRadius()*circle.getCollisionRadius()));
	  }
	  return false;
  }


  // Properties

  /**
   * Returns the button's background color as an alpha-red-green-blue
   * integer, i.e., {@code 0xAARRGGBB}.  An alpha of {@code 00}
   * indicates fully transparent and {@code FF} means opaque.
   *
   * @return background color in the format 0xAARRGGBB, which includes
   * alpha, red, green, and blue components
   */
 
  public int BackgroundColor() {
    return backgroundColor;
  }

  /**
   * Specifies the button's background color as an alpha-red-green-blue
   * integer, i.e., {@code 0xAARRGGBB}.  An alpha of {@code 00}
   * indicates fully transparent and {@code FF} means opaque.
   *
   * @param argb background color in the format 0xAARRGGBB, which
   * includes alpha, red, green, and blue components
   */
 
  public void BackgroundColor(int argb) {
    backgroundColor = argb;
    if (argb != Component.COLOR_DEFAULT) {
      PaintUtil.changePaint(backgroundPaint, argb);
    } else {
      // The default background color is white.
      PaintUtil.changePaint(backgroundPaint, Component.COLOR_WHITE);
    }
    clearViewCanvas();
  }

  /**
   * Returns the path of the canvas background image.
   *
   * @return  the path of the canvas background image
   */
  
  public String BackgroundImage() {
    return backgroundImagePath;
  }
  
  /**
   *  Specifies whether to loop the animation or not
   * @param loop set this to true for looping, false
   *  for not looping.
   */
  
  public void LoopAnimation(boolean loop) {
	  animBackground.setOneShot(!loop);
  }
  
  private void setAnimBackground() {
	  
	  if (picList.size()>0) {
		  
		  animBackground = new AnimationDrawable();
		  for (int i = 0; i < picList.size(); i++) {
			  String path;
			  if (picList.get(i).contains(".")) {
				  path = picList.get(i).split("\\.")[0];
			  } else {
				  path = picList.get(i);
			  }
			  //int temp = form.getResources().getIdentifier(path, "drawable", form.getPackageName());
			  try {
				  //animBackground.addFrame(form.getResources().getDrawable(temp), fps);
				  animBackground.addFrame(MediaUtil.getDrawable(container.$form(), path), fps);
			  } catch (IOException ioe) {
				  Log.e("Canvas", "Unable to load " + picList.get(i));
				  animBackground = null;
				  
			  }
		  }
		  if (animBackground != null) {
			  Drawable draw = animBackground.getFrame(0);
			  view.bitmap = ((BitmapDrawable) draw).getBitmap();		  
			  clearViewCanvas();
		  }	    	
	  }
	    
  }
  
  // Sets a particular frame of the animation as the canvas' background
  
  public void setFrame(int frame) {
	  frame--;
	  if (animBackground != null) {
		  if (frame < animBackground.getNumberOfFrames()) {		  
			  //ViewUtil.setBackgroundImage(view, animBackground.getFrame(frame));
			  Drawable draw = animBackground.getFrame(frame);
			  view.bitmap = ((BitmapDrawable) draw).getBitmap();
			  clearViewCanvas();
		  }
	  }
  }

  /**
   * Specifies the path of the canvas background image.
   *
   * <p/>See {@link MediaUtil#determineMediaSource} for information about what
   * a path can be.
   *
   * @param path  the path of the canvas background image
   */
 
  public void BackgroundImage(String path) {
    backgroundImagePath = (path == null) ? "" : path;

    	if (path.contains(".")) {
    		path = path.split("\\.")[0];
    	}
        //int temp = form.getResources().getIdentifier(path, "drawable", form.getPackageName());
    	try {
    		//backgroundDrawable = form.getResources().getDrawable(temp);
    		backgroundDrawable = MediaUtil.getDrawable(container.$form(), backgroundImagePath);
    		firstrun=true;
    	} catch (IOException ioe) {
    		Log.e("Canvas", "Unable to load " + backgroundImagePath);
    		backgroundDrawable = null;
    	}
    	if (backgroundDrawable != null) {
            
            if (backgroundDrawable instanceof BitmapDrawable) {
              
              view.bitmap = ((BitmapDrawable) backgroundDrawable).getBitmap();
              
            }
    	}
    //ViewUtil.setBackgroundImage(view, backgroundDrawable);
    clearViewCanvas();
  }

  /**
   * Returns the currently specified paint color as an alpha-red-green-blue
   * integer, i.e., {@code 0xAARRGGBB}.  An alpha of {@code 00}
   * indicates fully transparent and {@code FF} means opaque.
   *
   * @return paint color in the format 0xAARRGGBB, which includes alpha,
   * red, green, and blue components
   */
  
  public int PaintColor() {
    return paintColor;
  }

  /**
   * Specifies the paint color as an alpha-red-green-blue integer,
   * i.e., {@code 0xAARRGGBB}.  An alpha of {@code 00} indicates fully
   * transparent and {@code FF} means opaque.
   *
   * @param argb paint color in the format 0xAARRGGBB, which includes
   * alpha, red, green, and blue components
   */
  
  public void PaintColor(int argb) {
    paintColor = argb;
    if (argb == Component.COLOR_DEFAULT) {
      // The default paint color is black.
      PaintUtil.changePaint(paint, Component.COLOR_BLACK);
    } else if (argb == Component.COLOR_NONE) {
      PaintUtil.changePaintTransparent(paint);
    } else {
      PaintUtil.changePaint(paint, argb);
    }
  }

  
  public float FontSize() {
    return paint.getTextSize();
  }

  
  public void FontSize(float size) {
    paint.setTextSize(size);
  }

  /**
   * Returns the currently specified stroke width
   * @return width
   */
  
  public float LineWidth() {
    return paint.getStrokeWidth();
  }

  /**
   * Specifies the stroke width
   *
   * @param width
   */
 
  public void LineWidth(float width) {
    paint.setStrokeWidth(width);
  }

  /**
   * Returns the alignment of the canvas's text: center, normal
   * (starting at the specified point in drawText()), or opposite
   * (ending at the specified point in drawText()).
   *
   * @return  one of {@link Component#ALIGNMENT_NORMAL},
   *          {@link Component#ALIGNMENT_CENTER} or
   *          {@link Component#ALIGNMENT_OPPOSITE}
   */
  
  public int TextAlignment() {
    return textAlignment;
  }

  /**
   * Specifies the alignment of the canvas's text: center, normal
   * (starting at the specified point in DrawText() or DrawAngle()),
   * or opposite (ending at the specified point in DrawText() or
   * DrawAngle()).
   *
   * @param alignment  one of {@link Component#ALIGNMENT_NORMAL},
   *                   {@link Component#ALIGNMENT_CENTER} or
   *                   {@link Component#ALIGNMENT_OPPOSITE}
   */
  
  public void TextAlignment(int alignment) {
    this.textAlignment = alignment;
    switch (alignment) {
      case Component.ALIGNMENT_NORMAL:
        paint.setTextAlign(Paint.Align.LEFT);
        break;
      case Component.ALIGNMENT_CENTER:
        paint.setTextAlign(Paint.Align.CENTER);
        break;
      case Component.ALIGNMENT_OPPOSITE:
        paint.setTextAlign(Paint.Align.RIGHT);
        break;
    }
  }


  // Methods supporting event handling

  /**
   * When the user touches a canvas, providing the (x, y) position of
   * the touch relative to the upper left corner of the canvas.  The
   * value "touchedSprite" is true if a sprite was also in this position.
   *
   * @param x  x-coordinate of the point that was touched
   * @param y  y-coordinate of the point that was touched
   * @param touchedSprite {@code true} if a sprite was touched, {@code false}
   *        otherwise
   */
  
  public void Touched(float x, float y, boolean touchedSprite) {
    EventDispatcher.dispatchEvent(this, "Touched", x, y, touchedSprite);
  }

  /**
   * When the user does a drag from one point (prevX, prevY) to
   * another (x, y).  The pair (startX, startY) indicates where the
   * user first touched the screen, and "draggedSprite" indicates whether a
   * sprite is being dragged.
   *
   * @param startX the starting x-coordinate
   * @param startY the starting y-coordinate
   * @param prevX the previous x-coordinate (possibly equal to startX)
   * @param prevY the previous y-coordinate (possibly equal to startY)
   * @param currentX the current x-coordinate
   * @param currentY the current y-coordinate
   * @param draggedSprite {@code true} if
   *        {@link Sprite#Dragged(float, float, float, float, float, float)}
   *        was called for one or more sprites for this segment, {@code false}
   *        otherwise
   */
  
  public void Dragged(float startX, float startY, float prevX, float prevY,
                      float currentX, float currentY, boolean draggedSprite) {
    EventDispatcher.dispatchEvent(this, "Dragged", startX, startY,
                                  prevX, prevY, currentX, currentY, draggedSprite);
  }


  // Functions

  /**
   * Clears the canvas, without removing the background image, if one
   * was provided.
   */
  
  public void Clear() {
    clearViewCanvas();
  }

  /**
   * Draws a point at the given coordinates on the canvas.
   *
   * @param x  x coordinate
   * @param y  y coordinate
   */
  
  public void DrawPoint(int x, int y) {
    view.canvas.drawPoint(x, y, paint);
    view.invalidate();
  }

  /**
   * Draws a circle (filled in) at the given coordinates on the canvas, with the
   * given radius.
   *
   * @param x  x coordinate
   * @param y  y coordinate
   * @param r  radius
   */
  
  public void DrawCircle(int x, int y, float r) {
    view.canvas.drawCircle(x, y, r, paint);
    view.invalidate();
  }

  /**
   * Draws a line between the given coordinates on the canvas.
   *
   * @param x1  x coordinate of first point
   * @param y1  y coordinate of first point
   * @param x2  x coordinate of second point
   * @param y2  y coordinate of second point
   */
  
  public void DrawLine(int x1, int y1, int x2, int y2) {
    view.canvas.drawLine(x1, y1, x2, y2, paint);
    view.invalidate();
  }

  /**
   * Draws the specified text relative to the specified coordinates.
   * Appearance depends on the values of {@link #textSize} and
   * {@link #textAlignment}.
   *
   * @param text the text to draw
   * @param x the x-coordinate of the origin
   * @param y the y-coordinate of the origin
   */
  
  public void DrawText(String text, int x, int y) {
    view.canvas.drawText(text, (float) x, (float) y, paint);
    view.invalidate();
  }

  /**
   * Draws the specified text starting at the specified coordinates
   * at the specified angle. Appearance depends on the values of
   * {@link #textSize} and {@link #textAlignment}.
   *
   * @param text the text to draw
   * @param x the x-coordinate of the origin
   * @param y the y-coordinate of the origin
   * @param angle the angle (in degrees) at which to draw the text
   */
  
  public void DrawTextAtAngle(String text, int x, int y, float angle) {
    view.canvas.save();
    view.canvas.rotate(-angle, (float) x, (float) y);
    view.canvas.drawText(text, (float) x, (float) y, paint);
    view.canvas.restore();
    view.invalidate();
  }

  /**
   * Saves a picture of this Canvas to the device's external storage and returns
   * the full path name of the saved file. If an error occurs the Screen's
   * ErrorOccurred event will be called.
   */
  
  public String Save() {
    try {
      File file = FileUtil.getPictureFile("png");
      return saveFile(file, Bitmap.CompressFormat.PNG, "Save");
    } catch (IOException e) {
      container.$form().dispatchErrorOccurredEvent(this, "Save",
          ErrorMessages.ERROR_MEDIA_FILE_ERROR, e.getMessage());
    } catch (FileUtil.FileException e) {
      container.$form().dispatchErrorOccurredEvent(this, "Save",
          e.getErrorMessageNumber());
    }
    return "";
  }

  /**
   * Saves a picture of this Canvas to the device's external storage in the file
   * named fileName. fileName must end with one of ".jpg", ".jpeg", or ".png"
   * (which determines the file type: JPEG, or PNG). Returns the full path
   * name of the saved file.
   */
 
  public String SaveAs(String fileName) {
    // Figure out desired file format
    Bitmap.CompressFormat format;
    if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
      format = Bitmap.CompressFormat.JPEG;
    } else if (fileName.endsWith(".png")) {
      format = Bitmap.CompressFormat.PNG;
    } else if (!fileName.contains(".")) {  // make PNG the default to match Save behavior
      fileName = fileName + ".png";
      format = Bitmap.CompressFormat.PNG;
    } else {
      container.$form().dispatchErrorOccurredEvent(this, "SaveAs",
          ErrorMessages.ERROR_MEDIA_IMAGE_FILE_FORMAT);
      return "";
    }
    try {
      File file = FileUtil.getExternalFile(fileName);
      return saveFile(file, format, "SaveAs");
    } catch (IOException e) {
      container.$form().dispatchErrorOccurredEvent(this, "SaveAs",
          ErrorMessages.ERROR_MEDIA_FILE_ERROR, e.getMessage());
    } catch (FileUtil.FileException e) {
      container.$form().dispatchErrorOccurredEvent(this, "SaveAs",
          e.getErrorMessageNumber());
    }
    return "";
  }

  // Helper method for Save and SaveAs
  private String saveFile(File file, Bitmap.CompressFormat format, String method) {
    try {
      boolean success = false;
      FileOutputStream fos = new FileOutputStream(file);
      try {
        if (SdkLevel.getLevel() >= SdkLevel.LEVEL_DONUT) {
          // TODO(user): note: we are setting autoscale to false here and
          // in the getDrawingCache call below because when it is true and
          // we're running in compatibility mode, attempting to set the
          // canvas size to a width X height that exceeds some threshold (somewhere
          // around 75,600 pixels) causes the getDrawingCache call to return null.
          // I couldn't figure out why this happens, but setting autoscale
          // to false seems to avoid the problem.
          DonutUtil.buildDrawingCache(view, false);
        } else {
          // On pre-1.6 devices, we can't use autoScale.
          view.buildDrawingCache();
        }
        try {
          Bitmap bitmap;
          if (SdkLevel.getLevel() >= SdkLevel.LEVEL_DONUT) {
            bitmap = DonutUtil.getDrawingCache(view, false);
          } else {
            // On pre-1.6 devices, we can't use autoScale.
            bitmap = view.getDrawingCache();
          }
          if (bitmap != null) {
            success = bitmap.compress(format,
                100,  // quality: ignored for png
                fos);
          }
        } finally {
          view.destroyDrawingCache();
        }
      } finally {
        fos.close();
      }
      if (success) {
        return file.getAbsolutePath();
      } else {
        container.$form().dispatchErrorOccurredEvent(this, method,
            ErrorMessages.ERROR_CANVAS_BITMAP_ERROR);
      }
    } catch (FileNotFoundException e) {
      container.$form().dispatchErrorOccurredEvent(this, method,
          ErrorMessages.ERROR_MEDIA_CANNOT_OPEN, file.getAbsolutePath());
    } catch (IOException e) {
      container.$form().dispatchErrorOccurredEvent(this, method,
          ErrorMessages.ERROR_MEDIA_FILE_ERROR, e.getMessage());
    }
    return "";
  }
  
	
		public void AutoToggle(boolean autotoggle) {
			this.autoToggle = autotoggle;
		}

		@Override
		public void onStop() {
			
			if (autoToggle) {
				if (animBackground.isRunning()) {
					animBackground.stop();
				}
			}			
		}

		@Override
		public void onResume() {
			
			if (autoToggle && animRunning) {
				animBackground.start();			
			}			
		}
		
		@Override
		public void onInitialize() {
			super.onInitialize();
			view.bitmap = Bitmap.createScaledBitmap(view.bitmap, this.Width(), this.Height(), true);
		}
		   
}
