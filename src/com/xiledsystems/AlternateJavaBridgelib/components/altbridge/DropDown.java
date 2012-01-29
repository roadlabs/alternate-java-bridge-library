package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.ArrayList;
import java.util.List;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class DropDown extends AndroidViewComponent implements OnItemSelectedListener {

	private final Spinner view;
	private List<String> elements;
	private String current;
	
	
	public DropDown(ComponentContainer container) {
		super(container);
		view = new Spinner(container.$context());
		view.setOnItemSelectedListener(this);
		
		container.$add(this);
		elements = new ArrayList<String>();
	}
	
	public DropDown(ComponentContainer container, int resourceId) {
		super(container, resourceId);
		view = null;
		
		android.widget.Spinner view = (Spinner) container.$form().findViewById(resourceId);
		view.setOnItemSelectedListener(this);
		
		//container.$add(this);
		elements = new ArrayList<String>();
	}
	
	public String[] getElements() {
		String[] temp = (String[]) elements.toArray();
		
		return temp;
	}
	
	public void Elements(String[] elements) {
		if (elements.length>0) {
			this.elements.clear();
			for (String element : elements) {
				this.elements.add(element);
			}
			ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(container.$context(), 
					android.R.layout.simple_spinner_item, elements);
			
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
			if (resourceId!=-1) {
				((android.widget.Spinner) container.$form().findViewById(resourceId)).setAdapter(adapter);
			} else {
				view.setAdapter(adapter);
			}
		}
		
	}
	
	public String getCurrentSelection() {
		return this.current;
	}
	
	public void setSelection(String element) {
		@SuppressWarnings("unchecked")
		ArrayAdapter<CharSequence> adapter;
		if (resourceId!=-1) {
			adapter = (ArrayAdapter<CharSequence>) ((android.widget.Spinner) container.$form().findViewById(resourceId)).getAdapter();
		} else {
			adapter = (ArrayAdapter<CharSequence>) view.getAdapter();
		}
		int position = adapter.getPosition(element);
		if (resourceId!=-1) {
			((android.widget.Spinner) container.$form().findViewById(resourceId)).setSelection(position);
		} else {
			view.setSelection(position);
		}
	}
	
	public void SelectorLayout(int layoutid, int textviewid) {
		String[] things = new String[elements.size()];
		for (int i = 0; i < elements.size(); i++) {
			things[i] = elements.get(i);
		}
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(container.$context(), layoutid, textviewid, things);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		if (resourceId!=-1) {
			((android.widget.Spinner) container.$form().findViewById(resourceId)).setAdapter(adapter);
		} else {
			view.setAdapter(adapter);
		}
	}
	
		
	@Override
	public View getView() {
		if (resourceId!=-1) {
			return (android.widget.Spinner) container.$form().findViewById(resourceId);
		} else {
			return view;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		EventDispatcher.dispatchEvent(this, "AfterSelection", parent.getItemAtPosition(pos));
		current = parent.getItemAtPosition(pos).toString();
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// Do nothing
		
	}
	
	@Override
	public void postAnimEvent() {
		EventDispatcher.dispatchEvent(this, "AnimationMiddle");
		
	}

}
