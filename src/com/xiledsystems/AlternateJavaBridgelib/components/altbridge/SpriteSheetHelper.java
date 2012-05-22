package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONException;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.SheetInfo;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.SheetReader;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;


public class SpriteSheetHelper extends AndroidNonvisibleComponent implements OnDestroyListener {

	private final static String TAG = "SpriteSheetHelper";
	private Bitmap spriteSheet;
	private ArrayList<SheetInfo> sheetInfo;
	private int spriteCount;
	
	
	public SpriteSheetHelper(ComponentContainer container, int sheetResId) {
		super(container);		
		sheetInfo = new ArrayList<SheetInfo>();		
		BitmapDrawable draw = (BitmapDrawable) container.$context().getResources().getDrawable(sheetResId);		
		spriteSheet = draw.getBitmap();
		
	}
	
	// TODO Add checks to make sure the sheetinfo list is populated.
	/**
	 * Similar to the other getDrawable method, this one allows
	 * you to specify the filename of the sprite to use, if you
	 * don't know the frame number.
	 * 
	 * @param fileName - the original filename of the image
	 * 
	 * @return a drawable to use for setting a components image
	 */
	public Drawable getDrawable(String fileName) {		
		for (SheetInfo info : sheetInfo) {
			if (info.getName().equals(fileName)) {
				int x = info.getRect().left;
				int y = info.getRect().top;
				int width = info.getWidth();
				int height = info.getHeight();
				BitmapDrawable draw = new BitmapDrawable(container.$context().getResources(), 
						Bitmap.createBitmap(spriteSheet, x, y, width, height).copy(Bitmap.Config.ARGB_8888, true));
				return draw; 
			}
		}
		return null;
	}
	
	/**
	 * Get a drawable from the sprite sheet to set in a component.
	 * 
	 * @param frame the frame number to pull
	 * 
	 * @return a drawable to use for setting the image of a component
	 */
	public Drawable getDrawable(int frame) {
		if (frame < 0 || frame > (sheetInfo.size()-1)) {
			Log.e(TAG, "Frame number is either less than zero, or higher than the size of the info list");
			return null;
		}
		SheetInfo info = sheetInfo.get(frame);
		int x = info.getRect().left;
		int y = info.getRect().top;
		int width = info.getWidth();
		int height = info.getHeight();
		BitmapDrawable draw = new BitmapDrawable(container.$context().getResources(), 
				Bitmap.createBitmap(spriteSheet, x, y, width, height).copy(Bitmap.Config.ARGB_8888, true));
		return draw; 
	}
	
	/**
	 * This will return the order of frames. (First frame, is the first
	 * item in the list -- index 0). You can then reorder this list,
	 * and pass it back to reorder the frame list.
	 * 
	 * @return arraylist of the filenames in the sprite sheet. This is in order of frames.
	 */
	public ArrayList<String> frameOrder() {
		ArrayList<String> list = new ArrayList<String>();
		int size = sheetInfo.size();
		for ( int i = 0; i < size; i++) {
			list.add(sheetInfo.get(i).getName());
		}		
		return list;
	}
	
	/**
	 * Use this method to reorder the frame numbers. Pass an arraylist
	 * of the filenames in the order you'd like them to be in. This will
	 * then reorder the frame numbers to the order of the list you pass
	 * in.
	 * 
	 * @param names Arraylist of filenames in order
	 */
	public void setFrameOrder(ArrayList<String> names) {
		if (names.size() != sheetInfo.size()) {
			Log.e(TAG, "The names list is not the same length as the length of frames!");
			return;
		}
		// Copy the current order into a new temporary list
		ArrayList<SheetInfo> tmplist = new ArrayList<SheetInfo>(sheetInfo);
		int size = sheetInfo.size();
		
		// Clear the list before we start adding to it.
		sheetInfo.clear();
		for ( int i = 0; i < size; i++) {
			
			int oldindex=-2;
			
			// We have to iterate through the tmp list to find the index
			// of the name given
			for (int x = 0; x < size; x++) {
				if (tmplist.get(x).getName().equals(names.get(i))) {
					oldindex = x;					
				}
			}
			if (oldindex == -2) {
				Log.e(TAG, "Name was not found. Make sure your list is accurate.");
				return;
			}
			sheetInfo.add(tmplist.get(oldindex));
		}
		spriteCount = sheetInfo.size();
		
	}
	
	public int getSpriteCount() {
		return spriteCount;
	}
	
	public SheetInfo getSpriteInfo(int frame) {
		return sheetInfo.get(frame);
	}

	
	/**
	 * This method loads the JSON data file located in the
	 * assets directory. Just pass the name of the file.
	 * 
	 * @param fileName - Name of the file in assets to load (must be in json format)
	 * 
	 * @return true is the load was successful, false otherwise
	 */
	public boolean loadSheetData(String fileName) {
		InputStream in;
  		 try {
			in = container.$context().getAssets().open(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			in = null;
			return false;
		}
  		BufferedReader r = new BufferedReader(new InputStreamReader(in));
  		StringBuilder string = new StringBuilder();
  		String line;
  		try {
			while ((line = r.readLine()) != null) {
				string.append(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
  		
  		String jsonString = string.toString();
  		try {
			sheetInfo = SheetReader.getSpriteFrames(jsonString);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
  		return true;
	}
	
	
	public Drawable getMap() {		
		return new BitmapDrawable(container.$context().getResources(), spriteSheet);		
	}

	
	@Override
	public void onDestroy() {
		if (spriteSheet != null) {
			spriteSheet.recycle();
			spriteSheet = null;
		}
	}
	

}
