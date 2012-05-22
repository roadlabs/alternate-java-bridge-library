package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Maps;

/**
 * Convenience class for setting up web headers for the Web component.
 * 
 * @author RyanBis.XiledSystems@gmail.com
 *
 */
public class WebHelper {
	
	private Map<String, List<String>> requestHeaders;
	private Map<String, String> postData;
	
	public WebHelper() {
		requestHeaders = Maps.newHashMap();
		postData = Maps.newHashMap();
	}
	
	/**
	 *  Add a header.
	 *  
	 * @param name The name of the header to add.
	 * @param values The values to attach to the above header name. Pass
	 * this as an array, or a single value.
	 */
	public void addHeader(String name, String... values) {
		requestHeaders.put(name, Arrays.asList(values));
	}
	
	/**
	 * 
	 * Internal method used by the Web class.
	 * 
	 * @return
	 */
	public Map<String, List<String>> getRequestHeaders() {
		return requestHeaders;
	}
	
	/**
	 * 
	 *  Add post data which will be converted to a string formatted
	 *   as application/x-www-form-urlencoded media type, suitable
	 *   to pass to PostText. 
	 *   
	 * @param name
	 * @param values
	 */
	public void addPostData(String name, String value) {		
		postData.put(name, value);
	}
	
	/**
	 * 
	 * Internal method used by the Web class.
	 * 
	 * @return
	 */
	public Map<String, String> getPostData() {
		return postData;
	}
	
	/**
	 * Used internally by the Web component.
	 * @return
	 */
	public int postCount() {
		return postData.size();		
	}
	
	/**
	 * Used internally by the Web component.
	 * @return
	 */
	public Set<String> getPostDataKeySet() {
		return postData.keySet();
	}
	
	/**
	 * Used internally by the Web component.
	 * @return
	 */
	public Set<String> getHeaderKeySet() {
		return requestHeaders.keySet();
	}
	
	/**
	 * Used internally by the Web component.
	 * @return
	 */
	public int headerCount() {
		return requestHeaders.size();
	}

}
