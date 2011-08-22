package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.app.Activity;
import android.util.Log;

import com.google.android.googlelogin.GoogleLoginServiceBlockingHelper;

/**
 * Utility class for accessing the GoogleLoginService.
 *
 *
 */
public class LoginServiceUtil {

  private static final String LOG_TAG = "LoginServiceUtil";

  private LoginServiceUtil() {
  }

  /**
   * Retrieves the email address registered with the phone. Since this
   * uses a blocking service, do not call this from the UI thread. It will
   * cause the program to freeze up and will eventually need to be force
   * closed.
   *
   * @param activityContext The Activity of the requester.
   * @return The email address registered with the phone or the empty string
   * if an exception is encountered.
   */
  public static String getPhoneEmailAddress(Activity activityContext) {
    String userEmail = "";

    try {
      final GoogleLoginServiceBlockingHelper googleLoginService =
          new GoogleLoginServiceBlockingHelper(activityContext);
      userEmail = googleLoginService.getAccount(false);
      googleLoginService.close();
    } catch (Exception e) {
      // We'd rather catch the more specific GoogleLoginServiceNotFoundException but doing so
      // caused a very strange NoClassDefFoundError when Simple tried to analyze
      // this class file.
      Log.w(LOG_TAG, e);
    }

    return userEmail;
  }
}
