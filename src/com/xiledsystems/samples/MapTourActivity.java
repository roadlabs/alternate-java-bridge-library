package com.examples.MapTour;

import java.util.ArrayList;

import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.components.HandlesEventDispatching;
import com.google.devtools.simple.runtime.components.android.ActivityStarter;
import com.google.devtools.simple.runtime.components.android.Form;
import com.google.devtools.simple.runtime.components.android.Image;
import com.google.devtools.simple.runtime.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.ListPicker2;


public class MapTourActivity extends Form implements HandlesEventDispatching {
    
	// I'm using the custom listpicker, and listpickeractivities from the Alternate Java Bridge Library I put out.
	// This way, we don't need the kawa library.
	private ListPicker2 listpicker;
	private ActivityStarter activityStarter;
	private String destinations[];
	private ArrayList<String> arraylist;
	
	void $define() {
		
		Image image1 = new Image(this);
		image1.Picture("parismap.png");
		listpicker = new ListPicker2(this);
		listpicker.Text("Choose Destination");

		// This could have been done another way, but for this example, we will do it the app inventor way. Plus,
		// this is just easier. Here we set the action, activitypackage, and activityclass. We set the Datauri
		// after choosing the item from the listpicker.
		activityStarter = new ActivityStarter(this);
		activityStarter.Action("android.intent.action.VIEW");
		activityStarter.ActivityPackage("com.google.android.apps.maps");
		activityStarter.ActivityClass("com.google.android.maps.MapsActivity");
		
		//Again, I've stored things in an XML file to make updating the elements in the listpicker easier.
		// Here we are simply loading the values that are stored in the destinations.xml file
		destinations = getResources().getStringArray(R.array.destinations);
		
		// Now we have to load the string array into an arraylist, which is needed to populate the listpicker elements
		arraylist = new ArrayList<String>();
		for (int i = 0; i < destinations.length; i++) {
			arraylist.add(destinations[i]);
		}
		listpicker.Elements(arraylist);
		
		// We need two seperate registers here for the listpicker. One for the click, and the other for once the user chooses
		// a selection from the listpicker
		EventDispatcher.registerEventForDelegation(this, "Listpickerclickevent", "Click");
		EventDispatcher.registerEventForDelegation(this, "Listpickerafterevent", "AfterPicking");
		
	}
	
	@Override
	public boolean dispatchEvent(Component component, String id, String eventName, Object[] args) {
		
		if (component.equals(listpicker) && eventName.equals("Click")) {
			listpickerWasClicked();
			return true;
		}
		
		if (component.equals(listpicker) && eventName.equals("AfterPicking")) {
			listpickerAfterClicked();
			return true;
		}
		return false;
		
	}

	private void listpickerAfterClicked() {
		
		//This is just like in AI, only no need for silly "make text" blocks. 
		activityStarter.DataUri("geo:0,0?q="+listpicker.selection());
		activityStarter.StartActivity();
		
	}

	private void listpickerWasClicked() {
		
		listpicker.Open();
		
	}
}