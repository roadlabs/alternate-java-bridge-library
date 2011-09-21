package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.ArrayList;

public final class Convert {

	private Convert() {		
	}
	
	public static int Int(Object object) {
		return Integer.parseInt(object.toString());
	}
	
	public static String string(Object object) {
		return object.toString();
	}
	
	public static long Long(Object object) {
		return Long.parseLong(object.toString());
	}
	
	public static double Double(Object object) {
		return Double.parseDouble(object.toString());
	}
	
	public static boolean Boolean(Object object) {
		return Boolean.parseBoolean(object.toString());
	}
	
	public static ArrayList<?> Arraylist(Object object) {
		return (ArrayList<?>) object; 
	}
}
