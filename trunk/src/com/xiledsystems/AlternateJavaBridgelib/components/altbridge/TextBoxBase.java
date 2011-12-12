package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.TextViewUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ViewUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

/**
 * Underlying base class for TextBox, not directly accessible to Simple
 * programmers.
 *
 */


public abstract class TextBoxBase extends AndroidViewComponent
    implements OnFocusChangeListener {

  protected final EditText view;
  
  //AJB
  
  private final Form form;

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

  // Backing for hint text
  private String hint;

  // Backing for text color
  private int textColor;

  // This is our handle on Android's nice 3-d default textbox.
  private Drawable defaultTextBoxDrawable;

  /**
   * Creates a new TextBoxBase component
   *
   * @param container  container that the component will be placed in
   * @param textview   the underlying EditText object that maintains the text
   */
  public TextBoxBase(ComponentContainer container, EditText textview) {
    super(container);
    form = container.$form();
    view = textview;

    // Listen to focus changes
    view.setOnFocusChangeListener(this);
    
    defaultTextBoxDrawable = view.getBackground();

    // Add a transformation method to provide input validation
    /* TODO(user): see comment above)
    setTransformationMethod(new ValidationTransformationMethod());
    */

    // Adds the component to its designated container
    container.$add(this);

    container.setChildWidth(this, ComponentConstants.TEXTBOX_PREFERRED_WIDTH);

    TextAlignment(Component.ALIGNMENT_NORMAL);
    // Leave the nice default background color. Users can change it to "none" if they like
    //
    // TODO(user): if we make a change here we also need to change the default property value.
    // Eventually I hope to simplify this so it has to be changed in one location
    // only). Maybe we need another color value which would be 'SYSTEM_DEFAULT' which
    // will not attempt to explicitly initialize with any of the properties with any
    // particular value.
    // BackgroundColor(Component.COLOR_NONE);
    Enabled(true);
    fontTypeface = Component.TYPEFACE_DEFAULT;
    TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
    FontSize(Component.FONT_DEFAULT_SIZE);
    Hint("");
    Text("");
    TextColor(Component.COLOR_BLACK);
  }

  @Override
  public View getView() {
    return view;
  }

  /**
   * Event raised when this component is selected for input, such as by
   * the user touching it.
   */
  
  public void GotFocus() {
    EventDispatcher.dispatchEvent(this, "GotFocus");
  }

  /**
   * Event raised when this component is no longer selected for input, such
   * as if the user touches a different text box.
   */
  
  public void LostFocus() {
    EventDispatcher.dispatchEvent(this, "LostFocus");
  }

  /**
   * Default Validate event handler.
   */
  /* TODO(user): Restore event if needed.
  @SimpleEvent
  public void Validate(String text, BooleanReferenceParameter accept) {
    EventDispatcher.dispatchEvent(this, "Validate", text, accept);
  }
  */

  /**
   * Returns the alignment of the textbox's text: center, normal
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
   * Specifies the alignment of the textbox's text: center, normal
   * (e.g., left-justified if text is written left to right), or
   * opposite (e.g., right-justified if text is written left to right).
   *
   * @param alignment  one of {@link Component#ALIGNMENT_NORMAL},
   *                   {@link Component#ALIGNMENT_CENTER} or
   *                   {@link Component#ALIGNMENT_OPPOSITE}
   */
  
  public void TextAlignment(int alignment) {
    this.textAlignment = alignment;
    TextViewUtil.setAlignment(view, alignment, false);
  }

  /**
   * Returns the textbox's background color as an alpha-red-green-blue
   * integer.
   *
   * @return  background RGB color with alpha
   */
  
  public int BackgroundColor() {
    return backgroundColor;
  }

  /**
   * Specifies the textbox's background color as an alpha-red-green-blue
   * integer.
   *
   * @param argb  background RGB color with alpha
   */
  
  public void BackgroundColor(int argb) {
    backgroundColor = argb;
    if (argb != Component.COLOR_DEFAULT) {
      TextViewUtil.setBackgroundColor(view, argb);
    } else {
      ViewUtil.setBackgroundDrawable(view, defaultTextBoxDrawable);
    }
  }

  /**
   * Returns true if the textbox is active and useable.
   *
   * @return  {@code true} indicates enabled, {@code false} disabled
   */
  
  public boolean Enabled() {
    return TextViewUtil.isEnabled(view);
  }

  /**
   * Specifies whether the textbox should be active and useable.
   *
   * @param enabled  {@code true} for enabled, {@code false} disabled
   */
  
  public void Enabled(boolean enabled) {
    TextViewUtil.setEnabled(view, enabled);
  }

  /**
   * Returns true if the textbox's text should be bold.
   * If bold has been requested, this property will return true, even if the
   * font does not support bold.
   *
   * @return  {@code true} indicates bold, {@code false} normal
   */
  
  public boolean FontBold() {
    return bold;
  }

  /**
   * Specifies whether the textbox's text should be bold.
   * Some fonts do not support bold.
   *
   * @param bold  {@code true} indicates bold, {@code false} normal
   */
  
  public void FontBold(boolean bold) {
    this.bold = bold;
    TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
  }

  /**
   * Returns true if the textbox's text should be italic.
   * If italic has been requested, this property will return true, even if the
   * font does not support italic.
   *
   * @return  {@code true} indicates italic, {@code false} normal
   */
  
  public boolean FontItalic() {
    return italic;
  }

  /**
   * Specifies whether the textbox's text should be italic.
   * Some fonts do not support italic.
   *
   * @param italic  {@code true} indicates italic, {@code false} normal
   */
  
  public void FontItalic(boolean italic) {
    this.italic = italic;
    TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
  }

  /**
   * Returns the textbox's text's font size, measured in pixels.
   *
   * @return  font size in pixel
   */
  
  public float FontSize() {
    return TextViewUtil.getFontSize(view);
  }

  /**
   * Specifies the textbox's text's font size, measured in pixels.
   *
   * @param size  font size in pixel
   */
  
  public void FontSize(float size) {
    TextViewUtil.setFontSize(view, size);
  }

  /**
   * Returns the textbox's text's font face as default, serif, sans
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
   * Specifies the textbox's text's font face as default, serif, sans
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
   * Hint property getter method.
   *
   * @return  hint text
   */
  
  public String Hint() {
    return hint;
  }

  /**
   * Hint property setter method.
   *
   * @param hint  hint text
   */
  
  public void Hint(String hint) {
    this.hint = hint;
    view.setHint(hint);
    view.invalidate();
  }

  /**
   * Returns the textbox contents.
   *
   * @return  text box contents
   */
 
  public String Text() {
    return TextViewUtil.getText(view);
  }

  /**
   * Specifies the textbox contents.
   *
   * @param text  new text in text box
   */
 
  public void Text(String text) {
    TextViewUtil.setText(view, text);
  }

  /**
   * Returns the textbox's text color as an alpha-red-green-blue
   * integer.
   *
   * @return  text RGB color with alpha
   */
  
  public int TextColor() {
    return textColor;
  }

  /**
   * Specifies the textbox's text color as an alpha-red-green-blue
   * integer.
   *
   * @param argb  text RGB color with alpha
   */
 
  public void TextColor(int argb) {
    textColor = argb;
    if (argb != Component.COLOR_DEFAULT) {
      TextViewUtil.setTextColor(view, argb);
    } else {
      TextViewUtil.setTextColor(view, Component.COLOR_BLACK);
    }
  }

  // OnFocusChangeListener implementation

  @Override
  public void onFocusChange(View previouslyFocused, boolean gainFocus) {
    if (gainFocus) {
      // Initialize content backing for input validation
      // TODO(user): this field stayed in TextBox. It isn't being used yet,
      // and I'm not sure what to do with this assignment.
      // text = TextViewUtil.getText(view);

      GotFocus();
    } else {
      LostFocus();
    }
  }
    
}