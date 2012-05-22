package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.Set;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Sets;
import android.content.Context;
import android.content.SharedPreferences;


public class Prefs extends AndroidNonvisibleComponent implements Deleteable {

	private SharedPreferences prefs;
	
	/**
	 * Constructor for Prefs component. This uses android's SharedPreferences
	 * to store data. It's great for preferences, but not for large amounts
	 * of data. 
	 * 
	 * @param container
	 */
	public Prefs(ComponentContainer container) {
		super(container);
		prefs = container.$context().getSharedPreferences("AB_Prefs", Context.MODE_PRIVATE);
	}
	
	public void StoreBoolean(String tag, boolean valueToStore) {
		final SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(tag, valueToStore);
		edit.commit();
	}
	
	
	public void StoreInt(String tag, int valueToStore) {
		final SharedPreferences.Editor edit = prefs.edit();
		edit.putInt(tag, valueToStore);
		edit.commit();
	}
	
	public void StoreLong(String tag, long valueToStore) {
		final SharedPreferences.Editor edit = prefs.edit();
		edit.putLong(tag, valueToStore);
		edit.commit();
	}
	
	public void StoreString(String tag, String valueToStore) {
		final SharedPreferences.Editor edit = prefs.edit();
		edit.putString(tag, valueToStore);
		edit.commit();		
	}
	
	public void StoreStringSet(String tag, Set<String> valueToStore) {
		final SharedPreferences.Editor edit = prefs.edit();
		edit.putStringSet(tag, valueToStore);
		edit.commit();		
	}
	
	public boolean GetBoolean(String tag) {
		return prefs.getBoolean(tag, false);
	}
	
	public int GetInt(String tag) {
		return prefs.getInt(tag, 0);
	}
	
	public long GetLong(String tag) {
		return prefs.getLong(tag, 0);
	}
	
	public String GetString(String tag) {
		return prefs.getString(tag, "");
	}
	
	public Set<String> GetStringSet(String tag) {
		Set<String> set = Sets.newHashSet();
		return prefs.getStringSet(tag, set);
	}
	
	

	@Override
	public void onDelete() {
		final SharedPreferences.Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}
	
	
}
