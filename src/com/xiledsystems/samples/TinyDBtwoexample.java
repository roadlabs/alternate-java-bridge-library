package com.xiledsystems.AlternateJavaBridgelib;

import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.components.HandlesEventDispatching;
import com.google.devtools.simple.runtime.components.android.Button;
import com.google.devtools.simple.runtime.components.android.Form;
import com.google.devtools.simple.runtime.components.android.Label;
import com.google.devtools.simple.runtime.events.EventDispatcher;

public class TinyDBtwoexample extends Form implements HandlesEventDispatching {
    
    private TinyDB2 db;
    // This is an array of Objects called test. This is where we will temporarily store our test values
    private Object test[];
    private Label label;
    private Button store;
    private Button retrieve;
    private Label recalleddata;
    private final String STORE_THIS = "Value to be stored: ";    // This is just a variable that never changes
                                                                // In this case we use it to set the label text easier
    private final String RECALLED = "Value recalled: ";
    private int valuenum=0;                                        // This is to track which value we're on
    
    void $define() {
        
        //Initialize our database
        db = new TinyDB2(this);    
        // Initialize an object array for our values, and set them here. These don't have to be set here, you can have
        // a textbox where a user inputs info for example.
        test = new Object[4];
        test[0] = "string of text";
        test[1] = 42;
        test[2] = 43.23524;
        test[3] = true;
        label = new Label(this);
        label.FontBold(true);
        // Remember those static Strings from above? This and everywhere we define the label text, it becomes useful.
        label.Text(STORE_THIS+test[0]);
        store = new Button(this);
        store.Text("Store Next Value");
        retrieve = new Button(this);
        retrieve.Enabled(false);
        retrieve.Text("Retrieve next value");
        recalleddata = new Label(this);
        recalleddata.Text(RECALLED+"Nothing yet.");
        
        EventDispatcher.registerEventForDelegation(this, "clicks", "Click");
        
    }
    
    @Override
    public boolean dispatchEvent(Component component, String id, String eventName, Object[] args) {
        
        if (component.equals(store) && eventName.equals("Click")) {
            storeWasClicked();
            return true;
        }
        if (component.equals(retrieve) && eventName.equals("Click")) {
            retrieveWasClicked();
            return true;
        }
        return false;
        
    }

    // Here we store our values into the db. It works the same way as the App Inventor TinyDB.
    // I'm just changing the tag to tag + the valuenumber. This way we can have 100 values to store, and the
    // code wouldn't get any bigger. We would just have to change the if statement.
    private void storeWasClicked() {
        //This changes the label to reflect what is going to be saved the next time you hit the store button. If the valuenum
        // is at 3, then don't change the display. Remember, arrays start at 0, so even though we have 4 values assigned, the
        // last index number is 3.
        if (valuenum<3) {
        label.Text(STORE_THIS + test[valuenum+1]);
        }
        db.StoreValue("tag"+valuenum, test[valuenum]);
        valuenum++;
        
        // This just disables the store button, so that you can go through retrieving the data you need.
        if (valuenum == 4) {
            store.Enabled(false);
            retrieve.Enabled(true);
            valuenum = 3;
        }
        
    }

    private void retrieveWasClicked() {
        
        // Right here we're just loading the objects into the label. Depending on what you'r doing with the values, you may
        // have to convert the object first. Since we're just updating a label, we don't need to convert in this example.
        // For instance, to convert to a String, you would put this:
        // String str = (String) db.GetValue("tag"+valuenum)); -- This typecasts the object to a String. 
        
        recalleddata.Text(RECALLED+db.GetValue("tag"+valuenum));
        valuenum--;
        if (valuenum == -1) {
            retrieve.Enabled(false);
            store.Enabled(true);
            valuenum = 0;
            label.Text(STORE_THIS+test[0]);
        }
        
    }

}

