package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.StateSet;
import android.widget.TabHost;
import android.widget.TabWidget;


public class TabForm extends TabActivity {

	private final static String LOG_TAG = "TabForm";
	
	private LinearLayout layout;
	private String tabformName;
	private TabWidget tabholder;	
	private TabHost tabHost;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		tabHost = getTabHost();
		
		layout = new LinearLayout(this, ComponentConstants.LAYOUT_ORIENTATION_VERTICAL);
		tabholder = new TabWidget(this);
		layout.add(tabholder);
		
		String classname = getClass().getName();
		int lastDot = classname.lastIndexOf('.');
		tabformName = classname.substring(lastDot + 1);
		Log.d(LOG_TAG, "TabForm " + tabformName + " got onCreate");
				
		$define();
	}
	
	public void setCurrentTab(int tabnumber) {
		tabHost.setCurrentTab(tabnumber);
	}
	
	public void addTab(String title, String selected, String notselected, Class<?> formtoopen) {
		
		int[] temp = new int[2];
		temp[0] = getResources().getIdentifier(selected, "drawable", getPackageName());
		temp[1] = getResources().getIdentifier(notselected, "drawable", getPackageName());
		StateListDrawable draw = new StateListDrawable();
		BitmapDrawable draw1 = (BitmapDrawable) getResources().getDrawable(temp[0]);
		BitmapDrawable draw2 = (BitmapDrawable) getResources().getDrawable(temp[1]);
		draw.addState(new int[] {android.R.attr.state_selected}, draw1);
		draw.addState(StateSet.WILD_CARD , draw2);
		Intent intent = new Intent().setClass(this, formtoopen);
		TabHost.TabSpec spec;
		spec = tabHost.newTabSpec(title.toLowerCase()).setIndicator(title, draw).setContent(intent);
		
		
		tabHost.addTab(spec);
		
	}		
	
	void $define() {		
		throw new UnsupportedOperationException();		
	}
	
}
