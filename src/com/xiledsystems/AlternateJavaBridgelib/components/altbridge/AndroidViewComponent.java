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
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

/**
 * Underlying base class for all components with views; not accessible to Simple programmers.
 * <p>
 * Provides implementations for standard properties and events.
 *
 */

public abstract class AndroidViewComponent extends VisibleComponent implements OnInitializeListener, OnDestroyListener  {

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
  
  public double getrelX() {
	  return this.relX;
  }
  
  public double getrelY() {
	  return this.relY;
  }
  
  public void setLocationMultipliers(double x, double y) {
	  if (isInRelArgmnt) {
		  this.relX = x;
		  this.relY = y;
	  }
  }
  
  public void MoveTo(int x, int y) {
	  if (isInRelArgmnt) {
			RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) getView().getLayoutParams();			
			layout.leftMargin = x;
			layout.topMargin = y;
			getView().requestLayout();
		}  
  }
  
  public void setInRelArgmnt(boolean isinrelargmnt) {
	  this.isInRelArgmnt = isinrelargmnt;
  }
  
  public boolean isInRelArgmnt() {
	  return isInRelArgmnt;
  }
  
  public boolean isAutoResize() {
	  return this.autoResize;
  }

  public void setMultipliers(double widthmultiplier, double heightmultiplier) {
		
		autoResize=true;
		this.widthMultiplier = widthmultiplier;
		this.heightMultiplier = heightmultiplier;			
	} 
  
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
   * Returns the {@link View} that is displayed in the UI.
   */
  public abstract View getView();
  
  // Method for components to run in the middle of the
  // flip animation (so devs can do stuff between )
  // This is where the EventDispatcher should be called
  public abstract void postAnimEvent();
  
  // User definable int array for setting the start and end
  // for the two animations in the flip animation (degrees)
  private int[] startEndDeg = { 0, 180, 180, 360 } ;
  
  public void RotateStartEndDegrees(int firststart, int firstend, int secondstart, int secondend) {
	  startEndDeg[0] = firststart;
	  startEndDeg[1] = firstend;
	  startEndDeg[2] = secondstart;
	  startEndDeg[3] = secondend;
  }
  
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
	  rotation.setInterpolator(new AccelerateInterpolator());
	  if (curRep < repeatMode || repeatMode==Animation.INFINITE) {		  		  
		  rotation.setAnimationListener(new DisplayNextAnimation());
	  } 
	  getView().startAnimation(rotation);
  }
  
  // Sets the start and end points of a moving animation
  public void AnimationPoints(float startX, float startY, float endX, float endY) {
	  customPoints = true;
	  animPoints[0] = startX;
	  animPoints[1] = endX;
	  animPoints[2] = startY;
	  animPoints[3] = endY;
  }
  
  // Basic animation to shake a component
  public void ShakeComponent() {
	  Animation shake = new TranslateAnimation(0, 10, 0, 0);
	  shake.setDuration(1000);
	  shake.setInterpolator(new CycleInterpolator(7));
	  getView().startAnimation(shake);
  }
    
  public float[] AnimationPoints() {
	  return animPoints;
  }
  
  public void AnimationDuration(long duration) {
	  this.animDuration = duration;
  }
  
  public long AnimationDuration() {
	  return animDuration;
  }
  
  // Delay before animation starts
  public void AnimationStartOffset(long offset) {
	  this.startOffset = offset;
  }
  
  public long AnimationStartOffset() {
	  return startOffset;
  }
  
  public void AnimationRepeatCount(int count) {
	  this.repeatMode = count;
  }
  
  public int AnimationRepeatCount() {
	  return repeatMode;
  }
  
  public void AnimationDirection(int direction) {
	  this.animDirection = direction;
  }
  
  public int AnimationDirection() {
	  return animDirection;
  }
  
  public void AnimationDepth(float depth) {
	  this.depth = depth;
  }
  
  public float AnimationDepth() {
	  return depth;
  }
  
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
  
  @Override
	public void onInitialize() {
		
		if (autoResize) {
			Form form = container.$form();
			this.Width((int) (form.scrnWidth * widthMultiplier));
			this.Height((int) (form.scrnHeight * heightMultiplier));
		}		
		if (isInRelArgmnt) {
			RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) getView().getLayoutParams();
			Form form = container.$form();
			layout.leftMargin = (int) (relX * form.scrnWidth);
			layout.topMargin = (int) (relY * form.scrnHeight);
			getView().requestLayout();
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
      }

      public void onAnimationEnd(Animation animation) {
    	  postAnimEvent();
          processSecondHalfAnimation();
      }

      public void onAnimationRepeat(Animation animation) {
      }
  }
}
