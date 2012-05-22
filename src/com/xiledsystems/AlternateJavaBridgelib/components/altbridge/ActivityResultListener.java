package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.content.Intent;

/**
 * Callback for receiving Activity results
 *
 */
public interface ActivityResultListener {

  /**
   * The callback method used to report Activity results back to the caller.
   * @param requestCode the originally passed in request code. Used to identify the call.
   * @param resultCode the returned result code: {@link android.app.Activity.RESULT_OK} or
   *                   {@link android.app.Activity.RESULT_CANCELED}
   * @param data the returned data, encapsulated as an {@link Intent}.
   */
  void resultReturned(int requestCode, int resultCode, Intent data);
}
