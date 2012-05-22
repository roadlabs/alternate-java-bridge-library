package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.os.Debug;

/**
 * Utility class to check the memory for your app.
 * 
 * Note that memory management in android is a very complex
 * process. These methods do their best to represent how
 * much memory is being used/not used/available to your
 * app. While the methods used seem to be the most accurate,
 * I wouldn't count on them being 100% correct in every single
 * situation.
 * 
 * @author Ryan Bis
 *
 */
public class MemUtil {
	
	private MemUtil() {
	}
	
	/**
	 * 
	 * @return - The amount of memory in MB that is currently allocated to this app.
	 */
	public static double Allocated() {				
		Debug.MemoryInfo memInfo = new Debug.MemoryInfo();
		Debug.getMemoryInfo(memInfo);
		return new Double(memInfo.getTotalPss()/1024/1024);
	}
	
	/**
	 * 
	 * @return - The amount of available heap size (in MB)
	 */
	public static double Available() {				
		return new Double(Runtime.getRuntime().maxMemory()/1024/1024);
	}
	
	/**
	 * 
	 * @return - The amount of free memory left in this heap (in MB)
	 */
	public static double Free() {		
		Debug.MemoryInfo memInfo = new Debug.MemoryInfo();
		Debug.getMemoryInfo(memInfo);
		return new Double((Runtime.getRuntime().maxMemory() - memInfo.getTotalPss())/1024/1024);
	}

}
