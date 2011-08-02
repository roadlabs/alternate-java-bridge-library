package com.xiledsystems.AlternateJavaBridgelib;

import java.io.IOException;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView.OnEditorActionListener;

import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.components.android.AndroidViewComponent;
import com.google.devtools.simple.runtime.components.android.ComponentContainer;
import com.google.devtools.simple.runtime.components.android.Form;
import com.google.devtools.simple.runtime.components.android.util.MediaUtil;
import com.google.devtools.simple.runtime.components.android.util.TextViewUtil;
import com.google.devtools.simple.runtime.components.android.util.ViewUtil;
import com.google.devtools.simple.runtime.events.EventDispatcher;

public abstract class ButtonBase2 extends AndroidViewComponent
implements OnClickListener, OnFocusChangeListener, OnLongClickListener, OnTouchListener, OnInitializeListener {

	private final android.widget.Button view;
	private final Form form;
	
	 private double widthMultiplier;
	 private double heightMultiplier;

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
public ButtonBase2(ComponentContainer container) {
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
if (form instanceof Form2) {
	((Form2) form).registerForOnInitialize(this);
}

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
try {
  drawable = MediaUtil.getDrawable(container.$form(), imagePath);
} catch (IOException ioe) {
  Log.e("ButtonBase", "Unable to load " + imagePath);
  drawable = null;
}

ViewUtil.setBackgroundImage(view, drawable);
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

// OnKeyListener implementation
// This only swaps images when the dualImages boolean is set. This boolean is set when you specify the two
// image filenames. 
/*
@Override
public boolean onKey(View v, int keyCode, KeyEvent event) {
	if (dualImages) {
		switch (keyCode) {
		case KeyEvent.ACTION_DOWN:	
			this.Image(buttonImages[1]);			
			return true;
			
		case KeyEvent.ACTION_UP:
			this.Image(buttonImages[0]);
			return true;
		}
	}
	return false;
}
*/
@Override
public void onInitialize() {
	
	this.Width((int) (((Form2) form).scrnWidth * widthMultiplier));
	this.Height((int) (((Form2) form).scrnHeight * heightMultiplier));
	
	
}
public void setMultipliers(double widthmultiplier, double heightmultiplier) {
	
	this.widthMultiplier = widthmultiplier;
	this.heightMultiplier = heightmultiplier;
	
}

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

}
