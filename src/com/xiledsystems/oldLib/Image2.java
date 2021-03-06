package com.xiledsystems.AlternateJavaBridgelib;

import java.io.IOException;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.devtools.simple.runtime.components.android.AndroidViewComponent;
import com.google.devtools.simple.runtime.components.android.ComponentContainer;
import com.google.devtools.simple.runtime.components.android.Form;
import com.google.devtools.simple.runtime.components.android.util.AnimationUtil;
import com.google.devtools.simple.runtime.components.android.util.MediaUtil;
import com.google.devtools.simple.runtime.components.android.util.ViewUtil;

public class Image2 extends AndroidViewComponent implements OnInitializeListener {

	private final ImageView view;

	  private String picturePath = "";  // Picture property

	  private final Form form;
	  private boolean autoResize= false;
		
		 private double widthMultiplier;
		 private double heightMultiplier;
	  /**
	   * Creates a new Image component.
	   *
	   * @param container  container, component will be placed in
	   */
	  public Image2(ComponentContainer container) {
	    super(container);
	    view = new ImageView(container.$context()) {
	      @Override
	      public boolean verifyDrawable(Drawable dr) {
	        super.verifyDrawable(dr);
	        // TODO(user): multi-image animation
	        return true;
	      }
	    };
	    form = container.$form();
	    // Adds the component to its designated container
	    container.$add(this);
	    view.setFocusable(true);
	    if (form instanceof Form2) {
	    	((Form2) form).registerForOnInitialize(this);
	    }
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

	    ViewUtil.setBackgroundImage(view, drawable);
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
	
	}
