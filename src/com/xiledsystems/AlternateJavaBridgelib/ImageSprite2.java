package com.xiledsystems.AlternateJavaBridgelib;

import java.io.IOException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
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

public class ImageSprite2 extends Sprite2 implements OnStopListener, OnTouchListener, OnResumeListener, OnInitializeListener {
	  private final Form form;
	  private Drawable drawable;
	  private int widthHint = LENGTH_PREFERRED;
	  private int heightHint = LENGTH_PREFERRED;
	  private String picturePath = "";  // Picture property
	  private boolean rotates;
	  private boolean autoResize= false;
	  private Matrix mat;

	  private Bitmap unrotatedBitmap;
	  private Bitmap rotatedBitmap;

	  private BitmapDrawable rotatedDrawable;
	  private double cachedRotationHeading;
	  private boolean rotationCached;
	  
	  private double widthMultiplier;
	  private double heightMultiplier;
	 	  

	  /**
	   * Constructor for ImageSprite.
	   *
	   * @param container
	   */
	  public ImageSprite2(ComponentContainer container) {
	    super(container);
	    form = container.$form();
	    mat = new Matrix();
	    rotates = false;
	    rotationCached = false;
	    form.registerForOnResume(this);
	    form.registerForOnStop(this);
	    if (form instanceof Form2) {
	    	((Form2) form).registerForOnInitialize(this);
	    }
	  }

	  public void onDraw(android.graphics.Canvas canvas) {
	    if (unrotatedBitmap != null && visible) {
	      int xinit = (int) Math.round(xLeft);
	      int yinit = (int) Math.round(yTop);
	      int w = Width();
	      int h = Height();
	      // If the sprite doesn't rotate,  use the original drawable
	      // otherwise use the bitmapDrawable
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
	            // add in the width and height of the rotated bitmap
	            // to get the other right and bottom edges
	            xinit + w / 2 + rotatedBitmap.getWidth() / 2,
	            yinit + h / 2 + rotatedBitmap.getHeight() / 2);
	        rotatedDrawable.draw(canvas);
	      }
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
