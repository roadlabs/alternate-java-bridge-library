package com.xiledsystems.AlternateJavaBridgelib;

import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;
import com.google.devtools.simple.runtime.components.android.ComponentContainer;
import com.google.devtools.simple.runtime.components.android.Form;
import com.google.devtools.simple.runtime.components.android.OnResumeListener;
import com.google.devtools.simple.runtime.components.android.OnStopListener;
import com.google.devtools.simple.runtime.components.android.util.MediaUtil;
import com.google.devtools.simple.runtime.events.EventDispatcher;

/*
 * 
 *  This has been mostly copied from Google's App Inventor Java Bridge ImageSprite.java class.
 *  It has been modified to implement listeners to prevent the timer attached to the sprite
 *  from leaking memory when "closing" the activity.
 *  **** This imagesprite starts out with the default of a NON rotating sprite. You must
 *  specify that it rotates in order to well,...make it rotate.
 *  
 *  This also now implements the OnInitializeListener. It also adds the method 
 *  setMultiplier(double widhtmultiplier, double heightmultiplier) . This allows a quicker
 *  way to adjust for different screen sizes (so you specify the multiplier for each
 *  UI component, then the component will resize itself for you). You must extend
 *  from Form2 in order to use this (for your activity, instead of extending Form).
 *  
 */

public class AnimSprite extends Sprite2 implements OnStopListener, OnTouchListener, OnResumeListener, OnInitializeListener {
	 
	  private final Form form;
	  private Drawable drawable;
	  private int widthHint = LENGTH_PREFERRED;
	  private int heightHint = LENGTH_PREFERRED;
	  private String picturePath = "";  // Picture property
	  private boolean rotates;
	  private boolean autoResize= false;
	  private Bitmap spriteSheetBitmap;
	  private double widthMultiplier;
	  private double heightMultiplier;
	  private ArrayList<String> picNames;
	  private int currentFrame=0;
	  private int spriteWidth;
	  private int spriteHeight;
	  private int frameCount;
	  private Rect sourceRect;
	  

	  /**
	   * Constructor for AnimSprite.
	   *
	   * @param container
	   * 
	   * @param frameCount the amount of frames in the animation
	   */
	  public AnimSprite(ComponentContainer container, int frameCount) {
	    super(container);
	    form = container.$form();
	    rotates = false;
	    //picNames = new ArrayList<String>();
	    form.registerForOnResume(this);
	    form.registerForOnStop(this);
	    this.frameCount = frameCount;
	    if (form instanceof Form2) {
	    	((Form2) form).registerForOnInitialize(this);
	    }
	    sourceRect = new Rect(0, 0, 0, 0);
	    
	  }

	  public void onDraw(android.graphics.Canvas canvas) {
	    if (spriteSheetBitmap != null && visible) {
	    		    	
	    	Rect destRect = new Rect((int)X(), (int)Y(), (int)X() + Width(), (int)Y() + Height());
	    	canvas.drawBitmap(spriteSheetBitmap, sourceRect, destRect, null);
	    	
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
	  
	  public void gotoFrame(int frame) {
		  frame--;
		  if (frame> frameCount) {
			  Toast.makeText(form.$context(), "Frame doesn't exist!", Toast.LENGTH_LONG);
		  } else {
			  currentFrame = frame;
			  sourceRect.left = currentFrame * spriteWidth;
			  sourceRect.right = sourceRect.left + spriteWidth;
			  registerChange();
		  }
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
	      //BitmapFactory.decodeResource(form.getResources(), id)
	      // we'll need the bitmap for the drawable in order to rotate it
	      spriteSheetBitmap = ((BitmapDrawable) drawable).getBitmap();
	      spriteWidth = spriteSheetBitmap.getWidth() / frameCount;
		  spriteHeight = spriteSheetBitmap.getHeight();
		  //sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);
		  sourceRect.right = spriteWidth;
		  sourceRect.bottom = spriteHeight;
	    } catch (IOException ioe) {
	      Log.e("ImageSprite", "Unable to load " + picturePath);
	      drawable = null;
	      spriteSheetBitmap = null;
	    }
	    
	    registerChange();
	  }

	  // The actual width/height of an ImageSprite whose Width/Height property is set to Automatic or
	  // Fill Parent will be the width/height of the image.

	 
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

	  
	  public void Height(int height) {
	    heightHint = height;
	    registerChange();
	  }

	 
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

	  public void setImageNames(ArrayList<String> list) {
		  
		   this.picNames = list;
		  		  
	  }
	  
		  
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
	    
	

	@Override
	public void onResume() {

		// We can now turn the timer back on
		
		Enabled(true);
		
	}

	@Override
	public void onStop() {

		// Turn the sprite's timer off to avoid memory leaks
		Enabled(false);
		
	}

	@Override
	public void onInitialize() {
		
		if (autoResize) {
			this.Width((int) (((Form2) form).scrnWidth * widthMultiplier));
			this.Height((int) (((Form2) form).scrnHeight * heightMultiplier));
			//this.spriteWidth = (int) Width()/frameCount;
			//this.spriteHeight = (int) Height();
		
		}
				
		
	}
	
		
	public void setMultipliers(double widthmultiplier, double heightmultiplier) {
		
		autoResize=true;
		this.widthMultiplier = widthmultiplier;
		this.heightMultiplier = heightmultiplier;
	
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

	
}
