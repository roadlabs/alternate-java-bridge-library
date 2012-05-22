package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.ArrayList;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.DoubleList;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.PickerList;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.CustomListItem;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
 * A button allowing a user to select one among a list of text strings.
 *
 */

public class ListPicker extends Picker implements ActivityResultListener, Deleteable {

  private static final String LIST_ACTIVITY_CLASS = ListPickerActivity.class.getName();
  static final String LIST_ACTIVITY_ARG_NAME = LIST_ACTIVITY_CLASS + ".list";
  static final String LIST_ACTIVITY_RESULT_NAME = LIST_ACTIVITY_CLASS + ".selection";
  static final String LIST_ACTIVITY_RESULT_INDEX = LIST_ACTIVITY_CLASS + ".index";
  public static final String LIST_CUSTOM_ROWID = "CustomRowId";
  public static final String LIST_CUSTOM_TEXT1 = "FirstText";
  public static final String LIST_CUSTOM_TEXT2 = "SecondText";
  public static final String LIST_CUSTOM_IMGV = "ImageView";  
  public static final String LIST_CUSTOM_ITEMS = "CustomItemsList";
  
  // AJB change - Remove YailLists replace with ArrayList, add abliity to customize listpicker view
  
  static final String LIST_ACTIVITY_LAYOUT = LIST_ACTIVITY_CLASS + ".layout";
  static final String LIST_ACTIVITY_TEXTVIEWID = LIST_ACTIVITY_CLASS + ".textViewId";
  static final String LIST_ACTIVITY_HEADERS = LIST_ACTIVITY_CLASS + ".headers";
  private int textViewId = android.R.id.text1;			// Default textview ID to populate info into
  private int layout=android.R.layout.simple_list_item_1; // This is the default layout view of the listpicker

  private ArrayList<String> items;
  private ArrayList<String> headers;
  private ArrayList<CustomListItem> custItems;
  private String selection;
  private int selectionIndex;
  private boolean twoline;
  private String[] selections;
  private int rowLayout;
  private int text1;
  private int text2;
  private int imgView;
  
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
   * 
   * @return A string array with the selection's two text fields when using
   * a twoline listpicker.
   */
  public String[] TwoLineSelection() {
	  return new String[] { headers.get(selectionIndex-1), items.get(selectionIndex-1) };
  }
  
  public String[] Selections() {
	  return selections;
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
   * Sets the layout used for the custom list display
   * 
   * @param layoutId the resource int for the layout
   *  
   */
  
  public void SetLayout(int layoutId) {
	  layout = layoutId;	  
  }
  
  /**
   * Use this to customize the list row layout.
   * 
   * @param layoutId - The resource Id of your custom layout to use
   * @param textView1 - The resource Id of the first text view to fill with text
   * @param textView2 - The resource Id of the second text view to fill with text (can be null if not needed)
   * @param imageView - The resource Id of the imageview to manipulate
   */
  public void rowLayout(int layoutId, int textView1, int textView2, int imageView) {
	  rowLayout = layoutId;
	  text1 = textView1;
	  text2 = textView2;
	  imgView = imageView;
  }
  
  /**
   * 
   *  This sets the ListPicker to a two line format.
   *  
   * @param headers A list of header names
   * @param content A list of the content
   */
  public void TwoLine(ArrayList<String> headers, ArrayList<String> content) {
	  twoline = true;
	  this.headers = headers;
	  items = content;
	  
  }
  
  /**
   * 
   *  This also sets the ListPicker to a two line format.
   *  This lets you use a DoubleList to set the two lines of
   *  data in the listpicker.
   *  
   * @param doubleList A doublelist containing headers, and content
   * 
   */
  public void TwoLine(DoubleList doubleList) {
	  twoline = true;
	  headers = doubleList.getStringList(1);
	  items = doubleList.getStringList(2);
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
   * Use this method to set the contents of the listpicker
   * making use of an ArrayList of CustomListItems. You can
   * set two different text views, and one imageview. This
   * may get expanded depending on interest.
   * 
   * @param itemList
   */
  public void CustomElements(ArrayList<CustomListItem> itemList) {
	  this.custItems = itemList;
  }
  
  /**
   * 
   * @return an ArrayList<CustomListItem> of the items to fill the picker with
   */
  public ArrayList<CustomListItem> CustomElements() {
	  return custItems;
  }
  
  /**
   * Elements property setter method
   * @param itemList - an ArrayList<String> containing the strings to be added to the
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
		if (headers != null && headers.size() > 0) {
			intent.putExtra(LIST_ACTIVITY_HEADERS, headers.toArray(new String[0]));			
		}		
		intent.putExtra(LIST_ACTIVITY_LAYOUT, layout);
		intent.putExtra(LIST_ACTIVITY_TEXTVIEWID, textViewId);
		if (rowLayout > 0) {
			intent.putExtra(LIST_CUSTOM_ROWID, rowLayout);
		}
		if (text1 > 0) {
			intent.putExtra(LIST_CUSTOM_TEXT1, text1);
		}
		if (text2 > 0) {
			intent.putExtra(LIST_CUSTOM_TEXT2, text2);
		}
		if (imgView > 0) {
			intent.putExtra(LIST_CUSTOM_IMGV, imgView);
		}
		if (custItems != null && custItems.size() > 0) {	
			Bundle b = new Bundle();
			PickerList list = new PickerList(custItems);
			b.putParcelable(LIST_CUSTOM_ITEMS, list);
			intent.putExtras(b);
		}
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
    	  if (!twoline) {
    		  selection = data.getStringExtra(LIST_ACTIVITY_RESULT_NAME);
    	  } else {
    		 // selections = data.getStringArrayExtra(LIST_ACTIVITY_RESULT_NAME);
    	  }
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
