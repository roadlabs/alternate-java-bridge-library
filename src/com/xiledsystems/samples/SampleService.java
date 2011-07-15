package com.examples.SampleServiceActivity;

import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.components.HandlesEventDispatching;
import com.google.devtools.simple.runtime.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.Clocksvc;
import com.xiledsystems.AlternateJavaBridgelib.FormService;
import com.xiledsystems.AlternateJavaBridgelib.Notifiersvc;
import com.xiledsystems.AlternateJavaBridgelib.TinyDBsvc;

/*
 * Here is how we declare our Service. I tried to make things as similar to how you write an
 * activity with the Java Bridge (JB). You have to extend FormService for your new service.
 * 
 * Make sure to have your new service listed in your AndroidManifest.xml ex:
 * <service android:name="SampleService"></service>
 * 
 * Ryan Bis - www.xiledsystems.com
 */
public class SampleService extends FormService implements HandlesEventDispatching {
	
	// You can't just add the standard JB components in a service. I've rewritten a few components
	// to work in a service. We'll declare them here.
	private TinyDBsvc db;
	private Clocksvc timer;
	private int counterVar=0;
	private Notifiersvc note;
	
	// Again, just like in Form, this $define() section is similar to a main() method. One thing to note. This is only run
	// when the service is first created. If the service is in sticky mode, and you send another intent to start the
	// service, it runs OnStartCommand instead.
	
	void $define() {
		// And we initialize our declarations just like usual
		db = new TinyDBsvc(this);
		timer = new Clocksvc(this);
		timer.TimerInterval(3000);
		timer.TimerEnabled(true);
		note = new Notifiersvc(this);
		loadCounterVal();
		// This value specifies that the service should start in STICKY mode. This means that even if the activity that called
		// this service is terminated, this service continues to run. 
		this.setStickyVal(START_STICKY);
		
		// Remember this line?
		EventDispatcher.registerEventForDelegation(this, "ServiceTimer", "Timer");
		
	}
	
	private void loadCounterVal() {
		// Here we try to load the counterVar. If we get a NumberFormatException, then that means it couldn't load the file
		// because it doesnt exist, and it can't convert null to a boolean. We simply catch this exception, and set the
		// boolean to false.
		try {
			counterVar= Integer.parseInt(db.GetValue("CounterVariable").toString());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			counterVar = 0;			
		}
						
	}

	@Override
	public boolean dispatchEvent(Component component, String id, String eventName, Object[] args) {
		
		if (component.equals(timer) && eventName.equals("Timer")) {
			timerHasFired();
			return true;
		}
		return false;
		
	}

	private void timerHasFired() {
		
		counterVar++;
		db.StoreValue("CounterVariable", counterVar);
		note.ShowAlert("Counter Value: "+counterVar);
				
	}

}
