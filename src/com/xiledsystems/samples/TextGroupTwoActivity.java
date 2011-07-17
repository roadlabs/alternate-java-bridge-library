package com.examples.TextGroupTwo;

import java.util.ArrayList;

import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.components.HandlesEventDispatching;
import com.google.devtools.simple.runtime.components.android.Button;
import com.google.devtools.simple.runtime.components.android.Form;
import com.google.devtools.simple.runtime.components.android.HorizontalArrangement;
import com.google.devtools.simple.runtime.components.android.Label;
import com.google.devtools.simple.runtime.components.android.PhoneNumberPicker;
import com.google.devtools.simple.runtime.components.android.TextBox;
import com.google.devtools.simple.runtime.components.android.Texting;
import com.google.devtools.simple.runtime.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.ListPicker2;
import com.xiledsystems.AlternateJavaBridgelib.TinyDB2;
import com.xiledsystems.AlternateJavaBridgelib.TinyListDB;


public class TextGroupTwoActivity extends Form implements HandlesEventDispatching {
    
	private TextBox messageText;
	private Button textGroupButton;
	private Label statusLabel;
	private Label groupHeadingLabel;
	private Label membersLabel;
	private PhoneNumberPicker phonepicker;

	// Here we are going to use my modified version of listpicker, so that we don't need the Kawa library.
	private ListPicker2 removeListPicker;
	private Texting texter;
	private ArrayList<String> phonenumbers;
	
	// Declare the modified version of TinyDB
	private TinyDB2 db;
	private final static String PREFIX = "last message sent: ";
	private final static String TAG = "textGroup";
	
	void $define() {
		
		// Here we are going to use my TinyListDB version of TinyDB. If you want to save lists, this is how
		// you will have to do it for now. This version also supports storing of normal values, such as ints, doubles,
		// Strings, etc. Storing is the same. The only difference in using it, is that you have to use GetList rather
		// than GetValue when you are retrieving a list from the DB. If you are retrieving ints, doubles, strings, then use
		// GetValue like you usually would. The difference behind the scenes is that it stores
		// data to a binary file, rather than using SharedPreferences.
		db = new TinyDB2(this);
		messageText = new TextBox(this);
		messageText.Hint("enter a message");
		textGroupButton = new Button(this);
		textGroupButton.Text("Text Group");
		statusLabel = new Label(this);
		statusLabel.Text("status");
		phonenumbers = new ArrayList<String>();
		groupHeadingLabel = new Label(this);
		groupHeadingLabel.Text("--- Group");
		groupHeadingLabel.FontBold(true);
		membersLabel = new Label(this);
		
		HorizontalArrangement ha1 = new HorizontalArrangement(this);
		phonepicker = new PhoneNumberPicker(ha1);
		phonepicker.Text("Add Member");
		removeListPicker = new ListPicker2(ha1);
		removeListPicker.Text("Remove Member");
		
				
		// Initialize our texting component. Don't forget to add uses permissions to your androidmanifest file
		// for SEND_SMS, otherwise no texts will be sent.
		texter = new Texting(this);
		checkForStoredGroup();		
		EventDispatcher.registerEventForDelegation(this, "Buttonclicks", "Click");
		EventDispatcher.registerEventForDelegation(this, "pickerevents", "AfterPicking");
		EventDispatcher.registerEventForDelegation(this, "pickerevents", "BeforePicking");
		
		
	}
	
	private void checkForStoredGroup() {
				
		// These two lines are unnecessary. I did this to make it easier to read. There's no real good reason
		// to create a variable here at all, other than to make it easier to see what's going on. See if you can
		// figure out how to do it without using the test arraylist.
		ArrayList<String> test = new ArrayList<String>();
		test = (ArrayList<String>) db.GetValue(TAG);
		
		if (test.size()>0) {
			phonenumbers = test;
			displayMembers();
		}
				
	}
	

	@Override
	public boolean dispatchEvent(Component component, String id, String eventName, Object[] args) {
		
		if (component.equals(textGroupButton) && eventName.equals("Click")) {
			
			textGroupWasClicked();
			return true;			
		}
		if (component.equals(phonepicker) && eventName.equals("AfterPicking")) {
			
			phoneAfterPick();
			return true;
		}
		if (component.equals(removeListPicker) && eventName.equals("BeforePicking")) {
			
			removeListBeforePick();
			return true;
		}
		if (component.equals(removeListPicker) && eventName.equals("AfterPicking")) {
			
			removeListAfterPick();
			return true;
		}
		return false;
		
	}

	private void removeListAfterPick() {
		
		// Again, a needless variable. This one would be a bit more unpleasant to read than the last one.
		int position = phonenumbers.indexOf(removeListPicker.selection());
		phonenumbers.remove(position);
		db.StoreValue(TAG, phonenumbers);
		displayMembers();
		
	}

	private void removeListBeforePick() {
		
		removeListPicker.Elements(phonenumbers);
		
	}

	private void phoneAfterPick() {
		
		phonenumbers.add(phonepicker.PhoneNumber());
		db.StoreValue(TAG, phonenumbers);
		displayMembers();
		
	}

	private void displayMembers() {
		
		membersLabel.Text(" ");
		for (String number : phonenumbers) {
			membersLabel.Text(membersLabel.Text()+"\n"+number);
		}
		
	}

	private void textGroupWasClicked() {
		
		texter.Message(messageText.Text());
		
		// This is how to write a for each statement. You first declare what type of variable it will use
		// in this case, it's a String. Then seperate that with a : and specify what array, or list you wish
		// to parse. I think this way is a little easier to comprehend.
		
		for (String var : phonenumbers) {
			texter.PhoneNumber(var);
			texter.SendMessage();
		}
		
		// Here we add that static string to the front, then append it with the message.
		statusLabel.Text(PREFIX+messageText.Text());
		
	}
	
}