package com.xiledsystems.AlternateJavaBridgelib;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.events.EventDispatcher;

/**
 * This is mostly just copied from Java Bridge's Notifier class. I've just changed a few things
 *  such as how the Notifier gets the context, and the Constructor (so it can be put into
 *  a SvcComponentContainer. 
 * 
 * 
 * Ryan Bis - www.xiledsystems.com
 *
 */

public class Notifiersvc extends AndroidServiceComponent implements Component {

	private static final String LOG_TAG = "Notifier";
	private final Service service;
	private final Handler handler;
	
	public Notifiersvc(SvcComponentContainer container) {
		super(container.$formService());
		service = container.$context();
		handler = new Handler();		
	}
	public void ShowMessageDialog(String message, String title, String buttonText) {
	    oneButtonAlert(message, title, buttonText);
	  }

	  private void oneButtonAlert(String message, String title, String buttonText) {
	    Log.i(LOG_TAG, "One button alert " + message);
	    AlertDialog alertDialog = new AlertDialog.Builder(service).create();
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

	    AlertDialog alertDialog = new AlertDialog.Builder(service).create();
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
	    AlertDialog alertDialog = new AlertDialog.Builder(service).create();
	    alertDialog.setTitle(title);
	    alertDialog.setMessage(message);
	    // Set an EditText view to get user input
	    final EditText input = new EditText(service);
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
	        Toast.makeText(service, notice, Toast.LENGTH_LONG).show();
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

