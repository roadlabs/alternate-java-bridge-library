package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Maps;


public class SoapHelper {
	
	private String method="";
	private String url="";
	private Map<String, List<String>> dataMap = Maps.newHashMap();
	private static final String XML_TITLE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
	private static final String OPEN_ENVELOPE = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n";
	private static final String OPEN_BODY = "  <soap:Body>\n";
	
	
	/**
	 * 
	 *  Helper class to accomodate the SoapClient component. Use this
	 *  to create requests to use with SoapClient.
	 *  
	 */
	public SoapHelper() {		
	}
	
	/**
	 *  
	 *  Add a request. This is setup like name values. You can have multiple
	 *  values per name. 
	 *  
	 * @param name The name of the tag
	 * @param values The value of the tag
	 */
	public void addData(String name, String... values) {
		List<String> list = new ArrayList<String>();
		for (String val : values) {
			list.add(val);
		}
		dataMap.put(name, list);
	}
	
	/**
	 * 
	 *  Clears any entered data for this request.
	 *  
	 */
	public void clearData() {
		dataMap.clear();
	}
	
	/**
	 * 
	 *  Sets the url of the Soap server to connect to.
	 *  
	 * @param url url of the soap server to connect to
	 */
	public void setURL(String url) {
		this.url = url;
	}
	
	/**
	 * 
	 *  Set the method, or operation which will be run on the Soap server.
	 *  
	 * @param method
	 */
	public void setMethod(String method) {
		this.method = method;
	}
	
	/**
	 * 
	 * @return The method set in this SoapHelper
	 */
	public String getMethod() {
		return method;
	}
	
	/**
	 * 
	 * @return the url of the Soap server this SoapHelper is set to go to
	 */
	public String getUrl() {
		return url;
	}		

	/**
	 * 
	 *  This method is used internally when sending the request to
	 *  the Soap server. 
	 *  
	 * @return the generated soap envelope xml format
	 */
	public String getSoapEnvelope() {
		// Create the XML soap envelope to wrap the request
		// inside of.
		String openspace = "      <";
		StringBuilder blob = new StringBuilder();
		blob.append(XML_TITLE+OPEN_ENVELOPE+OPEN_BODY);
		blob.append("    <");
		blob.append(method);
		blob.append(" xmlns=\"http://tempuri.org/\">\n");
		Set<String> keys = dataMap.keySet();		
		for (String key : keys) {
			for (String value : dataMap.get(key)) {
				blob.append(openspace);
				blob.append(key+">");
				blob.append(value);
				blob.append("</"+key+">\n");
			}
		}
		blob.append("    </"+method+">\n");
		blob.append("  </soap:Body>\n");
		blob.append("</soap:Envelope>");
		return blob.toString();
	}
}
