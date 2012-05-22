package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.PhoneCallUtil;

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
  

  /**
   * Creates a Phone Call component.
   *
   * @param container container, component will be placed in
   */
  public PhoneCall(ComponentContainer container) {
    super(container);
    
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
    PhoneCallUtil.makePhoneCall(container.$context(), phoneNumber);
  }
}
