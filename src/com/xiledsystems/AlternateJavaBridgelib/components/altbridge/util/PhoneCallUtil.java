package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Helper functions for making phone calls.
 * TODO(user): eventually support making conference calls when the Android
 * framework supports it via an API
 *
 */

public class PhoneCallUtil {

  private PhoneCallUtil() {
  }

  public static void makePhoneCall(Context context, String phoneNumber) {
    if (null != phoneNumber && phoneNumber.length() > 0) {
      Uri phoneUri = Uri.parse("tel:" + phoneNumber);   // Could also use Uri.Builder
      Intent intent = new Intent(Intent.ACTION_CALL, phoneUri);
      context.startActivity(intent);
    }
  }
}
