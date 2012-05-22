package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.ArrayList;
import java.util.List;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class DropDown extends AndroidViewComponent implements OnItemSelectedListener, OnInitializeListener {

	private final Spinner view;
	private List<String> elements;
	private String current;
	private boolean initialized;
	private boolean manual;
	
	/**
	 *  Constructor for the DropDown visible component. Use this constructor
	 *  if you are doing your UI design in code.
	 *  
	 * @param container - The container this visible component will reside in
	 */
	public DropDown(ComponentContainer container) {
		super(container);
		view = new Spinner(container.$context());
		view.setOnItemSelectedListener(this);
		
		container.$add(this);
		elements = new ArrayList<String>();
	}
	
	/**
	 * 
	 * Constructor for the DropDown visible component. Use this
	 * constructor when you've placed the DropDown in the GLE.
	 * 
	 * @param container - Always use "this" (without the quotes) for
	 * this parameter.
	 * @param resourceId - The resource id of the DropDown you placed
	 * in the GLE (ex. R.id.aBdropDown1)
	 */
	public DropDown(ComponentContainer container, int resourceId) {
		super(container, resourceId);
		view = null;
		
		android.widget.Spinner view = (Spinner) container.$form().findViewById(resourceId);
		view.setOnItemSelectedListener(this);
		
		//container.$add(this);
		elements = new ArrayList<String>();
	}
	
	/**
	 *   
	 * @return String array of the elements in the DropDown list.
	 */
	public String[] getElements() {
		String[] temp = (String[]) elements.toArray();
		
		return temp;
	}
	
	
	/**
	 * Sets the elements in the Dropdown list
	 * 
	 * @param elements - String array of elements
	 */
	public void Elements(String[] elements) {
		if (elements.length>0) {
			manual = true;
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
	
	/**
	 * Set the prompt to display before user makes a choice.
	 * 
	 * @param prompt
	 */
	public void Prompt(String prompt) {
		if (view == null) {
			((android.widget.Spinner) container.$form().findViewById(resourceId)).setPrompt(prompt);
		} else {
			view.setPrompt(prompt);			
		}
	}
	
	/**
	 * Get the current prompt, if any.
	 * 
	 * @return
	 */
	public String Prompt() {
		if (view == null) {
			return ((android.widget.Spinner) container.$form().findViewById(resourceId)).getPrompt().toString();
		} else {
			return view.getPrompt().toString();
		}
	}
	
	
	/**
	 * 
	 * @return the current selection.
	 */
	public String getCurrentSelection() {
		return this.current;
	}
	
	/**
	 * Set what item the DropDown list is set on.
	 * 
	 * @param element the item to set the selection to.
	 */
	public void setSelection(String element) {
		@SuppressWarnings("unchecked")
		ArrayAdapter<CharSequence> adapter;
		manual = true;
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
	
	/**
	 * This allows you to set the layout type for the Dropdown view.
	 * You can design your own layot in the GLE, then pass the
	 * layout into the DropDown. You must have a listview in there.
	 * This hasn't been tested very much/at all. 
	 * @param layoutid resource Id of the layout
	 * @param textviewid the textview resource id (this is where the elements are displayed)
	 */
	public void SelectorLayout(int layoutid, int textviewid) {
		String[] things = new String[elements.size()];
		manual = true;
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
	
		
	/**
	 * This is for more advanced users who want to be able to tap into
	 * the android view this component is based on. This allows you
	 * more flexibility with the bridge, should you need it. 
	 */
	@Override
	public View getView() {
		if (resourceId!=-1) {
			return (android.widget.Spinner) container.$form().findViewById(resourceId);
		} else {
			return view;
		}
	}

	/**
	 * This is used internally, you should never have to call it yourself.
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		if (initialized && !manual) {
			EventDispatcher.dispatchEvent(this, "AfterSelection", parent.getItemAtPosition(pos));
			current = parent.getItemAtPosition(pos).toString();
		}		
		if (manual) {
			manual = false;
		}
	}

	/**
	 * Internal method. No need to call this.
	 */
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// Do nothing
		
	}
	
	/**
	 * Internal method. No need to call this.
	 */
	@Override
	public void postAnimEvent() {
		EventDispatcher.dispatchEvent(this, "AnimationMiddle");
		
	}
	
	/**
	 * Internal method. No need to call this.
	 */
	@Override
	public void onInitialize() {
		super.onInitialize();
		initialized = true;
	}


}
