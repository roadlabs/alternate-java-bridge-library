package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.net.URLEncoder;

public class ClientLogin {
	private final static String authURI = "https://www.google.com/accounts/ClientLogin";
	
	public static String authorize(String username, String password) {
		String body = "Email=" + URLEncoder.encode(username) + "&" +
			      "Passwd=" + URLEncoder.encode(password) + "&" +
			      "service=fusiontables&" +
			      "accountType=HOSTED_OR_GOOGLE";
		String response = RequestHandler.sendHttpRequest(authURI, "POST", body, null);
		
		String[] splitResponse = response.trim().split("=");
		if (splitResponse.length == 4) {
			return splitResponse[3];
		}
		return null;
	}
}
