package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * ListPickerActivity class - Brings up a list of items specified in an intent
 * and returns the selected item as the result.
 *
 */
public class ListPickerActivity extends ListActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String items[] = null;
    int layout = 0;
    int textViewId=0;
    Intent myIntent = getIntent();
    if (myIntent.hasExtra(ListPicker.LIST_ACTIVITY_ARG_NAME)) {
      items = getIntent().getStringArrayExtra(ListPicker.LIST_ACTIVITY_ARG_NAME);
      layout = getIntent().getIntExtra(ListPicker.LIST_ACTIVITY_LAYOUT, android.R.layout.simple_list_item_1);
      textViewId = getIntent().getIntExtra(ListPicker.LIST_ACTIVITY_TEXTVIEWID, android.R.id.text1);
      //setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
      setListAdapter(new ArrayAdapter<String>(this, layout, textViewId, items));
      getListView().setTextFilterEnabled(true);
    } else {
      setResult(RESULT_CANCELED);
      finish();
    }
  }

  @Override
  public void onListItemClick(ListView lv, View v, int position, long id) {
    Intent resultIntent = new Intent();
    resultIntent.putExtra(ListPicker.LIST_ACTIVITY_RESULT_NAME,
                          (String) getListView().getItemAtPosition(position));
    resultIntent.putExtra(ListPicker.LIST_ACTIVITY_RESULT_INDEX, position + 1);
    setResult(RESULT_OK, resultIntent);
    finish();
  }

}
