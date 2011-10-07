package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.io.IOException;
import java.util.ArrayList;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.Form;
import com.xiledsystems.AlternateJavaBridgelib.Form2;
import com.xiledsystems.AlternateJavaBridgelib.OnInitializeListener;
import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.MediaUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.TextViewUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ViewUtil;

import android.content.res.ColorStateList;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
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
  
  private final Form form;
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
    form = container.$form();
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

  @Override
  public View getView() {
    return view;
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
    TextViewUtil.setAlignment(view, alignment, true);
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

    ViewUtil.setBackgroundImage(view, drawable);
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
			  //int temp = form.getResources().getIdentifier(path, "drawable", form.getPackageName());
			  try {
				  
				 // animBackground.addFrame(form.getResources().getDrawable(temp), fps);
				  animBackground.addFrame(MediaUtil.getDrawable(form, path), fps);
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
      TextViewUtil.setBackgroundColor(view, argb);
    } else {
      ViewUtil.setBackgroundDrawable(view, defaultButtonDrawable);
    }
  }

  /**
   * Returns true if the button is active and clickable.
   *
   * @return  {@code true} indicates enabled, {@code false} disabled
   */
  
  public boolean Enabled() {
    return TextViewUtil.isEnabled(view);
  }

  /**
   * Specifies whether the button should be active and clickable.
   *
   * @param enabled  {@code true} for enabled, {@code false} disabled
   */
  
  public void Enabled(boolean enabled) {
    TextViewUtil.setEnabled(view, enabled);
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
    TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
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
    TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
  }

  /**
   * Returns the button's text's font size, measured in pixels.
   *
   * @return  font size in pixel
   */
  
  public float FontSize() {
    return TextViewUtil.getFontSize(view);
  }

  /**
   * Specifies the button's text's font size, measured in pixels.
   *
   * @param size  font size in pixel
   */
  
  public void FontSize(float size) {
    TextViewUtil.setFontSize(view, size);
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
    TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
  }

  /**
   * Returns the text displayed by the button.
   *
   * @return  button caption
   */
 
  public String Text() {
    return TextViewUtil.getText(view);
  }

  /**
   * Specifies the text displayed by the button.
   *
   * @param text  new caption for button
   */
  
  public void Text(String text) {
    TextViewUtil.setText(view, text);
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
      TextViewUtil.setTextColor(view, argb);
    } else {
      TextViewUtil.setTextColors(view, defaultColorStateList);
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
		
		 public void AutoToggle(boolean autotoggle) {
				this.autoToggle = autotoggle;
			}
}
