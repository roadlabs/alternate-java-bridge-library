package com.xiledsystems.AlternateJavaBridgelib;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;


import com.google.devtools.simple.runtime.components.android.ActivityResultListener;
import com.google.devtools.simple.runtime.components.android.ComponentContainer;
import com.google.devtools.simple.runtime.components.android.Deleteable;
import com.google.devtools.simple.runtime.components.android.Picker;
import com.xiledsystems.AlternateJavaBridgelib.ListPickerActivity2;


// It's best to view the source for the original ListPicker to see the differences. I didn't include any of 
// the code for the designer window, as it's not needed here. This will result in less bloat in the code as well.
// In this tiny example, it won't make any difference, but for larger applications, every little bit you can
// save is worth it.

public class ListPicker2 extends Picker implements ActivityResultListener, Deleteable {

	private ArrayList<String> items;			// This is our replacement for yaillist. Actually, I think it would
												// be better to just use a string array, but we'll keep it so that the
												// functionality of this component is nearly identical.
	private String selection;
	private int selectionIndex;
	private static final String LIST_ACTIVITY_CLASS = ListPickerActivity2.class.getName();
	static final String LIST_ACTIVITY_ARG_NAME = LIST_ACTIVITY_CLASS + ".list";
	static final String LIST_ACTIVITY_RESULT_NAME = LIST_ACTIVITY_CLASS + ".selection";
	static final String LIST_ACTIVITY_RESULT_INDEX = LIST_ACTIVITY_CLASS + ".index";
	
	public ListPicker2(ComponentContainer container) {
		super(container);
		items = new ArrayList<String>();
		selection = "";
		selectionIndex = 0;		
	}
	
	public String selection() {
		return selection;
	}
	
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
	
	public int SelectionIndex() {
		return selectionIndex;
	}
	
	public void SelectionIndex(int index) {
		if (index <= 0 || index > items.size()) {
			selectionIndex = 0;
			selection = "";
		} else {
			selectionIndex = index;
			selection = items.get(selectionIndex-1);
		}
	}
	
	public ArrayList<String> Elements() {
		return items;
	}
	
	public void Elements(ArrayList<String> itemList) {
		Object[] objects = itemList.toArray();
		for (int i = 0; i < objects.length; i++) {
			if (!(objects[i] instanceof String)) {
				throw new RuntimeException("Items passed to ListPicker2 must be Strings");
			}
		}
		items = itemList;
	}
	
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

	@Override
	protected Intent getIntent() {
		Intent intent = new Intent();
		intent.setClassName(container.$context(), LIST_ACTIVITY_CLASS);
		
		// Here we convert our list to a String array. If you noticed the 0,
    // it's not creating a string array of 0 strings. The list.toArray
    // automatically creates whatever size array it needs. We need to
    // sepcify new String[0] in there to convert it from being and
    // object array to a string array.
		intent.putExtra(LIST_ACTIVITY_ARG_NAME, items.toArray(new String[0]));
		return intent;		
	}
	
	@Override
	public void onDelete() {
		container.$form().unregisterForActivityResult(this);
	}

}
