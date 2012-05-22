package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * A component capable of sending and receiving text messages via SMS.

 *
 */

public class Texting extends AndroidNonvisibleComponent implements Component, OnDestroyListener, Deleteable, OnDestroySvcListener {

	private final static String SENT_NMBR = "Texting.SentNumber";
	private final static String SENT_MSG = "Texting.SentMessage";
	private final static String TXT_INTENT = "com.xiledsystems.AlternateJavaBridgelib.TextingIntent";	
	private MsgReceiver msgReceiver = new MsgReceiver();
	
	
  /**
   * Handles the SMS reception
   */
  class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      SmsMessage[] messages = getMessagesFromIntent(intent);
      SmsMessage message = messages[0];
      if (message != null) {
        String from = message.getOriginatingAddress();
        String messageText = message.getMessageBody();
        MessageReceived(from, messageText);
      } else {
        Log.i("Simple", "Sms message suppposedly received but with no actual content.");
      }
    }
  }

  // Provides an event for SMS reception

  // Indicates whether the SMS receiver is running or not
  private boolean receivingEnabled;
  private SmsManager smsManager;

  // The phone number to send the text message to.
  private String phoneNumber;
  // The message to send
  private String message;
  private SmsReceiver thisreceiver;


  /**
   * Creates a new Texting component.
   *
   * @param container  Use "this"
   */
  public Texting(ComponentContainer container) {
    super(container);    
    IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
    thisreceiver = new SmsReceiver();
    container.$context().registerReceiver(thisreceiver, intentFilter);    
    container.$context().registerReceiver(msgReceiver, new IntentFilter(TXT_INTENT));    
    Log.d("Simple", "Texting constructor");
    smsManager = SmsManager.getDefault();
    PhoneNumber("");
    receivingEnabled = true;
  }
  
  /**
   * Creates a new Texting component in a formservice.
   *
   * @param container  Use "this"
   */
  public Texting(SvcComponentContainer container) {
	  super(container);
	  IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
	  thisreceiver = new SmsReceiver();
	    container.$context().registerReceiver(thisreceiver, intentFilter);
	    container.$context().registerReceiver(msgReceiver, new IntentFilter(TXT_INTENT));
	    Log.d("Simple", "Texting constructor");
	    smsManager = SmsManager.getDefault();
	    PhoneNumber("");
	    receivingEnabled = true;
  }

  /**
   * Sets the phone number to send the text message to when the SendMessage function is called.
   *
   * @param phoneNumber a phone number to call
   */
  
  public void PhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }
  
  
  /**
   * Get the phone number that the message will be sent to when the SendMessage function is called.
   */
  
  public String PhoneNumber() {
    return phoneNumber;
  }

  /**
   * Sets the text message to send when the SendMessage function is called.
   *
   * @param message the message to send when the SendMessage function is called.
   */
  
  public void Message(String message) {
    this.message = message;
  }

  /**
   * Get the message that will be sent when the SendMessage function is called.
   */
  
  public String Message() {
    return message;
  }

  /**
   * Send a text message
   */
  
  public void SendMessage() {
	  
	  Intent msgIntent = new Intent(TXT_INTENT);
	  msgIntent.putExtra(SENT_NMBR, phoneNumber);
	  msgIntent.putExtra(SENT_MSG, message);	  
	  PendingIntent pIntent = PendingIntent.getBroadcast(container.$context(), 0, msgIntent, PendingIntent.FLAG_NO_CREATE);	  
	  smsManager.sendTextMessage(phoneNumber, null, message, pIntent, null);	  
	  
  }

  /**
   * Event that's raised when a new text message is received by the phone.
   * @param number the phone number that the text message was sent from.
   * @param messageText the text of the message.
   */
  
  public void MessageReceived(String number, String messageText) {
    // TODO(user): maybe we should unregister and re-register the SmsReceiver based on the
    // receivingEnabled setting rather than just checking here.
    if (receivingEnabled) {
      Log.d("Simple", "MessageReceived");
      EventDispatcher.dispatchEvent(this, "MessageReceived", number, messageText);
    }
  }


  /**
   * Gets whether you want the {@link #MessageReceived) event to get run when a new text message is
   * received.
   *
   * @return 'true' or 'false' depending on whether you want the {@link #MessageReceived) event to
   *          get run when a new text message is received.
   */
  
  public boolean ReceivingEnabled() {
    return receivingEnabled;
  }

  /**
   * Sets whether you want the {@link #MessageReceived) event to get run when a new text message is
   * received.
   *
   * @param enabled  Set to 'true' or 'false' depending on whether you want the
   *                 {@link #MessageReceived) event to get run when a new text message is received.
   */
  
  public void ReceivingEnabled(boolean enabled) {
    this.receivingEnabled = enabled;
  }

  /**
   * Parse the messages out of the extra fields from the "android.permission.RECEIVE_SMS" broadcast
   * intent.
   *
   * Note: This code was copied from the Android android.provider.Telephony.Sms.Intents class.
   *
   * @param intent the intent to read from
   * @return an array of SmsMessages for the PDUs
   */
  public static SmsMessage[] getMessagesFromIntent(
          Intent intent) {
      Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
      byte[][] pduObjs = new byte[messages.length][];

      for (int i = 0; i < messages.length; i++) {
          pduObjs[i] = (byte[]) messages[i];
      }
      byte[][] pdus = new byte[pduObjs.length][];
      int pduCount = pdus.length;
      SmsMessage[] msgs = new SmsMessage[pduCount];
      for (int i = 0; i < pduCount; i++) {
          pdus[i] = pduObjs[i];
          msgs[i] = SmsMessage.createFromPdu(pdus[i]);
      }
      return msgs;
  }

@Override
public void onDestroy() {
	
	if (container == null) {
		sContainer.$context().unregisterReceiver(thisreceiver);
		sContainer.$context().unregisterReceiver(msgReceiver);
	} else {
		container.$context().unregisterReceiver(thisreceiver);
		container.$context().unregisterReceiver(msgReceiver);
	}
	msgReceiver = null;
	thisreceiver = null;
	
}

@Override
public void onDelete() {	
	if (container == null) {
		sContainer.$context().unregisterReceiver(thisreceiver);
		sContainer.$context().unregisterReceiver(msgReceiver);
	} else {
		container.$context().unregisterReceiver(thisreceiver);
		container.$context().unregisterReceiver(msgReceiver);
	}
	msgReceiver = null;
	thisreceiver = null;	
}

	private void msgReceivedOk(Intent intent) {
		if (intent.hasExtra(SENT_NMBR) && intent.hasExtra(SENT_MSG)) {
			String n = intent.getStringExtra(SENT_NMBR);
			String m = intent.getStringExtra(SENT_MSG);
			EventDispatcher.dispatchEvent(this, "MessageSent", n, m);			
		}
	}

	private void msgNotSent(Intent intent, String errorMsg) {
		String n = "";
		String m = "";
		if (intent.hasExtra(SENT_NMBR) && intent.hasExtra(SENT_MSG)) {
			n = intent.getStringExtra(SENT_NMBR);
			m = intent.getStringExtra(SENT_MSG);
		}
		EventDispatcher.dispatchEvent(this, "MessageFailed", n, m, errorMsg);
	}

	private class MsgReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				msgReceivedOk(intent);
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				msgNotSent(intent, "Generic Error.");
				break;
			case SmsManager.RESULT_ERROR_NO_SERVICE:
				msgNotSent(intent, "Error: No Service.");
				break;
			case SmsManager.RESULT_ERROR_NULL_PDU:
				msgNotSent(intent, "Error: Null PDU.");
				break;
			case SmsManager.RESULT_ERROR_RADIO_OFF:
				msgNotSent(intent, "Error: Radio off.");
				break;			
			}
		}		
	}	

}
