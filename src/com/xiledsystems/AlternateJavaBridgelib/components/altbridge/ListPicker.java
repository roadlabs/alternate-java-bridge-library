package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;

/**
 * A button allowing a user to select one among a list of text strings.
 *
 */

public class ListPicker extends Picker implements ActivityResultListener, Deleteable {

  private static final String LIST_ACTIVITY_CLASS = ListPickerActivity.class.getName();
  static final String LIST_ACTIVITY_ARG_NAME = LIST_ACTIVITY_CLASS + ".list";
  static final String LIST_ACTIVITY_RESULT_NAME = LIST_ACTIVITY_CLASS + ".selection";
  static final String LIST_ACTIVITY_RESULT_INDEX = LIST_ACTIVITY_CLASS + ".index";
  
  // AJB change - Remove YailLists replace with ArrayList, add abliity to customize listpicker view
  
  static final String LIST_ACTIVITY_LAYOUT = LIST_ACTIVITY_CLASS + ".layout";
	static final String LIST_ACTIVITY_TEXTVIEWID = LIST_ACTIVITY_CLASS + ".textViewId";
	private int textViewId = android.R.id.text1;			// Default textview ID to populate info into
	private int layout=android.R.layout.simple_list_item_1; // This is the default layout view of the listpicker

  private ArrayList<String> items;	
  private String selection;
  private int selectionIndex;

  /**
   * Create a new ListPicker component.
   *
   * @param container the parent container.
   */
  public ListPicker(ComponentContainer container) {
    super(container);
    items = new ArrayList<String>();
    selection = "";
    selectionIndex = 0;
  }
  
  public ListPicker(ComponentContainer container, int resourceId) {
	    super(container, resourceId);
	    items = new ArrayList<String>();
	    selection = "";
	    selectionIndex = 0;
	  }

  /**
   * Selection property getter method.
   */
  
  public String Selection() {
    return selection;
  }

  /**
   * Selection property setter method.
   */
  
  public void Selection(String value) {
	  selection = value;
		for (int i =0; i < items.size();i++) {
			if (items.get(i).equals(value)) {
				selectionIndex = i + 1;
				return;
			}
		}
		selectionIndex = 0;
  }
  
  /**
   * Sets the layout used for the list display
   * @param layoutId the resource int for the layout
   * @param textViewId the resource int for the textview
   */
  
  public void SetLayout(int layoutId, int textViewId) {
	  layout = layoutId;
	  this.textViewId = textViewId;
  }

  /**
   * Selection index property getter method.
   */
  
  public int SelectionIndex() {
    return selectionIndex;
  }

  /**
   * Selection index property setter method.
   */
  // Not a designer property, since this could lead to unpredictable
  // results if Selection is set to an incompatible value.
  
  public void SelectionIndex(int index) {
	  if (index <= 0 || index > items.size()) {
			selectionIndex = 0;
			selection = "";
		} else {
			selectionIndex = index;
			selection = items.get(selectionIndex-1);
		}
  }

  /**
   * Elements property getter method
   *
   * @return a YailList representing the list of strings to be picked from
   */
  
  public ArrayList<String> Elements() {
		return items;
	}

  /**
   * Elements property setter method
   * @param itemList - a YailList containing the strings to be added to the
   *                   ListPicker
   */
  // TODO(user): we need a designer property for lists
  
  public void Elements(ArrayList<String> itemList) {
		Object[] objects = itemList.toArray();
		for (int i = 0; i < objects.length; i++) {
			if (!(objects[i] instanceof String)) {
				throw new RuntimeException("Items passed to ListPicker2 must be Strings");
			}
		}
		items = itemList;
	}

  /**
   * ElementsFromString property setter method
   *
   * @param itemstring - a string containing a comma-separated list of the
   *                     strings to be picked from
   */
  
  // TODO(user): it might be nice to have a list editorType where the developer
  // could directly enter a list of strings (e.g. one per row) and we could
  // avoid the comma-separated business.
  
  public void ElementsFromString(String itemstring) {
		if (itemstring.length() ==0) {
			items = new ArrayList<String>();
		} else {
			Object[] obj = itemstring.split(" *, *");
			items = new ArrayList<String>(); 
			for (int i = 0;i<obj.length;i++) {
				items.add(obj[i].toString());
			}			
		}
	}

  @Override
	protected Intent getIntent() {
		Intent intent = new Intent();
		intent.setClassName(container.$context(), LIST_ACTIVITY_CLASS);
		intent.putExtra(LIST_ACTIVITY_ARG_NAME, items.toArray(new String[0]));
		intent.putExtra(LIST_ACTIVITY_LAYOUT, layout);
		intent.putExtra(LIST_ACTIVITY_TEXTVIEWID, textViewId);
		return intent;		
	}

  /**
   * Callback method to get the result returned by the list picker activity
   *
   * @param requestCode a code identifying the request.
   * @param resultCode a code specifying success or failure of the activity
   * @param data the returned data, in this case an Intent whose data field
   *        contains the selected item.
   */
  public void resultReturned(int requestCode, int resultCode, Intent data) {
    if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK) {
      if (data.hasExtra(LIST_ACTIVITY_RESULT_NAME)) {
        selection = data.getStringExtra(LIST_ACTIVITY_RESULT_NAME);
      } else {
        selection = "";
      }
      selectionIndex = data.getIntExtra(LIST_ACTIVITY_RESULT_INDEX, 0);
      AfterPicking();
    }
  }

  // Deleteable implementation

  @Override
  public void onDelete() {
    container.$form().unregisterForActivityResult(this);
  }

}
