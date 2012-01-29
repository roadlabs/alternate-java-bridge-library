package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.TextViewUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Check box with the ability to detect initialization, focus
 * change (mousing on or off of it), and user clicks.
 *
 */

public final class CheckBox extends AndroidViewComponent
    implements OnCheckedChangeListener, OnFocusChangeListener {

  private final android.widget.CheckBox view;
     
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

  /**
   * Creates a new CheckBox component.
   *
   * @param container  container, component will be placed in
   */
  public CheckBox(ComponentContainer container) {
    super(container);
    
    view = new android.widget.CheckBox(container.$context());

    // Listen to focus changes
    view.setOnFocusChangeListener(this);
    view.setOnCheckedChangeListener(this);
    

    // Adds the component to its designated container
    container.$add(this);
    BackgroundColor(Component.COLOR_NONE);
    Enabled(true);
    fontTypeface = Component.TYPEFACE_DEFAULT;
    TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
    FontSize(Component.FONT_DEFAULT_SIZE);
    Text("");
    TextColor(Component.COLOR_BLACK);
    Checked(false);
  }
  
  public CheckBox(ComponentContainer container, int resourceId) {
	    super(container, resourceId);
	    
	    // view = (android.widget.CheckBox) form.findViewById(resourceId);
	    view = null;
	    android.widget.CheckBox view = (android.widget.CheckBox) container.$form().findViewById(resourceId);
	    // Listen to focus changes
	    view.setOnFocusChangeListener(this);
	    view.setOnCheckedChangeListener(this);
	    	    
	    BackgroundColor(Component.COLOR_NONE);
	    Enabled(true);
	    fontTypeface = Component.TYPEFACE_DEFAULT;
	    TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
	    FontSize(Component.FONT_DEFAULT_SIZE);
	    Text("");
	    TextColor(Component.COLOR_BLACK);
	    Checked(false);
	  }

  @Override
  public View getView() {
	  if (resourceId!= -1) {
		  return (android.widget.CheckBox) container.$context().findViewById(resourceId);
	  } else {
		  return view;
	  }
  }

  /**
   * Default Changed event handler.
   */
  
  public void Changed() {
    EventDispatcher.dispatchEvent(this, "Changed");
  }

  /**
   * Default GotFocus event handler.
   */
  
  public void GotFocus() {
    EventDispatcher.dispatchEvent(this, "GotFocus");
  }

  /**
   * Default LostFocus event handler.
   */
  
  public void LostFocus() {
    EventDispatcher.dispatchEvent(this, "LostFocus");
  }

  /**
   * Returns the checkbox's background color as an alpha-red-green-blue
   * integer.
   *
   * @return  background RGB color with alpha
   */
  
  public int BackgroundColor() {
    return backgroundColor;
  }

  /**
   * Specifies the checkbox's background color as an alpha-red-green-blue
   * integer.
   *
   * @param argb  background RGB color with alpha
   */
  
  public void BackgroundColor(int argb) {
    backgroundColor = argb;
    if (argb != Component.COLOR_DEFAULT) {
    	if (resourceId!= -1) {
    		TextViewUtil.setBackgroundColor((android.widget.CheckBox) container.$context().findViewById(resourceId), argb);
    	} else {
    		TextViewUtil.setBackgroundColor(view, argb);
    	}
    } else {
    	if (resourceId!= -1) {
    		TextViewUtil.setBackgroundColor((android.widget.CheckBox) container.$context().findViewById(resourceId), Component.COLOR_NONE);
    	} else {
    		TextViewUtil.setBackgroundColor(view, Component.COLOR_NONE);
    	}
    }
  }

  /**
   * Returns true if the checkbox is active and clickable.
   *
   * @return  {@code true} indicates enabled, {@code false} disabled
   */
  
  public boolean Enabled() {
	  if (resourceId!= -1) {
		  return container.$context().findViewById(resourceId).isEnabled();
	  } else {
		  return view.isEnabled();
	  }
  }

  /**
   * Specifies whether the checkbox should be active and clickable.
   *
   * @param enabled  {@code true} for enabled, {@code false} disabled
   */
  
  public void Enabled(boolean enabled) {
	  if (resourceId!= -1) {
		  TextViewUtil.setEnabled((android.widget.CheckBox) container.$context().findViewById(resourceId), enabled);
	  } else {
		  TextViewUtil.setEnabled(view, enabled);
	  }
  }

  /**
   * Returns true if the checkbox's text should be bold.
   * If bold has been requested, this property will return true, even if the
   * font does not support bold.
   *
   * @return  {@code true} indicates bold, {@code false} normal
   */
 
  public boolean FontBold() {
    return bold;
  }

  /**
   * Specifies whether the checkbox's text should be bold.
   * Some fonts do not support bold.
   *
   * @param bold  {@code true} indicates bold, {@code false} normal
   */
  
  public void FontBold(boolean bold) {
    this.bold = bold;
    if (resourceId != -1) {
    	TextViewUtil.setFontTypeface((android.widget.CheckBox) container.$context().findViewById(resourceId), fontTypeface, bold, italic);
    } else {
    	TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
    }
  }

  /**
   * Returns true if the checkbox's text should be italic.
   * If italic has been requested, this property will return true, even if the
   * font does not support italic.
   *
   * @return  {@code true} indicates italic, {@code false} normal
   */
 
  public boolean FontItalic() {
    return italic;
  }

  /**
   * Specifies whether the checkbox's text should be italic.
   * Some fonts do not support italic.
   *
   * @param italic  {@code true} indicates italic, {@code false} normal
   */
  
  public void FontItalic(boolean italic) {
    this.italic = italic;
    if (resourceId!=-1) {
    	TextViewUtil.setFontTypeface((android.widget.CheckBox) container.$context().findViewById(resourceId), fontTypeface, bold, italic);
    } else {
    	TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
    }
  }

  /**
   * Returns the checkbox's text's font size, measured in pixels.
   *
   * @return  font size in pixel
   */
  
  public float FontSize() {
	  if (resourceId!=-1) {
		  return TextViewUtil.getFontSize((android.widget.CheckBox) container.$context().findViewById(resourceId));
	  } else {
		  return TextViewUtil.getFontSize(view);
	  }
  }

  /**
   * Specifies the checkbox's text's font size, measured in pixels.
   *
   * @param size  font size in pixel
   */
  
  public void FontSize(float size) {
	  if (resourceId!=-1) {
		  TextViewUtil.setFontSize((android.widget.CheckBox) container.$context().findViewById(resourceId), size);
	  } else {
		  TextViewUtil.setFontSize(view, size);
	  }
  }

  /**
   * Returns the checkbox's text's font face as default, serif, sans
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
   * Specifies the checkbox's text's font face as default, serif, sans
   * serif, or monospace.
   *
   * @param typeface  one of {@link Component#TYPEFACE_DEFAULT},
   *                  {@link Component#TYPEFACE_SERIF},
   *                  {@link Component#TYPEFACE_SANSSERIF} or
   *                  {@link Component#TYPEFACE_MONOSPACE}
   */
  
  public void FontTypeface(int typeface) {
    fontTypeface = typeface;
    if (resourceId!=-1) {
    	TextViewUtil.setFontTypeface((android.widget.CheckBox) container.$context().findViewById(resourceId), fontTypeface, bold, italic);
    } else {
    	TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
    }
  }

  /**
   * Returns the text displayed by the checkbox.
   *
   * @return  checkbox caption
   */
  
  public String Text() {
	  if (resourceId!=-1) {
		  return TextViewUtil.getText((android.widget.CheckBox) container.$context().findViewById(resourceId));
	  } else {
		  return TextViewUtil.getText(view);
	  }
  }

  /**
   * Specifies the text displayed by the checkbox.
   *
   * @param text  new caption for checkbox
   */
  
  public void Text(String text) {
	  if (resourceId!=-1) {
		  TextViewUtil.setText((android.widget.CheckBox) container.$context().findViewById(resourceId), text);
	  } else {
		  TextViewUtil.setText(view, text);
	  }
  }

  /**
   * Returns the checkbox's text color as an alpha-red-green-blue
   * integer.
   *
   * @return  text RGB color with alpha
   */
  
  public int TextColor() {
    return textColor;
  }

  /**
   * Specifies the checkbox's text color as an alpha-red-green-blue
   * integer.
   *
   * @param argb  text RGB color with alpha
   */
  
  public void TextColor(int argb) {
    textColor = argb;
    if (argb != Component.COLOR_DEFAULT) {
    	if (resourceId!=-1) {
    		TextViewUtil.setTextColor((android.widget.CheckBox) container.$context().findViewById(resourceId), argb);
    	} else {
    		TextViewUtil.setTextColor(view, argb);
    	}
    } else {
    	if (resourceId!=-1) {
    		TextViewUtil.setTextColor((android.widget.CheckBox) container.$context().findViewById(resourceId), Component.COLOR_BLACK);
    	} else {
    		TextViewUtil.setTextColor(view, Component.COLOR_BLACK);
    	}
    }
  }

  /**
   * Returns true if the checkbox is checked.
   *
   * @return  {@code true} indicates checked, {@code false} unchecked
   */
  
  public boolean Checked() {
	  if (resourceId!=-1) {
		  return ((android.widget.CheckBox) container.$context().findViewById(resourceId)).isChecked();
	  } else {
		  return view.isChecked();
	  }
  }

  /**
   * Checked property setter method.
   *
   * @param value  {@code true} indicates checked, {@code false} unchecked
   */
  
  public void Checked(boolean value) {
	  if (resourceId!=-1) {
		  ((android.widget.CheckBox) container.$context().findViewById(resourceId)).setChecked(value);
		  ((android.widget.CheckBox) container.$context().findViewById(resourceId)).invalidate();
	  } else {
		  view.setChecked(value);
		  view.invalidate();
	  }
  }

  // OnCheckedChangeListener implementation

  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    Changed();
  }

  // OnFocusChangeListener implementation

  public void onFocusChange(View previouslyFocused, boolean gainFocus) {
    if (gainFocus) {
      GotFocus();
    } else {
      LostFocus();
    }
  }
  
  @Override
	public void postAnimEvent() {
		EventDispatcher.dispatchEvent(this, "AnimationMiddle");
		
	}
    
}
