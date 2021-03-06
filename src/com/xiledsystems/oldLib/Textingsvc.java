package com.xiledsystems.AlternateJavaBridgelib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.events.EventDispatcher;

/*
 * This is mostly copied from the JB component Texting. The constructor has been modified to be inserted into
 * a service. 
 * 
 * Ryan Bis - www.xiledsystems.com
 * 
 */

@SuppressWarnings("deprecation")
public class Textingsvc extends AndroidServiceComponent implements Component, OnStartCommandListener, OnDestroyListener {

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
      // Create the SmsReceiver variable so we can kill it later
      private SmsReceiver receiver = new SmsReceiver();

      // Provides an event for SMS reception

      // Indicates whether the SMS receiver is running or not
      private boolean receivingEnabled;
      private SmsManager smsManager;

      // The phone number to send the text message to.
      private String phoneNumber;
      // The message to send
      private String message;


      /**
       * Creates a new TextMessage component.
       *
       * @param container  ignored (because this is a non-visible component)
       */
      public Textingsvc(SvcComponentContainer container) {
        super(container.$formService());
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        container.$context().registerReceiver(receiver, intentFilter);
        Log.d("Simple", "Texting constructor");
        smsManager = SmsManager.getDefault();
        PhoneNumber("");
        receivingEnabled = true;
        formService.registerForOnStartCommand(this);
        formService.registerForOnDestroy(this);
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
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
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
        
        // This hopefully kills the smsreceiver on destroy
        
        formService.unregisterReceiver(receiver);

        // Add a little null action for good measure
        receiver = null;
        
    }

    @Override
    public void onStartCommand() {
        
        
    }
}
    


