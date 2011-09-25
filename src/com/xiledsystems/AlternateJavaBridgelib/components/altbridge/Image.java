package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import java.io.IOException;
import java.util.ArrayList;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.AnimationUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.MediaUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ViewUtil;

/**
 * Component for displaying images and animations.
 *
 */

public final class Image extends AndroidViewComponent implements OnResumeListener, OnStopListener {

  private final ImageView view;
  
  //AJB
 
  private final Form form;
  private AnimationDrawable animBackground;
  private int fps=10;
  private boolean animRunning=false;
  private ArrayList<String> picList;
  private boolean firstrun=true;
  private boolean autoToggle=true;

  private String picturePath = "";  // Picture property

  
  /**
   * 
   *  Set the list of image names for the animation, and the fps 
   *  , or speed of the animation. Doing this negates any image
   *  that was set with the Image() method. 
   *   
   * @param piclist a String ArrayList of the image filenames
   * 
   * @param fps frames per second; affects the speed of the
   * animation
   */
  public void setAnimListandFPS(ArrayList<String> piclist, int fps) {
	  picList = new ArrayList<String>();
	  this.picList = piclist;
	  this.fps = 1000/fps;
	  setAnimBackground();
	  
  }
  
  /**
   * Creates a new Image component.
   *
   * @param container  container, component will be placed in
   */
  
  /**
   * 
   *  Start the animation
   */
  
  public void startAnimation() {
	  if (!animRunning) {
		  if (firstrun) {
			  ViewUtil.setBackgroundImage(view, animBackground);		  
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
  
  public Image(ComponentContainer container) {
    super(container);
    form = container.$form();
    view = new ImageView(container.$context()) {
      @Override
      public boolean verifyDrawable(Drawable dr) {
        super.verifyDrawable(dr);
        // TODO(user): multi-image animation
        return true;
      }
    };
    
    // Adds the component to its designated container
    container.$add(this);
    view.setFocusable(true);
  }

  @Override
  public View getView() {
    return view;
  }

  /**
   * Returns the path of the image's picture.
   *
   * @return  the path of the image's picture
   */
  
  public String Picture() {
    return picturePath;
  }

  /**
   * Specifies the path of the image's picture.
   *
   * <p/>See {@link MediaUtil#determineMediaSource} for information about what
   * a path can be.
   *
   * @param path  the path of the image's picture
   */
  
  public void Picture(String path) {
    picturePath = (path == null) ? "" : path;

    Drawable drawable;
    try {
      drawable = MediaUtil.getDrawable(container.$form(), picturePath);
    } catch (IOException ioe) {
      Log.e("Image", "Unable to load " + picturePath);
      drawable = null;
    }

    // AJB change - changed from setImage, so it can resize freely (not locked to aspect ratio)
    ViewUtil.setBackgroundImage(view, drawable);
  }
  
  private void setAnimBackground() {
	  
	  if (picList.size()>0) {
		  
		  animBackground = new AnimationDrawable();
		  for (int i = 0; i < picList.size(); i++) {			  
			  try {
				  animBackground.addFrame(MediaUtil.getDrawable(form, picList.get(i)), fps);
			  } catch (IOException ioe) {
				  Log.e("Canvas", "Unable to load " + picList.get(i));
				  animBackground = null;				  
			  }
		  }
		  ViewUtil.setBackgroundImage(view, animBackground.getFrame(0));		  		  
	    	
	  }
	    
  }
  
  public void setFrame(int frame) {
	  frame--;
	  if (frame < animBackground.getNumberOfFrames()) {
		  ViewUtil.setBackgroundImage(view, animBackground.getFrame(frame));
	  }
	  
  }


  /**
   * Animation property setter method.
   *
   * @see AnimationUtil
   *
   * @param animation  animation kind
   */
  
  public void Animation(String animation) {
    AnimationUtil.ApplyAnimation(view, animation);
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
  
    
}
