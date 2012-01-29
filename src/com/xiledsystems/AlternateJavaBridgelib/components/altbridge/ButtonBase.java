package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.io.IOException;
import java.util.ArrayList;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.Form;
import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.MediaUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.TextViewUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ViewUtil;

import android.content.res.ColorStateList;
import android.content.res.Resources.NotFoundException;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

/**
 * Underlying base class for click-based components, not directly accessible to Simple programmers.
 *
 */
// AJB change - added ontouchlistener, and oninitializelistener

public abstract class ButtonBase extends AndroidViewComponent
    implements OnClickListener, OnFocusChangeListener, OnLongClickListener, OnTouchListener, OnResumeListener, OnStopListener {

  private final android.widget.Button view;
    
  private AnimationDrawable animBackground;
  private int fps=10;
  private boolean animRunning=false;
  private ArrayList<String> picList;
  private boolean firstrun=true;
  private boolean autoToggle=true;
  
  // Backing for text alignment
  private int textAlignment;

  // Backing for background color
  private int backgroundColor;

  // Backing for font typeface
  private int fontTypeface;

  // Backing for font bold
  private boolean bold;

  // Backing for font italic
  private boolean italic;

  // Backing for text color
  private int textColor;

  // Image path
  private String imagePath = "";

  // This is our handle on Android's nice 3-d default button.
  private Drawable defaultButtonDrawable;

  // This is our handle in Android's default button color states;
  private ColorStateList defaultColorStateList;
  
    
  /**
   * Creates a new ButtonBase component.
   *
   * @param container  container, component will be placed in
   */
  public ButtonBase(ComponentContainer container) {
    super(container);    
    view = new android.widget.Button(container.$context());
    defaultButtonDrawable = view.getBackground();
    defaultColorStateList = view.getTextColors();

    // Adds the component to its designated container
    container.$add(this);

    // Listen to clicks and focus changes
    view.setOnClickListener(this);
    view.setOnFocusChangeListener(this);
    view.setOnLongClickListener(this);
    view.setOnTouchListener(this);
    container.$form().registerForOnStop(this);
    container.$form().registerForOnResume(this);
    
    // Default property values
    TextAlignment(Component.ALIGNMENT_CENTER);
    // Background color is a dangerous property: Once you set it the nice
    // graphical representation for the button disappears forever (including the
    // focus marker).
    // BackgroundColor(Component.COLOR_NONE);
    BackgroundColor(Component.COLOR_DEFAULT);
    Enabled(true);
    fontTypeface = Component.TYPEFACE_DEFAULT;
    TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
    FontSize(Component.FONT_DEFAULT_SIZE);
    Text("");
    TextColor(Component.COLOR_DEFAULT);
    picList = new ArrayList<String>();
  }
  
  public ButtonBase(ComponentContainer container, int resourceId) {
	    super(container, resourceId);
	    
	    view = null;
	    android.widget.Button tmpview = (android.widget.Button) container.$form().findViewById(resourceId);
			    
	    if (tmpview == null) {
	    	throw new RuntimeException("View is null! Has setContentView been called yet?");
	    }
	    defaultButtonDrawable = tmpview.getBackground();
	    defaultColorStateList = tmpview.getTextColors();

	    // Listen to clicks and focus changes
	    tmpview.setOnClickListener(this);
	    tmpview.setOnFocusChangeListener(this);
	    tmpview.setOnLongClickListener(this);
	    tmpview.setOnTouchListener(this);
	    container.$form().registerForOnStop(this);
	    container.$form().registerForOnResume(this);
	    	    
	    Enabled(true);	    
	    getfontTypeFace();	    
	    picList = new ArrayList<String>();	    
	   	    
	    
	  }
  
  
  public void setButtonStates(String upstate, String downstate) {
	  int up = container.$context().getResources().getIdentifier(upstate, "drawable", container.$context().getPackageName());
	  int down = container.$context().getResources().getIdentifier(downstate, "drawable", container.$context().getPackageName());
	  
	  StateListDrawable drawstates = new StateListDrawable();
	  drawstates.addState(new int[] {android.R.attr.state_pressed}, container.$context().getResources().getDrawable(down));	  
	  drawstates.addState(new int[] {android.R.attr.state_enabled}, container.$context().getResources().getDrawable(up));
	  if (resourceId!= -1) {
		  ((android.widget.Button) container.$form().findViewById(resourceId)).setBackgroundDrawable(drawstates);
	  } else {
		  view.setBackgroundDrawable(drawstates);
	  }
  }
  
  public void setButtonColorStates(int upStateColor, int downStateColor) {
	  StateListDrawable drawStates = new StateListDrawable();
	  drawStates.addState(new int[] {android.R.attr.state_pressed}, container.$context().getResources().getDrawable(downStateColor));
	  drawStates.addState(new int[] {android.R.attr.state_enabled}, container.$context().getResources().getDrawable(upStateColor));
	  if (resourceId!= -1) {
		  ((android.widget.Button) container.$form().findViewById(resourceId)).setBackgroundDrawable(drawStates);
	  } else {
		  view.setBackgroundDrawable(drawStates);
	  }
  }
  
  private void getfontTypeFace() {
	
	Typeface tf;
	if (resourceId > 0) {
		android.widget.Button tmpview = (android.widget.Button) container.$form().findViewById(resourceId);
		tf = tmpview.getTypeface();
	} else {
		tf = view.getTypeface();
	}
	if (tf == null) {
		fontTypeface = Component.TYPEFACE_DEFAULT;
	} else {
		if (tf.equals(Typeface.DEFAULT)) {
			fontTypeface = Component.TYPEFACE_DEFAULT;
		} else if (tf.equals(Typeface.SERIF)) {
			fontTypeface = Component.TYPEFACE_SERIF;
		} else if (tf.equals(Typeface.SANS_SERIF)) {
			fontTypeface = Component.TYPEFACE_SANSSERIF;
		} else if (tf.equals(Typeface.MONOSPACE)) {
			fontTypeface = Component.TYPEFACE_MONOSPACE;
		}
	}
  }

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
			  if (resourceId!= -1) {
				  ViewUtil.setBackgroundImage((android.widget.Button) container.$form().findViewById(resourceId), animBackground);
			  } else {
				  ViewUtil.setBackgroundImage(view, animBackground);
			  }
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
	  if (resourceId!= -1) {
		  return container.$form().findViewById(resourceId);
	  } else {
		  return view;
	  }
  }

  /**
   * Indicates the cursor moved over the button so it is now possible
   * to click it.
   */
  
  public void GotFocus() {
    EventDispatcher.dispatchEvent(this, "GotFocus");
  }

  /**
   * Indicates the cursor moved away from the button so it is now no
   * longer possible to click it.
   */
  
  public void LostFocus() {
    EventDispatcher.dispatchEvent(this, "LostFocus");
  }

  /**
   * Returns the alignment of the button's text: center, normal
   * (e.g., left-justified if text is written left to right), or
   * opposite (e.g., right-justified if text is written left to right).
   *
   * @return  one of {@link Component#ALIGNMENT_NORMAL},
   *          {@link Component#ALIGNMENT_CENTER} or
   *          {@link Component#ALIGNMENT_OPPOSITE}
   */
 
  public int TextAlignment() {
    return textAlignment;
  }

  /**
   * Specifies the alignment of the button's text: center, normal
   * (e.g., left-justified if text is written left to right), or
   * opposite (e.g., right-justified if text is written left to right).
   *
   * @param alignment  one of {@link Component#ALIGNMENT_NORMAL},
   *                   {@link Component#ALIGNMENT_CENTER} or
   *                   {@link Component#ALIGNMENT_OPPOSITE}
   */
  
  public void TextAlignment(int alignment) {
    this.textAlignment = alignment;
    if (resourceId!= -1) {
    	TextViewUtil.setAlignment((android.widget.Button) container.$form().findViewById(resourceId), alignment, true);
    } else {
    	TextViewUtil.setAlignment(view, alignment, true);
    }
  }

  /**
   * Returns the path of the button's image.
   *
   * @return  the path of the button's image
   */
  
  public String Image() {
    return imagePath;
  }

  /**
   * Specifies the path of the button's image.
   *
   * <p/>See {@link MediaUtil#determineMediaSource} for information about what
   * a path can be.
   *
   * @param path  the path of the button's image
   */
  
  public void Image(String path) {
    imagePath = (path == null) ? "" : path;

    Drawable drawable;
    if (path.contains(".")) {
    	path = path.split("\\.")[0];
    }
    //int temp = form.getResources().getIdentifier(path, "drawable", form.getPackageName());
    try {
      //drawable = form.getResources().getDrawable(temp);
      drawable = MediaUtil.getDrawable(container.$form(), imagePath);
      firstrun=true;
    } catch (IOException ioe) {
      Log.e("ButtonBase", "Unable to load " + imagePath);
      drawable = null;
    }
    if (resourceId!= -1) {
    	ViewUtil.setBackgroundImage((android.widget.Button) container.$form().findViewById(resourceId), drawable);
    } else {
    	ViewUtil.setBackgroundImage(view, drawable);
    }
  }
  
  /**
   * Specifies whether or not to loop the animation.
   * 
   * @param loop set to true to loop, false to run
   * once.
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
			  try {				
				  animBackground.addFrame(MediaUtil.getDrawable(container.$form(), path), fps);
			  } catch (IOException ioe) {
				  Log.e("Canvas", "Unable to load " + picList.get(i));
				  animBackground = null;				  
			  }
		  }
		  if (resourceId!= -1) {
			  ViewUtil.setBackgroundImage((android.widget.Button) container.$form().findViewById(resourceId), animBackground.getFrame(0));
		  } else {
			  ViewUtil.setBackgroundImage(view, animBackground.getFrame(0));
		  }	    	
	  }
	    
  }
  
  public void setFrame(int frame) {
	  frame--;
	  if (frame < animBackground.getNumberOfFrames()) {
		  if (resourceId!= -1) {
			  ViewUtil.setBackgroundImage((android.widget.Button) container.$form().findViewById(resourceId), animBackground.getFrame(frame));
		  } else {
			  ViewUtil.setBackgroundImage(view, animBackground.getFrame(frame));
		  }
	  }
	  
  }

  /**
   * Returns the button's background color as an alpha-red-green-blue
   * integer.
   *
   * @return  background RGB color with alpha
   */
 
  public int BackgroundColor() {
    return backgroundColor;
  }

  /**
   * Specifies the button's background color as an alpha-red-green-blue
   * integer.
   *
   * @param argb  background RGB color with alpha
   */
  
  public void BackgroundColor(int argb) {
    backgroundColor = argb;
    if (argb != Component.COLOR_DEFAULT) {
    	if (resourceId!= -1) {
    		TextViewUtil.setBackgroundColor((android.widget.Button) container.$form().findViewById(resourceId), argb);
    	} else {
    		TextViewUtil.setBackgroundColor(view, argb);
    	}
    } else {
    	if (resourceId!= -1) {
    		ViewUtil.setBackgroundDrawable((android.widget.Button) container.$form().findViewById(resourceId), defaultButtonDrawable);
    	} else {
    		ViewUtil.setBackgroundDrawable(view, defaultButtonDrawable);
    	}
    }
  }

  /**
   * Returns true if the button is active and clickable.
   *
   * @return  {@code true} indicates enabled, {@code false} disabled
   */
  
  public boolean Enabled() {
	  if (resourceId!= -1) {
		  return TextViewUtil.isEnabled((android.widget.Button) container.$form().findViewById(resourceId));
	  } else {
		  return TextViewUtil.isEnabled(view);
	  }
  }

  /**
   * Specifies whether the button should be active and clickable.
   *
   * @param enabled  {@code true} for enabled, {@code false} disabled
   */
  
  public void Enabled(boolean enabled) {
	  if (resourceId!= -1) {
		  TextViewUtil.setEnabled((android.widget.Button) container.$form().findViewById(resourceId), enabled);
	  } else {
		  TextViewUtil.setEnabled(view, enabled);
	  }
  }

  /**
   * Returns true if the button's text should be bold.
   * If bold has been requested, this property will return true, even if the
   * font does not support bold.
   *
   * @return  {@code true} indicates bold, {@code false} normal
   */
 
  public boolean FontBold() {
    return bold;
  }

  /**
   * Specifies whether the button's text should be bold.
   * Some fonts do not support bold.
   *
   * @param bold  {@code true} indicates bold, {@code false} normal
   */
  
  public void FontBold(boolean bold) {
    this.bold = bold;
    if (resourceId!= -1) {
    	TextViewUtil.setFontTypeface((android.widget.Button) container.$form().findViewById(resourceId), fontTypeface, bold, italic);
    } else {
    	TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
    }
  }

  /**
   * Returns true if the button's text should be italic.
   * If italic has been requested, this property will return true, even if the
   * font does not support italic.
   *
   * @return  {@code true} indicates italic, {@code false} normal
   */
 
  public boolean FontItalic() {
    return italic;
  }

  /**
   * Specifies whether the button's text should be italic.
   * Some fonts do not support italic.
   *
   * @param italic  {@code true} indicates italic, {@code false} normal
   */
 
  public void FontItalic(boolean italic) {
    this.italic = italic;    
    if (resourceId!= -1) {
    	TextViewUtil.setFontTypeface((android.widget.Button) container.$form().findViewById(resourceId), fontTypeface, bold, italic);
    } else {
    	TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
    }
  }

  /**
   * Returns the button's text's font size, measured in pixels.
   *
   * @return  font size in pixel
   */
  
  public float FontSize() {
	  if (resourceId!= -1) {
		  return TextViewUtil.getFontSize((android.widget.Button) container.$form().findViewById(resourceId));
	  } else {
		  return TextViewUtil.getFontSize(view);
	  }
  }

  /**
   * Specifies the button's text's font size, measured in pixels.
   *
   * @param size  font size in pixel
   */
  
  public void FontSize(float size) {
	  if (resourceId!= -1) {
		  TextViewUtil.setFontSize((android.widget.Button) container.$form().findViewById(resourceId), size);
	  } else {
		  TextViewUtil.setFontSize(view, size);
	  }
  }

  /**
   * Returns the button's text's font face as default, serif, sans
   * serif, or monospace.
   *
   * @return  one of {@link Component#TYPEFACE_DEFAULT},
   *          {@link Component#TYPEFACE_SERIF},
   *          {@link Component#TYPEFACE_SANSSERIF} or
   *          {@link Component#TYPEFACE_MONOSPACE}
   */
  
  public int FontTypeface() {
    return fontTypeface;
  }

  /**
   * Specifies the button's text's font face as default, serif, sans
   * serif, or monospace.
   *
   * @param typeface  one of {@link Component#TYPEFACE_DEFAULT},
   *                  {@link Component#TYPEFACE_SERIF},
   *                  {@link Component#TYPEFACE_SANSSERIF} or
   *                  {@link Component#TYPEFACE_MONOSPACE}
   */
 
  public void FontTypeface(int typeface) {
    fontTypeface = typeface;
    if (resourceId!= -1) {
    	TextViewUtil.setFontTypeface((android.widget.Button) container.$form().findViewById(resourceId), fontTypeface, bold, italic);
    } else {
    	TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
    }
  }

  /**
   * Returns the text displayed by the button.
   *
   * @return  button caption
   */
 
  public String Text() {
	  if (resourceId!= -1) {
		  return  TextViewUtil.getText((android.widget.Button) container.$form().findViewById(resourceId));
	  } else {
		  return TextViewUtil.getText(view);
	  }
  }

  /**
   * Specifies the text displayed by the button.
   *
   * @param text  new caption for button
   */
  
  public void Text(String text) {
	  if (resourceId!= -1) {
		  TextViewUtil.setText((android.widget.Button) container.$form().findViewById(resourceId), text);
	  } else {
		  TextViewUtil.setText(view, text);
	  }
  }

  /**
   * Returns the button's text color as an alpha-red-green-blue
   * integer.
   *
   * @return  text RGB color with alpha
   */
  
  public int TextColor() {
    return textColor;
  }

  /**
   * Specifies the button's text color as an alpha-red-green-blue
   * integer.
   *
   * @param argb  text RGB color with alpha
   */
  
  public void TextColor(int argb) {
    // TODO(user): I think there is a way of only setting the color for the enabled state
    textColor = argb;
    if (argb != Component.COLOR_DEFAULT) {
    	if (resourceId!= -1) {
    		TextViewUtil.setTextColor((android.widget.Button) container.$form().findViewById(resourceId), argb);
    	} else {
    		TextViewUtil.setTextColor(view, argb);
    	}
    } else {
    	if (resourceId!= -1) {
    		TextViewUtil.setTextColors((android.widget.Button) container.$form().findViewById(resourceId), defaultColorStateList);
    	} else {
    		TextViewUtil.setTextColors(view, defaultColorStateList);
    	}
    }
  }

  public abstract void click();

  // Override this if your component actually will consume a long
  // click.  A 'false' returned from this function will cause a long
  // click to be interpreted as a click (and the click function will
  // be called).
  public boolean longClick() {
    return false;
  }

  // OnClickListener implementation

  @Override
  public void onClick(View view) {
    click();
  }

  // OnFocusChangeListener implementation

  @Override
  public void onFocusChange(View previouslyFocused, boolean gainFocus) {
    if (gainFocus) {
      GotFocus();
    } else {
      LostFocus();
    }
  }

  // OnLongClickListener implementation

  @Override
  public boolean onLongClick(View view) {
    return longClick();
  }
  
  // AJB change - OnTouchListener
  
 

  @Override
  public boolean onTouch(View v, MotionEvent event) {
  	// TODO Auto-generated method stub
  	if (event.getAction()==MotionEvent.ACTION_DOWN) {
  		EventDispatcher.dispatchEvent(this, "DownState");
  	}
  	if (event.getAction()==MotionEvent.ACTION_UP) {
  		EventDispatcher.dispatchEvent(this, "UpState");
  	}
  	return false;
  }
  
   
  @Override
	public void onStop() {
		
		if (autoToggle) {
			if (animBackground != null) {
				if (animBackground.isRunning()) {
					animBackground.stop();
				}
			}
		}
		
	}
  
  @Override
	public void postAnimEvent() {
		EventDispatcher.dispatchEvent(this, "AnimationMiddle");
		
	}

	@Override
	public void onResume() {
		
		if (autoToggle && animRunning && animBackground != null) {
			animBackground.start();			
		}
		
	}
		
		 public void AutoToggle(boolean autotoggle) {
				this.autoToggle = autotoggle;
			}
}
