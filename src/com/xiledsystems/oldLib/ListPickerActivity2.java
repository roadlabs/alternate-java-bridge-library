package com.xiledsystems.AlternateJavaBridgelib;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.xiledsystems.AlternateJavaBridgelib.ListPicker2;




public class ListPickerActivity2 extends ListActivity {
	
	@Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    String items[] = null;
	    Intent myIntent = getIntent();
	    if (myIntent.hasExtra(ListPicker2.LIST_ACTIVITY_ARG_NAME)) {
	      items = getIntent().getStringArrayExtra(ListPicker2.LIST_ACTIVITY_ARG_NAME);
	      setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
	      getListView().setTextFilterEnabled(true);
	    } else {
	      setResult(RESULT_CANCELED);
	      finish();
	    }
	  }
	
	@Override
	  public void onListItemClick(ListView lv, View v, int position, long id) {
	    Intent resultIntent = new Intent();
	    resultIntent.putExtra(ListPicker2.LIST_ACTIVITY_RESULT_NAME,
	                          (String) getListView().getItemAtPosition(position));
	    resultIntent.putExtra(ListPicker2.LIST_ACTIVITY_RESULT_INDEX, position + 1);
	    setResult(RESULT_OK, resultIntent);
	    finish();
	  }

}
