package com.xiledsystems.AlternateJavaBridgelib;

import com.google.devtools.simple.runtime.components.android.ComponentContainer;
import com.google.devtools.simple.runtime.components.util.ErrorMessages;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.util.Log;

@SuppressWarnings("deprecation")
public class PhoneNumberPicker2 extends ContactPicker2 {

	 private static final String[] PROJECTION = {
		    Contacts.PeopleColumns.NAME,
		    Contacts.PhonesColumns.NUMBER,
		    Contacts.Phones.PERSON_ID,
		    Contacts.People.PRIMARY_EMAIL_ID,
		  };
		  private static final int NAME_INDEX = 0;
		  private static final int NUMBER_INDEX = 1;
		  private static final int PERSON_INDEX = 2;
		  private static final int EMAIL_INDEX = 3;

		  private String phoneNumber;

		  /**
		   * Create a new ContactPicker component.
		   *
		   * @param container the parent container.
		   */
		  public PhoneNumberPicker2(ComponentContainer container) {
		    super(container, Contacts.Phones.CONTENT_URI);
		  }

		  /**
		   * PhoneNumber property getter method.
		   */
		  
		  public String PhoneNumber() {
		    return ensureNotNull(phoneNumber);
		  }

		  /**
		   * Callback method to get the result returned by the contact picker activity
		   *
		   * @param requestCode a code identifying the request.
		   * @param resultCode a code specifying success or failure of the activity
		   * @param data the returned data, in this case an Intent whose data field
		   *        contains the contact's content provider Uri.
		   */
		  // TODO(user): Rework how the content selection is done to make this overlap
		  // more with Contact Picker.  Note that the two components use different intents, so that
		  // the returned URIs are different (contacts/people vs, contacts/phones)
		  // This really should be fixed by updating the way we handle contacts.   See the comments
		  // on checkUri in ContactPicker.

		  @Override
		  public void resultReturned(int requestCode, int resultCode, Intent data) {
		    if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK) {
		      Log.i("PhoneNumberPicker", "received intent is " + data);
		      Uri phoneUri = data.getData();
		      if (checkContactUri(phoneUri, "//contacts/phones")) {
		        // This test is not good enough.  The lookup code below does not work with
		        // Motorola Blur (Droid Global), even though the URI has the correct form.
		        // Hopefully, moving to the new contact scheme will solve this problem.
		        Cursor cursor = null;
		        try {
		          cursor = activityContext.getContentResolver().query(phoneUri,
		              PROJECTION, null, null, null);
		          if (cursor.moveToFirst()) {
		            contactName = guardCursorGetString(cursor, NAME_INDEX);
		            phoneNumber = guardCursorGetString(cursor, NUMBER_INDEX);
		            int contactId = cursor.getInt(PERSON_INDEX);
		            Uri cUri = ContentUris.withAppendedId(Contacts.People.CONTENT_URI, contactId);
		            contactPictureUri = cUri.toString();
		            String emailId = guardCursorGetString(cursor, EMAIL_INDEX);
		            emailAddress = getEmailAddress(emailId);
		            Log.i("PhoneNumberPicker",
		                "Contact name = " + contactName + ", phone number = " + phoneNumber +
		                ", emailAddress = " + emailAddress + ", contactPhotoUri = " +  contactPictureUri);
		          }
		        } catch (Exception e) {
		          // There was an exception in trying to compute the cursor from the activity context.
		          // It's bad form to catch an arbitrary exception, but if there is an error here
		          // it's unclear what's going on.
		          puntContactSelection(ErrorMessages.ERROR_PHONE_UNSUPPORTED_CONTACT_PICKER);
		        } finally {
		          if (cursor != null) {
		            cursor.close();
		          }
		        }
		      } // ends if (checkContactUri ...
		      AfterPicking();
		    }  //ends if (requestCode ....
		  }


		
	
}
