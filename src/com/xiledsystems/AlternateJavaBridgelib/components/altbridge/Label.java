package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.view.View;
import android.widget.TextView;

/**
 * Label containing a text string.
 *
 */

public final class Label extends AndroidViewComponent {

  private final TextView view;

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

  /**
   * Creates a new Label component.
   *
   * @param container  container, component will be placed in
   */
  public Label(ComponentContainer container) {
    super(container);
    view = new TextView(container.$context());

    // Adds the component to its designated container
    container.$add(this);

    // Default property values
    TextAlignment(Component.ALIGNMENT_NORMAL);
    BackgroundColor(Component.COLOR_NONE);
    fontTypeface = Component.TYPEFACE_DEFAULT;
    TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
    FontSize(Component.FONT_DEFAULT_SIZE);
    Text("");
    TextColor(Component.COLOR_BLACK);
  }

  @Override
  public View getView() {
    return view;
  }

  /**
   * Returns the alignment of the label's text: center, normal
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
   * Specifies the alignment of the label's text: center, normal
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
   * Returns the label's background color as an alpha-red-green-blue
   * integer.
   *
   * @return  background RGB color with alpha
   */
  
  public int BackgroundColor() {
    return backgroundColor;
  }

  /**
   * Specifies the label's background color as an alpha-red-green-blue
   * integer.
   *
   * @param argb  background RGB color with alpha
   */
  
  public void BackgroundColor(int argb) {
    backgroundColor = argb;
    if (argb != Component.COLOR_DEFAULT) {
      TextViewUtil.setBackgroundColor(view, argb);
    } else {
      TextViewUtil.setBackgroundColor(view, Component.COLOR_NONE);
    }
  }

  /**
   * Returns true if the label's text should be bold.
   * If bold has been requested, this property will return true, even if the
   * font does not support bold.
   *
   * @return  {@code true} indicates bold, {@code false} normal
   */
  
  public boolean FontBold() {
    return bold;
  }

  /**
   * Specifies whether the label's text should be bold.
   * Some fonts do not support bold.
   *
   * @param bold  {@code true} indicates bold, {@code false} normal
   */
  
  public void FontBold(boolean bold) {
    this.bold = bold;
    TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
  }

  /**
   * Returns true if the label's text should be italic.
   * If italic has been requested, this property will return true, even if the
   * font does not support italic.
   *
   * @return  {@code true} indicates italic, {@code false} normal
   */
  
  public boolean FontItalic() {
    return italic;
  }

  /**
   * Specifies whether the label's text should be italic.
   * Some fonts do not support italic.
   *
   * @param italic  {@code true} indicates italic, {@code false} normal
   */
  
  public void FontItalic(boolean italic) {
    this.italic = italic;
    TextViewUtil.setFontTypeface(view, fontTypeface, bold, italic);
  }

  /**
   * Returns the label's text's font size, measured in pixels.
   *
   * @return  font size in pixel
   */
  
  public float FontSize() {
    return TextViewUtil.getFontSize(view);
  }

  /**
   * Specifies the label's text's font size, measured in pixels.
   *
   * @param size  font size in pixel
   */
  
  public void FontSize(float size) {
    TextViewUtil.setFontSize(view, size);
  }

  /**
   * Returns the label's text's font face as default, serif, sans
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
   * Specifies the label's text's font face as default, serif, sans
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
   * Returns the text displayed by the label.
   *
   * @return  label caption
   */
  
  public String Text() {
    return TextViewUtil.getText(view);
  }

  /**
   * Specifies the text displayed by the label.
   *
   * @param text  new caption for label
   */
  
  public void Text(String text) {
    TextViewUtil.setText(view, text);
  }

  /**
   * Returns the label's text color as an alpha-red-green-blue
   * integer.
   *
   * @return  text RGB color with alpha
   */
  
  public int TextColor() {
    return textColor;
  }

  /**
   * Specifies the label's text color as an alpha-red-green-blue
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
}
