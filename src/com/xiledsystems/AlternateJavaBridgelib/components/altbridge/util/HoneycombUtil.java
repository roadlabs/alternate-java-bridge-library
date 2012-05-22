package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

public class HoneycombUtil {
	private HoneycombUtil() {		
	}
	
	/**
	 * This is only available in API 11 (Honeycomb), which is
	 * why this method is here.
	 * 
	 * @param draw ColorDrawable to get the color from
	 * 
	 * @return int of the color
	 */
	public static int getColor(Drawable draw) throws NoSuchMethodError {
		return ((ColorDrawable)draw).getColor();
	}
	
}
