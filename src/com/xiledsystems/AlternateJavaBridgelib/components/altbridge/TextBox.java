package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.text.InputType;

import android.widget.EditText;

/**
 * A box in which the user can enter text.
 *
 */

public final class TextBox extends TextBoxBase {
  /* TODO(user): this code requires Android SDK M5 or newer - we are currently on M3
  enables this when we upgrade

  // Backing for text during validation
  private String text;

  private class ValidationTransformationMethod extends TransformationMethod {
   @Override
   public CharSequence getTransformation(CharSequence source) {
     BooleanReferenceParameter accept = new BooleanReferenceParameter(false);
     Validate(source.toString, accept);

     if (accept.get()) {
       text = source.toString();
     }

     return text;
   }
 }
*/

  // If true, then accept numeric keyboard input only
  private boolean acceptsNumbersOnly;

  /**
   * Creates a new TextBox component.
   *
   * @param container  container, component will be placed in
   */
  public TextBox(ComponentContainer container) {
    super(container, new EditText(container.$context()));
    NumbersOnly(false);
  }
  
  public TextBox(ComponentContainer container, int resourceId) {
	    super(container, (EditText) container.$form().findViewById(resourceId), resourceId);
	    NumbersOnly(false);
	  }


  /**
   * NumbersOnly property getter method.
   *
   * @return {@code true} indicates that the textbox accepts numbers only, {@code false} indicates
   *         that it accepts any text
   */
  
  public boolean NumbersOnly() {
    return acceptsNumbersOnly;
  }

  /**
   * NumersOnly property setter method.
   *
   * @param acceptsNumbersOnly {@code true} restricts input to numeric,
   * {@code false} allows any text
   */
  
  public void NumbersOnly(boolean acceptsNumbersOnly) {
    if (acceptsNumbersOnly) {
      view.setInputType(
          InputType.TYPE_CLASS_NUMBER |
          InputType.TYPE_NUMBER_FLAG_SIGNED |
          InputType.TYPE_NUMBER_FLAG_DECIMAL);
    } else {
      view.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    }
    this.acceptsNumbersOnly = acceptsNumbersOnly;
  }


}
