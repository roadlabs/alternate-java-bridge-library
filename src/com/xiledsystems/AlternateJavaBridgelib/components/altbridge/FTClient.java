package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.os.Handler;
import android.util.Log;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ClientLogin;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.RequestHandler;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

public class FTClient extends AndroidNonvisibleComponent {
	
	public static final String REQUEST_URL =
		      "https://www.google.com/fusiontables/api/query";
	private String token;
	private boolean authorized;
	private final static String TAG = "FTClient";
	private String username;
	private String password;
	
	
	/**
	 *  Use this along with ClientLogin to register the
	 *  Fusion Table client.
	 *  
	 *  ex: FTClient client = ClientLogin.authorize(username, password);
	 *  
	 *  NOTE: This is now deprecated, as it still runs in the
	 *  UI thread to login, which will cause a force close
	 *  on devices with a later API.
	 *  
	 * @param token
	 */
	@Deprecated
	 public FTClient(ComponentContainer container, String token) {
		 super(container);
		 this.token = token;
		 authorized = true;
	 }
	
	/**
	 * Constructor for FTClient for connecting to Fusion Tables
	 * along with Client Login
	 * 
	 * @param container "this"
	 * @param username - The username to connect with
	 * @param password - The password for the username
	 */
	public FTClient(ComponentContainer container, final String username, final String password) {
		super(container);
		this.username = username;
		this.password = password;
		
				
	}
	
	public void Login() {
		if (username != null && password != null) {
			final FTClient f = this;
			ThreadTimer.runOneTimeThread(new Runnable() {			
				@Override
				public void run() {
					token = ClientLogin.authorize(username, password);
					if (token != null || !token.equals("")) {
						authorized = true;
						sendLoginAck(f, true);
					} else {
						sendLoginAck(f, false);
					}
				}
			});
		}
	}
	
	/**
	 * 
	 * @return Whether the client is logged in or not.
	 */
	public boolean LoggedIn() {
		return authorized;
	}
	 
	 
	 @SuppressWarnings("static-access")
	 /**
	  * Network operations should be run in a 
	  * seperate thread from the main UI thread. This method
	  * should be run inside a thread (use ThreadTimer.runOneTimeThread(runnable) ).
	  * 
	  * The Query() method (with a capital Q) will send the request in a seperate
	  * thread, and return the results in the "Response" event.
	  * 
	  * Send a query to fusion tables
	  * 
	  * @param query
	  * 
	  * @return The result as a string
	  */	 
	public String query(String query) {
		 if (authorized) {
		    String result = "";

		    // Create the auth header
		    Map<String, String> headers = new HashMap<String, String>();
		    headers.put("Authorization", "GoogleLogin auth=" + this.token);

		    // Convert to lower for comparison below
		    String lower = query.toLowerCase();
		    // Encode the query
		    query = "sql=" + URLEncoder.encode(query);

		    // Determine POST or GET based on query
		    if (lower.startsWith("select") ||
		        lower.startsWith("show") ||
		        lower.startsWith("describe")) {

		      result = RequestHandler.sendHttpRequest(this.REQUEST_URL + "?" + query,
		          "GET", null, headers);

		    } else {
		      result = RequestHandler.sendHttpRequest(this.REQUEST_URL,
		          "POST", query, headers);
		    }

		    return result;
		 } else {
			 Log.e(TAG, "Attempted query when not authorized.");
			 return "Not authorized. Are you sure you logged on?";
		 }
	}
	 
	 /**
	  * 
	  * Send a query to fusion tables.
	  * 
	  * This component will throw the "Response" event when
	  * a response is received. The response is sent in the
	  * event.
	  * 
	  * @param query
	  * 
	  *
	  */
	 public void Query(final String query) {
		 final FTClient f = this;
		 Runnable run = new Runnable() {				
				@Override
				public void run() {
					int count = 0;
					while (count < 10) {
						if (authorized) {
							String q = query;
							String result = "";

							// Create the auth header
							Map<String, String> headers = new HashMap<String, String>();
							headers.put("Authorization", "GoogleLogin auth=" + token);

							// Convert to lower for comparison below
							String lower = q.toLowerCase();
							// Encode the query
							q = "sql=" + URLEncoder.encode(q);

							// 		Determine POST or GET based on query
							if (lower.startsWith("select") ||
									lower.startsWith("show") ||
									lower.startsWith("describe")) {

								result = RequestHandler.sendHttpRequest(REQUEST_URL + "?" + q,
										"GET", null, headers);

							} else {
								result = RequestHandler.sendHttpRequest(REQUEST_URL,
										"POST", q, headers);
							}
							sendResponse(f, result);
							return;
						} else {
							count++;
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								Log.e(TAG, "Thread interrupted whlie waiting for authorization.");
							}
						}
					}
					Log.e(TAG, "Client was unable to authorize.");
				}
			};
		  ThreadTimer.runOneTimeThread(run);
		 
	 }
	 
	 
	 
	 private void sendLoginAck(final FTClient client, final boolean result) {		 
		 container.$form().post(new Runnable() {			
			@Override
			public void run() {
				EventDispatcher.dispatchEvent(client, "LoginResponse", result);				
			}
		});
	 }


	private void sendResponse(final FTClient client, final String result) {		
		container.$form().post(new Runnable() {			
			@Override
			public void run() {
				EventDispatcher.dispatchEvent(client, "Response", result);
			}
		});		
	}
}
