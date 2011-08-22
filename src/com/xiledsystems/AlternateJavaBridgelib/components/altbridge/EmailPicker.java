package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.widget.AutoCompleteTextView;

/**
 * Text box using auto-completion to pick out an email address from contacts.
 *
 */


public class EmailPicker extends TextBoxBase {

  private final EmailAddressAdapter addressAdapter;

  /**
   * Create a new EmailPicker component.
   *
   * @param container the parent container.
   */
  public EmailPicker(ComponentContainer container) {
    super(container, new AutoCompleteTextView(container.$context()));
    addressAdapter = new EmailAddressAdapter(container.$context());
    ((AutoCompleteTextView) super.view).setAdapter(addressAdapter);
  }

  /**
   * Event raised when this component is selected for input, such as by
   * the user touching it.
   */
  
  @Override
  public void GotFocus() {
    if (SdkLevel.getLevel() > SdkLevel.LEVEL_DONUT) {
      container.$form().dispatchErrorOccurredEvent(this, "GotFocus",
          ErrorMessages.ERROR_FUNCTIONALITY_NOT_SUPPORTED_EMAIL_PICKER);
    }
    EventDispatcher.dispatchEvent(this, "GotFocus");
  }
}
