package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class FormServiceReceiver extends BroadcastReceiver {

	public static final String FORMSVC_MSG = "Form_Service_Message";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.hasExtra(FORMSVC_MSG)) {
			String data = intent.getStringExtra(FORMSVC_MSG);
			EventDispatcher.dispatchEvent((Form)context, "FormServiceMessage", data);
		}
	}

}
