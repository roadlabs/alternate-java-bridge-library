package com.examples.ListPickerExample2;

import java.util.ArrayList;

import com.google.devtools.simple.runtime.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.ListPicker2;
import com.google.devtools.simple.runtime.components.HandlesEventDispatching;
import com.google.devtools.simple.runtime.components.android.Form;
import com.google.devtools.simple.runtime.events.EventDispatcher;

/*
 *  In order to use the listpicker without adding the Kawa library, I rewrote the listpicker and listpickeractivity
 *  classes. The listpicker simply was changed to use an ArrayList<String> instead of Yaillist, and the listpickeractivity
 *  needed to be changed simply to point to the new listpicker instead of the old. 
 *  This was probably the long way to go, but it works.
 *  You're can look at the original source code for the ListPicker, and compare to ListPicker2 here to see what changed.
 *  You must remember to copy the AlternateJavaBridgelib.jar to the libs directory, and add it to the build path, in the
 *  same manner that you do for the java bridge jar file.
 */
public class ListPickerExample2Activity extends Form implements HandlesEventDispatching {

	private ListPicker2 listpicker;
	private ArrayList<String> stringlist;		// This replaces the Yaillist (which requires another jar file)
	
	private String str[];						// This string array gets filled with the elements you want
												// in the listpicker from an XML file stored in the values folder
	
	void $define() {
		
		//Create new listpicker from our customized version
		listpicker = new ListPicker2(this);
		
		// Change some settings on the listpicker
		listpicker.FontBold(true);
		listpicker.FontSize(16.0f);
		listpicker.Text("Listpicker Button");
		
		// Initialize our arraylist
		stringlist = new ArrayList<String>();
		
		// This loads the list of elements from the values folder
		str = getResources().getStringArray(R.array.Elements);
		
		// The string array then gets put into the arraylist
		for (int i = 0; i < str.length ; i++) {
			stringlist.add(str[i]);
		}
		
		// This populates the listpicker with the elements from the XML file
		listpicker.Elements(stringlist);
		
		// Register the click event for the listpicker		
		EventDispatcher.registerEventForDelegation(this, "listpickerclick", "Click");
	}
	
	@Override
	public boolean dispatchEvent(Component component, String id, String eventName, Object[] args) {
		if (component.equals(listpicker) && eventName.equals("Click")) {
			openPicker();
			return true;
		}
		return false;
	}

	// This opens up the listpicker
	private void openPicker() {
		
		listpicker.Open();
		
	}
	
}