package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.graphics.Rect;

/**
 * Class for containing sprite sheet values
 * 
 * @author Ryan Bis
 *
 */
public class SheetInfo {
	
	private Rect srcRec;
	private int width;
	private int height;
	private String fileName;
	
	public void setRect(Rect rect) {
		this.srcRec = rect;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setFileName(String filename) {
		this.fileName = filename;
	}
	
	public Rect getRect() {
		return srcRec;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public String getName() {
		return fileName;
	}

}
