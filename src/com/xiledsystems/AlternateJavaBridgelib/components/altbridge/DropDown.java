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
		super(container);
		view = (Spinner) container.$form().findViewById(resourceId);
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
			view.setAdapter(adapter);
		}
		
	}
	
	public String getCurrentSelection() {
		return this.current;
	}
	
	public void setSelection(String element) {
		@SuppressWarnings("unchecked")
		ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) view.getAdapter();
		int position = adapter.getPosition(element);
		view.setSelection(position);
	}
	
	public void SelectorLayout(int layoutid, int textviewid) {
		String[] things = new String[elements.size()];
		for (int i = 0; i < elements.size(); i++) {
			things[i] = elements.get(i);
		}
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(container.$context(), layoutid, textviewid, things);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		view.setAdapter(adapter);		
	}
	
		
	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return view;
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

}
