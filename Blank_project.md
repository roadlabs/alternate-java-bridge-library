# Initial empty project #

```
package com.yourdomainname.yourpackagename;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.Form;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

public class Screen1 extends Form implements HandlesEventDispatching {

	
	void $define() {
		
		EventDispatcher.registerEventForDelegation(this, "Eventnamehere", "Initialize");
	}
	
	@Override
	public boolean dispatchEvent(Component component, String id, String eventName, Object[] args) {
		
		if (component.equals(this) && eventName.equals("Initialize")) {
			screenInitialized();
			return true;
		}
		return false;
	}

	private void screenInitialized() {
		// Anything you need done upon load up, once the screen is initialized
		
	}
	
}
```


## Screen1 ##

> This is the name of the screen. It's named Screen1 here to emulate how projects look in App Inventor. When you see "this" within the code above, it refers to the class it's in -- in this case, Screen1.

## Form ##

> We extend Form for our screens. Form is really just an activity with some extra code to handle events mostly. You must extend this class to use this library.

## HandlesEventDispatching ##

> This is the event handling class. We must implement this interface to be able to handle events within our Screen1.

## void $define() {} ##

> This method is what some would say is similar to a Main() method. It is where you should do most of your instantiation. This method is run when Screen1 first opens.

## EventDispatcher ##

> This line is where we declare which events we wish to capture. In this case, we are capturing the "Initialize" event. This is the event that is fired when Screen1 first loads up, and the screen has been initialized.
  * "this" means that Screen1 is registering for an event
  * "Eventnamehere" is not really used much, use it to distinguish between events (you may have multiple different components which throw the same event such as "Click" - Button, ListPicker, etc)
  * "Initialize" the actual event name that is raised, which we want to intercept

## dispatchEvent() method ##

> This is where we intercept events such as "Click" for button clicks, "Touched" for sprites and canvas', and "Initialize" for our Screen1 initialization event. Notice the @Override above this method. This is a method in Form which we are "overriding" so that we can set what we want to happen during these events. This method is called by the components you add to your project. The component sends information through this method that you can intercept to perform your operations. That info is:
  * component - The actual component which has raised the event
  * id - The id is the first string in your event declaration. In this case "Eventnamehere". You rarely will have to do anything with this.
  * eventName - This is the name of the event which has been raised - ex "Click", "Initialize", "CollidedWith"
  * args - This is an object array of optional parameters. Depending on the component, and the event, you will may or may not need to use this. A good example of an event which doesnt send arguments is the "Click" event raised by buttons. However, the "Touched" event also sends back the x and y location of the touch. The x would be the first index of the array, and the y would be the second (`args[0]` is x, `args[1]` is y).
You will have to convert from an object to a double, or integer (see Convert).

### Catching events ###
> The if statement above checks to see what component raised the event, and what event was raised by it. If it equals "this" (Screen1), and the event is "Initialized", run the method screenInitialized(). You then return true after that so the event handler processes the method, and returns from the dispatchEvent method (it doesn't process any more code with that method).

### screenInitialized ###
> Here is where you can run anything upon the startup of Screen1, when the screen itself has been initialized (upon initial startup of an activity, the screen size will be represented by 0,0. The Initialize event is called once the screen size has actually been determined.)

### Note ###
> When using version 1.9 and up, you must also have the following line in the define() method (make sure it's the first line):
```
setContentView(R.layout.main);
```

This sets the UI to be what you designed in the graphical editor in Eclipse. If you want to build your UI in code, you don't add the above line.