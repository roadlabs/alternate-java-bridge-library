package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;


/**
 * The Notifier component displays alert messages.  The kinds of messages are:
 * (a) ShowMessageDialog: user must dismiss the message by pressing a button.
 * (b) ShowChooseDialog): displays two buttons to let the user choose one of two responses,
 * for example, yes or no.
 * (c) ShowTextDialog: lets the user enter text in response to the message.
 * (d) AlertUser: displays an alert that goes away by itself after
 * a short time.
 * ShowChooseDialog raises the event AfterChoosing, whose argument is the text on the
 * button that was pressed. ShowTextDialog raises the event AfterTextInput, whose argument is the
 * text the user supplied.
 *
 */

//TODO(user): Change the dialog methods to be synchronous and return values rather
// than signaling events; or at least to use one-shot events, when we implement those.

//TODO(user): Figure out how/if these dialogs should deal with onPause.



public final class Notifier extends AndroidNonvisibleComponent implements Component {

  private static final String LOG_TAG = "Notifier";
  private final Activity activity;
  private final Handler handler;
  private boolean isaService = false;
  private final Service service;

  /**
   * Creates a new Notifier component.
   *
   * @param container the enclosing component
   */
  public Notifier (ComponentContainer container) {
    super(container.$form());
    activity = container.$context();
    handler = new Handler();
    service = null;
    isaService = false;
  }
  
  public Notifier (SvcComponentContainer container) {
	  super(container.$formService());
	  activity = null;
	  handler = new Handler();
	  service = container.$context();
	  isaService = true;
  }

  /**
   * Display an alert dialog with a single button
   *
   * @param message the text in the alert box
   * @param title the title for the alert box
   * @param buttonText the text on the button
   */
  
  public void ShowMessageDialog(String message, String title, String buttonText) {
    oneButtonAlert(message, title, buttonText);
  }

  private void oneButtonAlert(String message, String title, String buttonText) {
    Log.i(LOG_TAG, "One button alert " + message);
    AlertDialog alertDialog;
    if (isaService) {
    	alertDialog = new AlertDialog.Builder(service).create();
    } else {
    	alertDialog = new AlertDialog.Builder(activity).create();
    }
    alertDialog.setTitle(title);
    // prevents the user from escaping the dialog by hitting the Back button
    alertDialog.setCancelable(false);
    alertDialog.setMessage(message);
    alertDialog.setButton(buttonText, new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
      }});
    alertDialog.show();
  }


  /**
   * Display an alert with two buttons.   Raises the AfterChoosing event when the
   * choice has been made.
   *
   * @param message the text in the alert box
   * @param title the title for the alert box
   * @param button1Text the text on the left-hand button
   * @param button2Text the text on the right-hand button
   */
  
  public void ShowChooseDialog(String message, String title, String button1Text,
      String button2Text) {
    twoButtonAlert(message, title, button1Text, button2Text);
  }

  private void twoButtonAlert(String message,  String title,
       final String button1Text,  final String button2Text) {
    Log.i(LOG_TAG, "ShowChooseDialog: " + message);
    AlertDialog alertDialog;
    if (isaService) {
    	alertDialog = new AlertDialog.Builder(service).create();
    } else {
    	alertDialog = new AlertDialog.Builder(activity).create();
    }
    alertDialog.setTitle(title);
    // prevents the user from escaping the dialog by hitting the Back button
    alertDialog.setCancelable(false);
    alertDialog.setMessage(message);
    alertDialog.setButton(button1Text,
        new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        AfterChoosing(button1Text);
      }
    });
    // TODO(user): The android documentation says that setButton2 is deprecated and that one
    // should use setButton(AlertDialog.BUTTON_NEGATIVE, ...) instead.  When I use that, everything
    // compiles, but the application crashes immediately, in VFY.  Should we be using new a newer
    // version of the installer?
    alertDialog.setButton2(button2Text,
        new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        AfterChoosing(button2Text);
      }
    });
    alertDialog.show();
  }


  /**
   * Event after the user has made a selection for ShowChooseDialog.
   * @param choice is the text on the button the user pressed
   */
  
  public void AfterChoosing(String choice) {
    EventDispatcher.dispatchEvent(this, "AfterChoosing", choice);
  }

  
  public void ShowTextDialog(String message, String title) {
    textInputAlert(message, title);
  }

  /**
   * Display an alert with a text entry.   Raises the AfterTextInput event when the
   * text has been entered and the user presses "OK".
   *
   * @param message the text in the alert box
   * @param title the title for the alert box
   */
  private void textInputAlert(String message, String title) {
    Log.i(LOG_TAG, "Text input alert: " + message);
    AlertDialog alertDialog;
    if (isaService) {
    	alertDialog = new AlertDialog.Builder(service).create();
    } else {
    	alertDialog = new AlertDialog.Builder(activity).create();
    }
    alertDialog.setTitle(title);
    alertDialog.setMessage(message);
    // Set an EditText view to get user input
    final EditText input;
    if (isaService) {
    	input = new EditText(service);
    } else {
    	input = new EditText(activity);
    }
    alertDialog.setView(input);
    // prevents the user from escaping the dialog by hitting the Back button
    alertDialog.setCancelable(false);
    alertDialog.setButton("OK",
        new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        AfterTextInput(input.getText().toString());
      }
    });
    alertDialog.show();
  }

  /**
   * Event raised after the user has responded to ShowTextDialog.
   * @param response is the text that was entered
   */
  
  public void AfterTextInput(String response) {
    EventDispatcher.dispatchEvent(this, "AfterTextInput", response);
  }

  /**
   * Display a temporary notification
   *
   * @param notice the text of the notification
   */
  
  public void ShowAlert(final String notice) {
    handler.post(new Runnable() {
      public void run() {
    	  if (isaService) {
    		  Toast.makeText(service, notice, Toast.LENGTH_LONG).show();
    	  } else {
    		  Toast.makeText(activity, notice, Toast.LENGTH_LONG).show();
    	  }
      }
    });
  }

  /**
   * Log an error message.
   *
   * @param message the error message
   */
  
  public void LogError(String message) {
    Log.e(LOG_TAG, message);
  }

  /**
   * Log a warning message.
   *
   * @param message the warning message
   */
  
  public void LogWarning(String message) {
    Log.w(LOG_TAG, message);
  }

  /**
   * Log an information message.
   *
   * @param message the information message
   */
  
  public void LogInfo(String message) {
    Log.i(LOG_TAG, message);
  }
}
