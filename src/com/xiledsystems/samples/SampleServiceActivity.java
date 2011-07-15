package com.examples.SampleServiceActivity;

import android.content.Intent;
import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.components.HandlesEventDispatching;
import com.google.devtools.simple.runtime.components.android.Button;
import com.google.devtools.simple.runtime.components.android.Form;
import com.google.devtools.simple.runtime.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.TinyDB2;


public class SampleServiceActivity extends Form implements HandlesEventDispatching {
	
	private Button toggleSrvc;
	private boolean srvcRunning=false;
	private TinyDB2 db;
	
	void $define() {
		// Initialize the TinyListDB to get and store the service's status
		db = new TinyDB2(this);
		loadServiceStatus();
		toggleSrvc = new Button(this);
		if (srvcRunning) {
			toggleSrvc.Text("Stop the counter");
		} else {
			toggleSrvc.Text("Start the counter");
		}
		
		EventDispatcher.registerEventForDelegation(this, "Buttonclick", "Click");
	}
	
	private void loadServiceStatus() {
		
		// This is how we convert to a boolean (TinyDB2 returns an object)
		
		srvcRunning = Boolean.parseBoolean(db.GetValue("srvcRunning").toString());
			
	}

	@Override
	public boolean dispatchEvent(Component component, String id, String eventName, Object[] args) {
		if (component.equals(toggleSrvc) && eventName.equals("Click")) {
			toggleSrvc();
			return true;
		}
		return false;
	}

	private void toggleSrvc() {
		
		// And this is how to call a service ...it's the two lines with Intent intent..., and startService().
		// Here we are toggling the service on and off rather than have two seperate buttons.
		// In this example, we are running the service in STICKY mode (it doesnt stop when this activity stops)
		// So you must make sure to have it stop somewhere. A user shouldn't have to use a Task killer program
		// to stop your service.
		Intent intent = new Intent(this, SampleService.class);
		if (srvcRunning) {
			stopService(intent);
			srvcRunning=false;
			toggleSrvc.Text("Start the counter");
			db.StoreValue("srvcRunning", srvcRunning);
		} else {
		startService(intent);
		srvcRunning=true;
		toggleSrvc.Text("Stop the counter");
		db.StoreValue("srvcRunning", srvcRunning);
		}
	}

}
