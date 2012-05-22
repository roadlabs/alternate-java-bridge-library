package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.ArrayList;

public final class Convert {

	private Convert() {		
	}
	
	/**
	 * Converts an object to an int
	 * @param object
	 * @return
	 */
	public static int Int(Object object) {
		return Integer.parseInt(object.toString());
	}
	
	/**
	 * Converts a String to an int
	 * @param string
	 * @return
	 */
	public static int Int(String string) {
		return Integer.parseInt(string);
	}
	
	/**
	 * Converts an object to a String. Really, there's no need
	 * to use this. Just use object.toString()
	 * @param object
	 * @return
	 */
	public static String string(Object object) {
		return object.toString();
	}
	
	/**
	 * Converts an object to a long
	 * @param object
	 * @return
	 */
	public static long Long(Object object) {
		return Long.parseLong(object.toString());
	}
	
	/**
	 * Converts a String to a long
	 * @param string
	 * @return
	 */
	public static long Long(String string) {
		return Long.parseLong(string);
	}
	
	/**
	 * Converts an object to a double
	 * @param object
	 * @return
	 */
	public static double Double(Object object) {
		return Double.parseDouble(object.toString());
	}
	
	/**
	 * Converts a string to a double
	 * @param string
	 * @return
	 */
	public static double Double(String string) {
		return Double.parseDouble(string);
	}
	
	/**
	 * Converts an object to a boolean
	 * @param object
	 * @return
	 */
	public static boolean Boolean(Object object) {
		return Boolean.parseBoolean(object.toString());
	}
	
	/**
	 * Converts a string to a boolean
	 * @param string
	 * @return
	 */
	public static boolean Boolean(String string) {
		return Boolean.parseBoolean(string);
	}
	
	/**
	 * Converts an object to a float
	 * @param object
	 * @return
	 */
	public static float Float(Object object) {
		return Float.parseFloat(object.toString());
	}
	
	/**
	 * Converts a String to a float
	 * @param string
	 * @return
	 */
	public static float Float(String string) {
		return Float.parseFloat(string);
	}
	
	/**
	 * Untested. I don't think it works. Just cast it.
	 * @param object
	 * @return
	 */
	public static ArrayList<?> Arraylist(Object object) {
		return (ArrayList<?>) object; 
	}
	
	/**
	 * This will convert a number to a two digit
	 * format friendly for calendars (month, two digit year)
	 * @param object to convert
	 * @return
	 */
	public static String TwoDigits(Object object) {
		return String.format("%01", object.toString());
	}
}
