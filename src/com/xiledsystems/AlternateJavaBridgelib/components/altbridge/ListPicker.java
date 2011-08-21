package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

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

  private YailList items;
  private String selection;
  private int selectionIndex;

  /**
   * Create a new ListPicker component.
   *
   * @param container the parent container.
   */
  public ListPicker(ComponentContainer container) {
    super(container);
    items = new YailList();
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
    // Now, we need to change SelectionIndex to correspond to Selection.
    // If multiple Selections have the same SelectionIndex, use the first.
    // If none do, arbitrarily set the SelectionIndex to its default value
    // of 0.
    for (int i = 0; i < items.size(); i++) {
      // The comparison is case-sensitive to be consistent with yail-equal?.
      if (items.getString(i).equals(value)) {
        selectionIndex = i + 1;
        return;
      }
    }
    selectionIndex = 0;
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
      // YailLists are 0-based, but we want to be 1-based.
      selection = items.getString(selectionIndex - 1);
    }
  }

  /**
   * Elements property getter method
   *
   * @return a YailList representing the list of strings to be picked from
   */
  
  public YailList Elements() {
    return items;
  }

  /**
   * Elements property setter method
   * @param itemList - a YailList containing the strings to be added to the
   *                   ListPicker
   */
  // TODO(user): we need a designer property for lists
  
  public void Elements(YailList itemList) {
    Object[] objects = itemList.toStringArray();
    for (int i = 0; i < objects.length; i++) {
      if (!(objects[i] instanceof String)) {
        throw new YailRuntimeError("Items passed to ListPicker must be Strings", "Error");
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
    if (itemstring.length() == 0) {
      items = new YailList();
    } else {
      items = YailList.makeList((Object[]) itemstring.split(" *, *"));
    }
  }

  @Override
  public Intent getIntent() {
    Intent intent = new Intent();
    intent.setClassName(container.$context(), LIST_ACTIVITY_CLASS);
    intent.putExtra(LIST_ACTIVITY_ARG_NAME, items.toStringArray());
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
