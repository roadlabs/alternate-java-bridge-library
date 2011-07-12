package com.xiledsystems.AlternateJavaBridgelib;

import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.devtools.simple.runtime.components.android.AndroidNonvisibleComponent;
import com.google.devtools.simple.runtime.components.android.ComponentContainer;
import com.google.devtools.simple.runtime.components.android.Deleteable;
import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.components.util.JsonUtil;

public class TinyDB2 extends AndroidNonvisibleComponent implements Component, Deleteable {
	
	private SharedPreferences sharedPreferences;
	
	public TinyDB2(ComponentContainer container) {
		super(container.$form());
		final Context context = (Context) container.$context();
		sharedPreferences = context.getSharedPreferences("TinyDB2", Context.MODE_PRIVATE);		
	}
	
	public void StoreValue(final String tag, final Object valueToStore) {
		final SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();
		sharedPrefsEditor.putString(tag, valueToStore.toString());
		sharedPrefsEditor.commit();
	}
	
	public Object GetValue(final String tag) {
		try {
			String value = sharedPreferences.getString(tag, "");
			return (value.length() == 0) ? "" : JsonUtil.getObjectFromJson(value);
		} catch (JSONException e) {
			throw new RuntimeException("Value failed to convert from JSON.");
		}
	}
	
	@Override
	public void onDelete() {
		final SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();
		sharedPrefsEditor.clear();
		sharedPrefsEditor.commit();
	}

}
