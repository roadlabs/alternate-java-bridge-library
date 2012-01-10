package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

	public final static int AUTO_CANCEL = Notification.FLAG_AUTO_CANCEL;
	public final static int ONGOING = Notification.FLAG_ONGOING_EVENT;
	public final static int INSISTENT = Notification.FLAG_INSISTENT;
	private static final String LOG_TAG = "Notifier";  
	private static final int NOTIFIER_ID = 66;
	private final Handler handler;
	private boolean isaService = false;
	private Class<?> classToSpawn;
	private Notification notification;
  	private int notificationFlag = -1;
  

  /**
   * Creates a new Notifier component.
   *
   * @param container the enclosing component
   */
  public Notifier (ComponentContainer container) {
    super(container.$form());
    
    handler = new Handler();
    
    isaService = false;
    classToSpawn = form.getClass();
  }
  
  public Notifier (SvcComponentContainer container) {
	  super(container.$formService());
	  
	  handler = new Handler();
	  
	  isaService = true;
	  classToSpawn = formservice.getApplication().getClass();
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
    	alertDialog = new AlertDialog.Builder(formservice).create();
    } else {
    	alertDialog = new AlertDialog.Builder(form.$context()).create();
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
    	alertDialog = new AlertDialog.Builder(formservice).create();
    } else {
    	alertDialog = new AlertDialog.Builder(form.$context()).create();
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
    	alertDialog = new AlertDialog.Builder(formservice).create();
    } else {
    	alertDialog = new AlertDialog.Builder(form.$context()).create();
    }
    alertDialog.setTitle(title);
    alertDialog.setMessage(message);
    // Set an EditText view to get user input
    final EditText input;
    if (isaService) {
    	input = new EditText(formservice);
    } else {
    	input = new EditText(form.$context());
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
    		  Toast.makeText(formservice, notice, Toast.LENGTH_LONG).show();
    	  } else {
    		  Toast.makeText(form.$context(), notice, Toast.LENGTH_LONG).show();
    	  }
      }
    });
  }
  
  public void ShowStatusBarNotification(int iconResourceId, String title, String tickerText, String message) {
	  
	  String ns = Context.NOTIFICATION_SERVICE;
	  NotificationManager manager;
	  if (isaService) {
		  manager = (NotificationManager) formservice.getSystemService(ns);
	  } else {
		  manager = (NotificationManager) form.getSystemService(ns);
	  }
	  long when = System.currentTimeMillis();
	  notification = new Notification(iconResourceId, tickerText, when);
	  if (notificationFlag != -1) {
		  notification.flags |= notificationFlag;
	  }
	  Intent notificationIntent;
	  PendingIntent contentIntent;
	  if (isaService) {
		  notificationIntent = new Intent(formservice, classToSpawn);
		  contentIntent = PendingIntent.getActivity(formservice, 0, notificationIntent, 0);
		  notification.setLatestEventInfo(formservice, title, message, contentIntent);
	  } else {
		  notificationIntent = new Intent(form, classToSpawn);
		  contentIntent = PendingIntent.getActivity(form, 0, notificationIntent, 0);
		  notification.setLatestEventInfo(form, title, message, contentIntent);		  
	  }
	  manager.notify(NOTIFIER_ID, notification);
	  
  }
  
  public void setClassToOpen(Class<?> classtoopen) {
	  classToSpawn = classtoopen;
  }
  
  public void setNotificationFlag(int flag) {
	  notificationFlag = flag;
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
