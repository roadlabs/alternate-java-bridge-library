# FormService #

A FormService is similar to a Form. The difference is that a FormService can't do anything with the UI. Services run in the background, so they aren't tied to a Form's lifecycle.

If your app needs to process anything whether the user is in your app or not, then you will need a FormService.


## Basic Use ##

For basic use of a service, it works very closely to how you setup a Form.

```
package com.xiledsystems.examples;

import android.content.Intent;
import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.FormService;

public class NewFormService extends FormService implements HandlesEventDispatching {

	// Declare global variables here 

    
    
    // This method is run when this FormService (service) is first loaded. Whatever is in this method
    // runs once upon loading. If the service receives a start command, and it's already running, this
    // method is not run. This is where you want to instantiate your global components.     
    void $define() {
 		    
 		 // This registers the onStartCommand event. This is run whenever the service receives
 		 // a call to start. 
         EventDispatcher.registerEventForDelegation(this, "startcommand", "onStartCommand");
    }
    
    @Override
    public boolean dispatchEvent(Component component, String id, String eventName, Object[] args) {
        	
        if (component.equals(this) && eventName.equals("onStartCommand")) {
        	onStart(args[0]);
        	return true;
        }        
    	return false;
   	 }
   	 
   	 // This allows you to do stuff when the service receives a new intent to start
   	 // The intent is passed into this method, so you can pass data to the service
   	 // from a Form.
   	 private void onStart(Object intent1) {
   	 	Intent intent = (Intent) intent1;
   	 	
   	 }   	 
   	
}
```

Very similar, but you can see some differences already. Like a Form, when the FormService is launched, it runs the $define method. However, services don't stack like Forms do (you can't have the same FormService running more than once, unlike Forms - you can launch the same Form multiple times, and they will all be running in memory). Instead, what a FormService does, is to throw the onStartCommand event. This method gets thrown when the service is started by the startService method. This gives you a oneway communication between a Form and FormService. When you want to send a data to the service from a Form, add the data to the intent, then start the service again. Then, in the FormService's onStartCommand, you can retrieve the data from the intent.

Form: (Send "This is a test" to the service)
```
Intent intent = new Intent(this, FormServiceExample.class);
intent.putExtra("Test", "This is a test");
startService(intent);
```

FormService: (In the onStart method, we check the intent to see if it
contains the "Test" value. If it does, then get the string provided.)
```
private void onStart(Object intent1) {

      Intent intent = (Intent) intent1;
      if (intent.hasExtra("Test")) {
          String message = intent.getStringExtra("Test");
          // Do something to respond to the communication
      }
}
```
You can use any non-visible component in a service, and that works the same way it does in a Form (declare your global, then instantiate in $define, then register the events you wish to capture, then capture them in dispatchEvent). These are things like Sound, Player, Web, Clock, ThreadTimer, etc.
You can also open other Form/FormServices the same way you would in a Form.

## Binding a FormService ##

There may be times where you need to communicate with your FormService a lot, and need two way communication. In this case, you will want your activity to "bind" to the service. This will allow you to send a message to the currently bound activity. The other thing it allows you to do, is to run methods within your FormService.


Binding takes a little more to get going, but not much. The issue is that when you request to bind to a service, it will bind, but it does it asynchronously, so you have to check to see if it's bound. You'll need a Clock component to do the checking. Let's say you have the MyForm class which wants to bind to MyFormService.
MyForm:
```
package com.example.test;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.Clock;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.Form;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.events.Events;

public class MyForm extends Form implements HandlesEventDispatching {

	// Declare global variables here 
	
	private MyFormService service;
	private Clock clock;

	protected void $define() {
 		// This sets the UI designed in the graphical editor to our Form's UI.
 		setContentView(R.layout.myform);
 		
 		clock = new Clock(this);
 		clock.TimerEnabled(false);
 		clock.TimerInterval(25);
    
        // This method captures the initialization event of this Form, so we can do stuff when the
        // screen first gets initialized.
        EventDispatcher.registerEventForDelegation(this, "ScreenInitialization", "Initialize");
        EventDispatcher.registerEventForDelegation(this, "clockfiredevent", Events.TIMER);
    
    }
    
	@Override
    public boolean dispatchEvent(Component component, String id, String eventName, Object[] args) {
    
    	if (component.equals(this) && eventName.equals("Initialize")) {
   			screenInitialized();
    		return true;
    	}
    	if (component.equals(clock) && eventName.equals(Events.TIMER)) {
   			clockFired();
    		return true;
    	}
    	return false;
   	}
   	
	private void clockFired() {
		// boundService is what you need to check. If this is not
		// null, then the service has been bound. You can then cast
		// boundService to MyFormService so you can access methods
		// in MyFormService from this Form. We also shut off the clock
		// once the service has been bound.
		if (boundService != null) {
			service = (MyFormService) boundService;
			clock.TimerEnabled(false);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		// Start the process to bind to the service
		BindToService(MyFormService.class);
		// Start a clock to check to see when the service has been
		// bound.
		clock.TimerEnabled(true);
	}
					
	private void screenInitialized() {
   	 	// This method is run when the screen is initialized, and reports a valid width/height. 
   	 
   	}
   	
	@Override
	public void onStop() {
		super.onStop();
		// This will unbind the service when the Form loses focus. Services are
		// automatically unbound when a Form is destroyed, but if you are going
		// to have multiple Forms binding to a service, it's best to unbind when
		// a Form loses focus, so there's only ever one Form bound to a service
		// at any time.
		UnBindService();
	}
	
	
}
```