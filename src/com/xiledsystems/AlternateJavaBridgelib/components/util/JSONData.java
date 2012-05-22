package com.xiledsystems.AlternateJavaBridgelib.components.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.DoubleList;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Maps;

public class JSONData  {

	private Map<String, Object> data;
	
	public JSONData(Map<String, Object> data) {
		this.data = data;
	}
	
	public Object getValue(String name) {
		return data.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public String getJSONString() {
		JSONObject topLevel = new JSONObject();		
		for (String key : data.keySet()) {
			if (data.get(key) instanceof List<?>) {
				List<Map<String, Object>> listarray = (List<Map<String,Object>>) data.get(key);				
				JSONArray jArray = new JSONArray();
				int size = listarray.size();
				for (int i = 0; i < size; i++) {
					JSONObject obj = new JSONObject();
					for (String name : listarray.get(i).keySet()) {												
						try {
							obj.put(name, listarray.get(i).get(name));
						} catch (JSONException e) {
							Log.e("JSONData", "Failed to add data into JSON");
							e.printStackTrace();
						}							
					}
					jArray.put(obj);
				}
				try {
					topLevel.put(key, jArray);
				} catch (JSONException e) {
					Log.e("JSONData", "Failed to add data into JSON");
					e.printStackTrace();
				}
			} else {				
				try {
					topLevel.put(key, data.get(key));
				} catch (JSONException e) {
					Log.e("JSONData", "Failed to add data into JSON");
					e.printStackTrace();
				}
			}
		}
		return topLevel.toString();
	}
	
	
	/**
	 * 
	 * @return A List<String> which contains all of the nested
	 * names in the array provided (if it's not an array, the list
	 * will be empty)
	 */
	public List<String> getAllNestedNames(String name) {
		List<String> list = new ArrayList<String>();
		try {			
			List<Map<String, Object>> list1 = (List<Map<String, Object>>) data.get(name);
			Map<String, Object> map = Maps.newHashMap();
			map = list1.get(0);			
			list.addAll(map.keySet());
		} catch (ClassCastException e) {
			Log.e("JSONData", "The supplied name isn't a list");
		}
		Object tmp = data.get(name);
		return list;
	}
	
	/**
	 * This checks to see if a name has nested data inside
	 * of it's value 
	 * 
	 * @param name The name to check
	 * @return true if there is nested data in this name value
	 */
	public boolean HasNestedData(String name) {
		if (data.get(name) instanceof List<?>) {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @return List<String> of the names in the top level of the Json String
	 */
	public List<String> getNames() {
		List<String> names = new ArrayList<String>();
		Set<String> nms = data.keySet();
		names.addAll(nms);
		return names;
	}
	
	
	/**
	 * 
	 *  This will return a DoubleList of the data. This converts the
	 *  data from a Map<String, Object> to a DoubleList. You must sort
	 *  through it using if (list.get(0)[1] instanceof List<?>) , or 
	 *  (list.get(0)[1] instanceof DoubleList).
	 *  
	 * @return A doublelist of the JSON data in this class. 
	 */
	public DoubleList DataToDoubleList() {
		DoubleList biglist = new DoubleList();
		for (String str : data.keySet()) {
			if (data.get(str) instanceof List<?>) {
				List<Map<String, Object>> list1 = (List<Map<String, Object>>) data.get(str);
				List<DoubleList> list3 = new ArrayList<DoubleList>();
				
				int size = list1.size();
				for (int i = 0; i < size; i++) {
					DoubleList list2 = new DoubleList();
					Map<String, Object> map = list1.get(i);
					for (String st : map.keySet()) {
						list2.add(st, map.get(st));
					}
					list3.add(list2);
				}
				biglist.add(str, list3);
			}
			else if (data.get(str) instanceof Map<?, ?>) {
				DoubleList list = new DoubleList();
				Map<String, Object> map = (Map<String, Object>) data.get(str);
				for (String st : map.keySet()) {
					list.add(st, map.get(st));
				}
				biglist.add(str, list);
			}
			else {
				biglist.add(str, data.get(str));
			}
		}
		return biglist;
	}
	
}
