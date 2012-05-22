package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.util.Log;


public final class RuntimeErrorAlert {


/**
 * Show a runtime error alert with a single button.  Pressing the button will kill the application.
 * This a called by the Yail runtime mechanism
 * Components should throw a YailRuntimeErrorException, which will call this.
 */


  public static void alert(final Object context,
      final String message, final String title,final String buttonText) {
    Log.i("RuntimeErrorAlert", "in alert");
    AlertDialog alertDialog = new AlertDialog.Builder((Context) context).create();
    alertDialog.setTitle(title);
    alertDialog.setMessage(message);
    alertDialog.setButton(buttonText, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        ((Activity) context).finish();
      }});
    if (message == null) {
      // Avoid passing null to Log.e, which would cause a NullPointerException.
      Log.e(RuntimeErrorAlert.class.getName(), "No error message available");
    } else {
      Log.e(RuntimeErrorAlert.class.getName(), message);
    }
    alertDialog.show();
  }
}
