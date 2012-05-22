package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Maps;
import android.graphics.Rect;


/**
 * Helper class for reading the json data for a sprite sheet.
 * 
 * @author Ryan Bis
 *
 */
public class SheetReader {
	
	private SheetReader() {		
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, SheetInfo> getSpriteLocations(String jsonData) throws JSONException {
		Map<String, SheetInfo> spriteLocs = Maps.newHashMap();
		
		JSONObject mainObj = new JSONObject(jsonData);
		JSONObject frames = mainObj.getJSONObject("frames");
		
		Iterator<String> files = frames.keys();
		while (files.hasNext()) {
			SheetInfo info = new SheetInfo();
			
			String file = files.next();
			info.setFileName(file);
			JSONObject jfile = frames.getJSONObject(file);
			
			JSONObject frame = jfile.getJSONObject("frame");
			int width = frame.getInt("w");
			int height = frame.getInt("h");
			Rect r = new Rect();
			r.left = frame.getInt("x");
			r.top = frame.getInt("y");
			r.bottom = r.top + height;
			r.right = r.left + width;
			info.setRect(r);
			info.setWidth(width);
			info.setHeight(height);
			spriteLocs.put(file, info);			
		}				
		return spriteLocs;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<SheetInfo> getSpriteFrames(String jsonData) throws JSONException {
		ArrayList<SheetInfo> list = new ArrayList<SheetInfo>();
		JSONObject mainObj = new JSONObject(jsonData);
		JSONObject frames = mainObj.getJSONObject("frames");
		
		Iterator<String> files = frames.keys();
		while (files.hasNext()) {
			SheetInfo info = new SheetInfo();
			
			String file = files.next();
			info.setFileName(file);
			JSONObject jfile = frames.getJSONObject(file);
			
			JSONObject frame = jfile.getJSONObject("frame");
			int width = frame.getInt("w");
			int height = frame.getInt("h");
			Rect r = new Rect();
			r.left = frame.getInt("x");
			r.top = frame.getInt("y");
			r.bottom = r.top + height;
			r.right = r.left + width;
			info.setRect(r);
			info.setWidth(width);
			info.setHeight(height);
			list.add(info);			
		}	
		return list;		
	}

}
