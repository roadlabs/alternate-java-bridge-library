package com.xiledsystems.AlternateJavaBridgelib.components.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Maps;

public class JsonConverter {
	
	private final String jString;	
	private int arrayCount;
	private int objCount;
	
	
	public JsonConverter(String stringToParse) {
		this.jString = stringToParse;
		try {
			JSONObject obj = new JSONObject(jString);			 
			JSONArray array = obj.names();
			objCount = array.length();
			for (int i = 0; i < objCount; i++) {
				if (obj.get(array.get(i).toString()) instanceof JSONArray) {
					arrayCount++;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();			
		}	
	}
	
	/**
	 * 
	 * @return How many objects are in the provided string (name count)
	 */
	public int getObjectCount() {
		return objCount;
	}
	
	/**
	 * 	  
	 * @return How many lists (JSONArrays) are in the string provided.
	 */
	public int getListCount() {
		return arrayCount;
	}
	
	/**
	 * 
	 *  This returns the value from a name you supply. This returns
	 *  a Java Object, so it is up to you to convert from there.
	 *  
	 * @param name the name to get
	 * @return The object stored under the name value
	 */
	public Object getValue(String name) {
		try {
			JSONObject obj = new JSONObject(jString);
			return obj.get(name);
		} catch (JSONException e) {
			Log.e("JsonConverter", "JSON Exception. Check to make sure the string is actually in JSON format.");
			e.printStackTrace();
			return null;
		}
	}
	
	public String[] getAllNames() {
		try {
			JSONObject obj = new JSONObject(jString);
			Iterator<String> keys = obj.keys();
			List<String> tmp = new ArrayList<String>();
			do {
				tmp.add(keys.next());
			} while (keys.hasNext());
			return (String[]) tmp.toArray();
			
			
		} catch (JSONException e) {
			Log.e("JsonConverter", "JSON Exception. Check to make sure the string is actually in JSON format.");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 *  This will return a Map of all the NameValuePairs in the
	 *  string supplied to JsonConverter. This ignores all
	 *  JSONArrays in the object.
	 *  	   
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAllNamePairs() {
		Map<String, Object> map = Maps.newHashMap();
		try {
			JSONObject obj = new JSONObject(jString);			 
			JSONArray array = obj.names();			
			for (int i = 0; i < objCount; i++) {
				Object blob = obj.get(array.get(i).toString());
				if (blob instanceof JSONArray) {					
				} else {
					JSONObject jObject = new JSONObject(blob.toString());					
					Iterator<String> keys = jObject.keys();
					do {
						String key = keys.next();
						Object item = jObject.get(key);
						map.put(key, item);
						
					} while (keys.hasNext());					
				}
			}
		} catch (JSONException e) {
			Log.e("JsonConverter", "JSON Exception. Check to make sure the string is actually in JSON format.");
			e.printStackTrace();			
		}
		return map;
	}
	
	public boolean containsArrays() {
		Map<String, Object> test = getAllData();
		for (String name : test.keySet()) {
			if (test.get(name) instanceof List<?>) {
				return true;
			}
		}
		return false;
	}
	
	public JSONData getJSONData() {
		return new JSONData(getAllData());
	}
	
	/**
	 * 
	 * @return A Map<String, Object> containing the name/value pairs in the provided string.
	 * If a value is a JSON Array, it creates another Map. This class only suppports 3 levels.
	 * The Object contains further maps, if there are nested arrays.
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAllData() {
		Map<String, Object> map = Maps.newHashMap();
		
		
		try {
			// The full JSON string to parse is loaded
			// Then a JSONArray is created from the names
			// that are in the original JSONObject. This is
			// the first level to parse.
			JSONObject obj = new JSONObject(jString);			 
			JSONArray array = obj.names();				
			for (int i = 0; i < objCount; i++) {
				// Iterate through the array of names to export
				// data into the Map. These names are what's stored
				// in the String value of the Map. All values,
				// and subsequent arrays will be Maps (in the
				// object spot)
				Object blob = obj.get(array.get(i).toString());
				String name = array.get(i).toString();
				if (blob instanceof JSONArray) {
					List<Map<String, Object>> maplist1 = new ArrayList<Map<String,Object>>();			
					JSONArray bloba = (JSONArray) blob;
					int obcnt = bloba.length();
					// Run through this array's indexes and grab the name value pairs
					for (int x=0; x < obcnt; x++) {
						// If it's an object, we have our 2nd level of name/value pairs
						// If not, it's another nested array
						// Create a new map for our 2nd level of data
						Map<String, Object> map2 = Maps.newHashMap();
						if (bloba.get(x) instanceof JSONArray) {
							List<Map<String, Object>> maplist2 = new ArrayList<Map<String,Object>>();
							 // And here we check for the 3rd and final layer
							 // If people want more than that, they can parse
							 // it themselves using JSONArray and JSONObject.							
							 
							 JSONArray blobb = (JSONArray) bloba.get(x);
							 int cnt3 = blobb.length();
							 // Run through this array's indexes and grab the name value pairs
							 // This will ignore any further arrays
							 for (int y = 0; y < cnt3; y++) {
								 Map<String, Object> map3 = Maps.newHashMap();
								 if (blobb.get(y) instanceof JSONArray) {
									 Log.e("JSONConverter", "Found 4th level array. JSONConverter only supports 3 levels!");
								 } else if (blobb.get(y) instanceof JSONObject) {
									 JSONObject object3 = (JSONObject) blobb.get(y);
									 Iterator<String> keys = object3.keys();
									 do {
										 String key = keys.next();
										 Object item = object3.get(key);
										 map3.put(key, item);
									 } while (keys.hasNext());
									 maplist2.add(map3);
								 } else {
									 Log.e("JSONConverter", "Nested item is not a JSON Object! ");
								 }								 
							 }
							 map2.put(bloba.get(x).toString(), maplist2);
							 
						 } else if (bloba.get(x) instanceof JSONObject) {
							 JSONObject object2 = (JSONObject) bloba.get(x);
							 Iterator<String> keys = object2.keys();
							 do {
								 String key = keys.next();
								 Object item = object2.get(key);
								 map2.put(key, item);
							 } while (keys.hasNext());	
							 maplist1.add(map2);
						 } 						
					}
					map.put(name, maplist1);
					
				} else {
					// Grab whatever name/value pairs are in the top level
					// and add them to the map.
					
					Map<String, Object> smmap = Maps.newHashMap();
					JSONObject jObject = new JSONObject(blob.toString());					
					Iterator<String> keys = jObject.keys();
					do {
						String key = keys.next();
						Object item = jObject.get(key);
						smmap.put(key, item);
						
					} while (keys.hasNext());
					map.put(name, smmap);
				}
			}
		} catch (JSONException e) {
			Log.e("JsonConverter", "JSON Exception. Check to make sure the string is actually in JSON format.");
			e.printStackTrace();			
		}
		return map;
	}

}
