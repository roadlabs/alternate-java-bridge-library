package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;


import java.util.ArrayList;


import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnInitializeListener;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.AnimationUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.Rotate3dAnimation;
import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;
import com.xiledsystems.AlternateJavaBridgelib.components.VisibleComponent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

/**
 * Underlying base class for all components with views; not accessible to Simple programmers.
 * <p>
 * Provides implementations for standard properties and events.
 *
 */

public abstract class AndroidViewComponent extends VisibleComponent implements OnInitializeListener, OnDestroyListener {

  protected final ComponentContainer container;

  private int lastSetWidth = LENGTH_UNKNOWN;
  private int lastSetHeight = LENGTH_UNKNOWN;

  private int column = ComponentConstants.DEFAULT_ROW_COLUMN;
  private int row = ComponentConstants.DEFAULT_ROW_COLUMN;
  
  private boolean autoResize= false;
  private double widthMultiplier;
  private double heightMultiplier;
  
  // View animation variables
  private int repeatMode = 0;
  private int animDirection = AnimationUtil.LEFT_TO_RIGHT;
  private long animDuration = 1000;
  private long startOffset = 300;
  private float[] animPoints;
  private boolean customPoints;
  private boolean horizontal;
  private float depth = 100f;
  private int curRep = 0;
  private boolean secondAnim;
  
  
  // For animationdrawable implementation
  public boolean looping;
  public ArrayList<String> filelist;
  public int fps;
  public int curFrame;
  protected final int resourceId;
  
  private double relX = 0, relY = 0;
  
  private boolean isInRelArgmnt = false;

  /**
   * Creates a new AndroidViewComponent.
   *
   * @param container  container, component will be placed in
   */
  protected AndroidViewComponent(ComponentContainer container) {
    this.container = container;
    container.$form().registerForOnInitialize(this);
    container.$form().registerForOnDestroy(this);
    resourceId = -1;
    
  }
  
  protected AndroidViewComponent(ComponentContainer container, int resId) {
	    this.container = container;
	    container.$form().registerForOnInitialize(this);
	    container.$form().registerForOnDestroy(this);
	    resourceId = resId;
	    
  }
  
  /**
   * 
   * @return The X coordinate for this component. This only applies
   * when the component is in a RelativeArrangement
   */
  public double getrelX() {
	  return this.relX;
  }
  
  /**
   * 
   * @return The Y coordinate for this component. This only applies
   * when the component is in a RelativeArrangement
   */
  public double getrelY() {
	  return this.relY;
  }
  
  /**
   * This is a convenience method for automatically placing a component
   * in a certain area within a RelativeArrangement based of the
   * available screen size. Note that this is based off the total
   * screen size, not the available size of the RelativeArrangement.
   * So, a setting of setLocationMultipliers(.5, .5) in a screen size
   * of 480 by 800 would place the component at 240,400 inside the
   * RelativeArrangement (if the relative arrangement isn't that big,
   * you won't see the component).
   * @param x multiplier to multiply against screen's width
   * @param y multiplier to multiply against screen's height
   */
  public void setLocationMultipliers(double x, double y) {
	  if (isInRelArgmnt) {
		  this.relX = x;
		  this.relY = y;
	  }
  }
  
  /**
   * Move this component to a specific pixel location
   * inside it's parent RelativeArrangement
   * @param x The X coordinate
   * @param y The Y coordinate
   */
  public void MoveTo(int x, int y) {
	  if (isInRelArgmnt) {
			RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) getView().getLayoutParams();			
			layout.leftMargin = x;
			layout.topMargin = y;
			getView().requestLayout();
		}  
  }
  
  /**
   * This method is used internally, you don't have to run this at all.
   * @param isinrelargmnt
   */
  public void setInRelArgmnt(boolean isinrelargmnt) {
	  this.isInRelArgmnt = isinrelargmnt;
  }
  
  /**
   * 
   * @return If this component is inside a RelativeArrangement
   */
  public boolean isInRelArgmnt() {
	  return isInRelArgmnt;
  }
  
  
  /**
   * 
   * @return Whether this component is set to auto-resize (if setMultipliers was run)
   */
  public boolean isAutoResize() {
	  return this.autoResize;
  }

  /**
   * This will auto-size the component. The multipliers simply
   * are multiplied by the screen's available size.
   * ex:
   * If you use setMultipliers(.5, .5), the component
   * will be 50% of the screen's width, and 50% of the screen's
   * height. This should only be used when absolutely necessary.
   * @param x - The multiplier to apply to screen width
   * @param y - The multiplier to apply to screen height
   */
  public void setMultipliers(double widthmultiplier, double heightmultiplier) {
		
		autoResize=true;
		this.widthMultiplier = widthmultiplier;
		this.heightMultiplier = heightmultiplier;			
	} 
  
  /**
   * 
   * @return a double array of the size multipliers of this component
   */
  public double[] getMultipliers() {
		if (autoResize) {
			double[] temp = {widthMultiplier, heightMultiplier}; 
			return temp;
		} else {
			double[] temp = {-1, -1};
			return temp;
		}
	}
  /**
	 * This is for more advanced users who want to be able to tap into
	 * the android view this component is based on. This allows you
	 * more flexibility with the bridge, should you need it. 
	 */
  public abstract View getView();
  
  // Method for components to run in the middle of the
  // flip animation (so devs can do stuff between )
  // This is where the EventDispatcher should be called
  /**
   * Internal method, not to be called manually.
   */
  public abstract void postAnimEvent();
  
  /**
   * Used internally to send animation events
   * 
   * @param event
   */
  protected void postAnimEvent(String event) {
	  EventDispatcher.dispatchEvent(this, event);
  }
  
  /*public void postAnimEvent2() {
	  EventDispatcher.dispatchEvent(this, "AnimationMiddle");
  }*/
  
  // User definable int array for setting the start and end
  // for the two animations in the flip animation (degrees)
  private int[] startEndDeg = { 0, 180, 180, 360 } ;
  
  /**
   * Use this method to set the start, and ending position of the component
   * in degrees. Since the rotate animation is actually two animations
   * wrapped together, you need to set the start and end points
   * of each animation.
   * @param firststart first starting position in degrees
   * @param firstend first ending position in degrees
   * @param secondstart second starting position in degrees
   * @param secondend second ending position in degrees
   */
  public void RotateStartEndDegrees(int firststart, int firstend, int secondstart, int secondend) {
	  startEndDeg[0] = firststart;
	  startEndDeg[1] = firstend;
	  startEndDeg[2] = secondstart;
	  startEndDeg[3] = secondend;
  }
  
  /**
   * This starts a view animation on this component. The default animation
   * types are stored as static ints in Form, and start with ANIM_.
   * 
   * @param animationType The type of animation to run on this component
   */
  public void Animate(int animationType) {
	  // used to track how many repetitions of the animation
	  curRep = 0;
	  final View target = getView();
	  final View targetParent = (View) getView().getParent();
	  
	  float[] animArray;
	  // Get the start, and location points for the animation
	  // Use the defaults (left to right, etc), or user
	  // specified start and end points
	  if (customPoints) {
		  animArray = animPoints;
	  } else {
		  animArray = getAnimArray(target, targetParent);
	  }
	  if (animArray != null) {
		  
		  if (animationType != Form.ANIM_FLIP) {
			  Animation a = new TranslateAnimation(animArray[0], animArray[1], animArray[2], animArray[3]);
			  a.setDuration(animDuration);
			  a.setStartOffset(startOffset);
			  a.setRepeatMode(Animation.RESTART);			  
			  a.setRepeatCount(repeatMode);		  
			  a.setInterpolator(AnimationUtils.loadInterpolator(container.$context(), animationType));
			  a.setAnimationListener(new ComponentAnimListener());
			  target.startAnimation(a);
		  } else {
			  // Flip isn't moving the view per say, just flipping on the
			  // Y axis by default, but can be changed to the X axis 
			  // it also is set back on the Z axis a bit
			  // This is split into two animations for more usefulness
			  // ex a card that is flipped, the image can be changed
			  // when the view is at 90 or 270, giving the illusion
			  // of two sides.
			  Rotate3dAnimation rotation = new Rotate3dAnimation(startEndDeg[0], startEndDeg[1], Width()/2, Height()/2, depth, horizontal, false);
			  rotation.setDuration(animDuration/2);			  
			  rotation.setFillAfter(true);			  
			  rotation.setInterpolator(new AccelerateInterpolator());
			  // Add a custom listener to intercept the animation ended
			  // event, so we can send an event, and start the second
			  // animation.
			  rotation.setAnimationListener(new DisplayNextAnimation());			  
			  target.startAnimation(rotation);	
			  curRep = -1;
			  secondAnim = true;
		  }
	  }	  
  }
  
  /**
   * Internal method to start the second animation of the flip animation type.
   * This shouldn't be called manually.
   */
  public void processSecondHalfAnimation() {	  
	  
	  // Here we see if it's the second part of the animation, if so,
	  // we run the second set of degrees. Otherwise, run the first
	  // set.
	  Rotate3dAnimation rotation;	  
	  if (secondAnim) {
		  rotation = new Rotate3dAnimation(startEndDeg[2], startEndDeg[3], Width()/2, Height()/2, depth, horizontal, false);
		  secondAnim = false;
		  curRep++;
	  } else {
		  rotation = new Rotate3dAnimation(startEndDeg[0], startEndDeg[1], Width()/2, Height()/2, depth, horizontal, false);
		  secondAnim = true;
	  }
	  rotation.setDuration(animDuration/2);
	  rotation.setStartOffset(0);
	  rotation.setFillAfter(true);
	  rotation.setInterpolator(new DecelerateInterpolator());
	  if (curRep < repeatMode || repeatMode==Animation.INFINITE) {		  		  
		  rotation.setAnimationListener(new DisplayNextAnimation());
		  postAnimEvent("AnimRepeat");
	  } else {
		  rotation.setAnimationListener(new SecondHalfListener());
	  }
	  getView().startAnimation(rotation);
  }
  
  // Sets the start and end points of a moving animation
  /**
   * Set the start and end points of the view animation of this component.
   * 
   * @param startX The X coordinate of the starting position relative to it's parent
   * @param startY The Y coordinate of the starting position relative to it's parent
   * @param endX The X coordinate of the ending position relative to it's parent
   * @param endY The Y coordinate of the ending position relative to it's parent
   */
  public void AnimationPoints(float startX, float startY, float endX, float endY) {
	  customPoints = true;
	  if (animPoints == null) {
		  animPoints = new float[4];
	  }
	  animPoints[0] = startX;
	  animPoints[1] = endX;
	  animPoints[2] = startY;
	  animPoints[3] = endY;
  }
  
  // Basic animation to shake a component
  /**
   * Method to shake component.
   */
  public void ShakeComponent() {
	  Animation shake = new TranslateAnimation(0, 10, 0, 0);
	  shake.setDuration(1000);
	  shake.setInterpolator(new CycleInterpolator(7));
	  getView().startAnimation(shake);
  }
    
  /**
   * 
   * @return float array of the view animation start/end points
   */
  public float[] AnimationPoints() {
	  return animPoints;
  }
  
  /**
   * Sets the component's view animation duration
   * 
   * @param duration 
   */
  public void AnimationDuration(long duration) {
	  this.animDuration = duration;
  }
  
  /**
   * 
   * @return the component's view animation duration
   */
  public long AnimationDuration() {
	  return animDuration;
  }
  
  // Delay before animation starts
  /**
   * 
   * @param offset The amount of time before the view animation starts after calling Animate(animType)
   */
  public void AnimationStartOffset(long offset) {
	  this.startOffset = offset;
  }
  
  /**
   * 
   * @return The amount of time before the view animation is run after being started
   */
  public long AnimationStartOffset() {
	  return startOffset;
  }
  
  /**
   * 
   * @param count How many times the view animation repeats (0 means it will run once, as it has 0 repeats)
   */
  public void AnimationRepeatCount(int count) {
	  this.repeatMode = count;
  }
  
  /**
   * 
   * @return The amount of times this animation will repeat
   */
  public int AnimationRepeatCount() {
	  return repeatMode;
  }
  
  /**
   * Use this to set the direction of the view animation. If you manually
   * set the start/end points, this method is not necessary.
   * Use the static ints in AnimationUtils;
   * AnimationDirection(AnimationUtils.LEFT_TO_RIGHT)
   * 
   * @param direction The direction the view animation will run
   */
  public void AnimationDirection(int direction) {
	  this.animDirection = direction;
  }
  
  /**
   * 
   * @return the int representing the direction of the animation
   */
  public int AnimationDirection() {
	  return animDirection;
  }
  
  /**
   * This is only used for the ANIM_FLIP animation type.
   * This represents the Z axis (the flip animation rotates
   * the view in a 3d space. 
   * 
   * @param depth How far back on the Z axis the view animation is processed
   */
  public void AnimationDepth(float depth) {
	  this.depth = depth;
  }
  
  /**
   * 
   * @return the depth of the view animation (Z axis)
   */
  public float AnimationDepth() {
	  return depth;
  }
  
  /**
   * Use this method to set the start, and ending position of the component
   * in degrees. Since the rotate animation is actually two animations
   * wrapped together, you need to set the start and end points
   * of each animation.
   * This is only used with the ANIM_FLIP animation type.
   * 
   * @param startDegrees1 first starting position in degrees
   * @param endDegrees1 first ending position in degrees
   * @param startDegrees2 second starting position in degrees
   * @param endDegrees2 second ending position in degrees
   */
  public void AnimationFlipDegrees(int startDegrees1, int endDegrees1, int startDegrees2, int endDegrees2) {
	  startEndDeg[0] = startDegrees1;
	  startEndDeg[1] = endDegrees1;
	  startEndDeg[2] = startDegrees2;
	  startEndDeg[3] = endDegrees2;
  }

  // Parse the direction of the animation movement
  private float[] getAnimArray(View target, View targetParent) {
	  
	  float[] tmp = new float[4];
	  
	  switch (animDirection) {
	  
	  case AnimationUtil.LEFT_TO_RIGHT:		  
		  tmp[0] = 0.0f;
		  tmp[1] = targetParent.getWidth() - target.getWidth() - targetParent.getPaddingLeft() -
	                targetParent.getPaddingRight();
		  tmp[2] = 0.0f;
		  tmp[3] = 0.0f;
		  break;
		  
	  case AnimationUtil.RIGHT_TO_LEFT:
		  tmp[0] = targetParent.getWidth() - target.getWidth() - targetParent.getPaddingRight() -
          targetParent.getPaddingLeft();
		  tmp[1] = 0.0f;
		  tmp[2] = 0.0f;
		  tmp[3] = 0.0f;
		  break;
		  
	  case AnimationUtil.TOP_TO_BOTTOM:
		  tmp[0] = 0.0f;
		  tmp[1] = 0.0f;
		  tmp[2] = 0.0f;
		  tmp[3] = targetParent.getHeight() - target.getHeight() - targetParent.getPaddingTop() -
		          targetParent.getPaddingBottom();
		  break;
		  
	  case AnimationUtil.BOTTOM_TO_TOP:
		  tmp[0] = 0.0f;
		  tmp[1] = 0.0f;
		  tmp[2] = targetParent.getHeight() - target.getHeight() - targetParent.getPaddingBottom() -
		          targetParent.getPaddingTop();
		  tmp[3] = 0.0f;
		  break;		  
  }
	  return tmp;
	
}
  
  @Override
  public void onDestroy() {
	  // If the activity is killed when the animation is running,
	  // this cancels the animation
	  View view = getView();
	  if (view.getAnimation() != null) {
		  if (view.getAnimation().hasStarted() && !view.getAnimation().hasEnded()) {
			  view.getAnimation().cancel();
		  }
	  }
  }

/**
   * Returns true if the component is visible.
   * @return  true if the component is visible
   */  
  public boolean Visible() {
    return getView().getVisibility() == View.VISIBLE;
  }

  /**
   * Specifies whether the component should be visible
   * @param  visible desired state
   */  
  public void Visible(boolean visible) {
    // The principle of least astonishment suggests we not offer the
    // Android option INVISIBLE.
    getView().setVisibility(visible ? View.VISIBLE : View.GONE);
  }
  
  /**
   * Use this method to "hide" this component. This will make the
   * component invisible, but it will still take up space.
   * Use Visible(true) to unhide.
   * 
   */
  public void Hide() {
	  getView().setVisibility(View.INVISIBLE);
  }

  /**
   * Returns the component's horizontal width, measured in pixels.
   *
   * @return  width in pixels
   */
  @Override
  
  public int Width() {
    return getView().getWidth();
  }

  /**
   * Specifies the component's horizontal width, measured in pixels.
   *
   * @param  width in pixels
   */
  @Override
  
  public void Width(int width) {
    container.setChildWidth(this, width);
    lastSetWidth = width;
  }

  /**
   * Copy the width from another component to this one.  Note that we don't use
   * the getter method to get the property value from the source because the
   * getter returns the computed width whereas we want the width that it was
   * last set to.  That's because we want to preserve values like
   * LENGTH_FILL_PARENT and LENGTH_PREFERRED
   *
   * @param sourceComponent the component to copy from
   */
  
  public void CopyWidth(AndroidViewComponent sourceComponent) {
    Width(sourceComponent.lastSetWidth);
  }

  /**
   * Returns the component's vertical height, measured in pixels.
   *
   * @return  height in pixels
   */
  @Override
  
  public int Height() {
    return getView().getHeight();
  }

  /**
   * Specifies the component's vertical height, measured in pixels.
   *
   * @param  height in pixels
   */
  @Override
 
  public void Height(int height) {
    container.setChildHeight(this, height);
    lastSetHeight = height;
  }

  /**
   * Copy the height from another component to this one.  Note that we don't use
   * the getter method to get the property value from the source because the
   * getter returns the computed width whereas we want the width that it was
   * last set to.  That's because we want to preserve values like
   * LENGTH_FILL_PARENT and LENGTH_PREFERRED
   *
   * @param sourceComponent the component to copy from
   */
  
  public void CopyHeight(AndroidViewComponent sourceComponent) {
    Height(sourceComponent.lastSetHeight);
  }

  /**
   * Column property getter method.
   *
   * @return  column property used by the table arrangement
   */
  
  public int Column() {
    return column;
  }

  /**
   * Column property setter method.
   *
   * @param column  column property used by the table arrangement
   */
  
  public void Column(int column) {
    this.column = column;
  }

  /**
   * Row property getter method.
   *
   * @return  row property used by the table arrangement
   */
  
  public int Row() {
    return row;
  }

  /**
   * Row property setter method.
   *
   * @param row  row property used by the table arrangement
   */
  
  public void Row(int row) {
    this.row = row;
  }

  // Component implementation

  @Override
  public HandlesEventDispatching getDispatchDelegate() {
    return container.$form();
  }
  
  /**
   * Internal method not meant for a manual call.
   */
  @Override
	public void onInitialize() {
		
		if (autoResize) {
			Form form = container.$form();
			this.Width((int) (form.availWidth * widthMultiplier));
			this.Height((int) (form.availHeight * heightMultiplier));
		}		
		if (isInRelArgmnt) {
			RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) getView().getLayoutParams();
			Form form = container.$form();
			layout.leftMargin = (int) (relX * form.availWidth);
			layout.topMargin = (int) (relY * form.availHeight);
			getView().requestLayout();
		}
	}   
  
  private final class ComponentAnimListener implements Animation.AnimationListener {

	@Override
	public void onAnimationEnd(Animation arg0) {
		postAnimEvent("AnimEnd");		
	}

	@Override
	public void onAnimationRepeat(Animation arg0) {
		postAnimEvent("Repeat");
	}

	@Override
	public void onAnimationStart(Animation arg0) {
		postAnimEvent("AnimStart");
	}
	  
  }
  
  private final class SecondHalfListener implements Animation.AnimationListener {

	@Override
	public void onAnimationEnd(Animation animation) {
		postAnimEvent("AnimEnd");
	}

	@Override
	public void onAnimationRepeat(Animation animation) {		
	}

	@Override
	public void onAnimationStart(Animation animation) {				
	}
	  
  }
  
  
  /**
   * This class listens for the end of the animation.
   * It then posts a new event that effectively allows the dev
   * to change the image, or do anything in the middle of the
   * animation
   */
  private final class DisplayNextAnimation implements Animation.AnimationListener {      
      
      public void onAnimationStart(Animation animation) {    	
    	  if (curRep == -1) {
    		  postAnimEvent("AnimStart");
    	  }
      }

      public void onAnimationEnd(Animation animation) {
    	  postAnimEvent();
          processSecondHalfAnimation();
      }

      public void onAnimationRepeat(Animation animation) {
      }
  }
}
