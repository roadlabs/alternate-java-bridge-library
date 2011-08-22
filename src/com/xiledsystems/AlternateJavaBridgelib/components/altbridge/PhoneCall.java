package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.content.Context;

/**
 * Component for making a phone call to a programatically-specified number.
 *
 * TODO(user): Note that the initial carrier for Android phones only supports 3 participants
 *              in a conference call, so that's all that the current implementation of this
 *              component supports.  In the future we can generalize this to more participants.
 *
 */

public class PhoneCall extends AndroidNonvisibleComponent implements Component {

  private String phoneNumber;
  private final Context context;

  /**
   * Creates a Phone Call component.
   *
   * @param container container, component will be placed in
   */
  public PhoneCall(ComponentContainer container) {
    super(container.$form());
    context = container.$context();
    PhoneNumber("");
  }

  /**
   * PhoneNumber property getter method.
   */
  
  public String PhoneNumber() {
    return phoneNumber;
  }

  /**
   * PhoneNumber property setter method: sets a phone number to call.
   *
   * @param phoneNumber a phone number to call
   */
  
  public void PhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  
  public void MakePhoneCall() {
    PhoneCallUtil.makePhoneCall(context, phoneNumber);
  }
}
