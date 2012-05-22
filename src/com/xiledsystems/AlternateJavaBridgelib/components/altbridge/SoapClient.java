package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.util.Log;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.SoapHelper;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;


public class SoapClient extends AndroidNonvisibleComponent {
	
	private SoapHelper helper;
	private String soapResponse;
	final SoapClient client = this;
	
	/**
	 * 
	 *  Constructor for the SoapClient in a FormService.
	 * @param container Always use this
	 * @param helper The SoapHelper you used to create a request.
	 */
	public SoapClient(ComponentContainer container, SoapHelper helper) {
		super(container);
		this.helper = helper;
	}
	
	/**
	 * 
	 *  Constructor for the SoapClient in a FormService.
	 *  
	 * @param container Always use this
	 * @param helper The SoapHelper you used to create a request.
	 */
	public SoapClient(SvcComponentContainer container, SoapHelper helper) {
		super(container);
		this.helper = helper;
	}
	
	/**
	 * 
	 *  Post your request to the soap server. The "SoapResponse" Event will be
	 *  thrown when the response comes in. Make sure your request is setup
	 *  properly with SoapHelper.
	 *  
	 */
	public void PostRequest() {
		Thread thread = new Thread(new Runnable() {			
			@Override
			public void run() {
				URL url;
				HttpURLConnection connection = null;		
				try {
					url = new URL(helper.getUrl());			
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");
					connection.setRequestProperty("SOAPAction", "http://tempuri.org/"+helper.getMethod());
					connection.addRequestProperty("Content-Type", "text/xml");			
					connection.setUseCaches(false);
					connection.setDoInput(true);
					connection.setDoOutput(true);			
					DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
					dos.writeBytes(helper.getSoapEnvelope());
					dos.flush();
					dos.close();			
					InputStream is;
					if (connection.getResponseCode() <= 400) {				
						if (!connection.getResponseMessage().trim().equals("OK")) {
							Log.e("SoapClient", "Couldn't complete request. Denied by host. Response Message From Server: "+connection.getResponseMessage());
							return;
						} else {
							is = connection.getInputStream();
						}
					} else {
						is = connection.getErrorStream();
					}
					
					BufferedReader rd = new BufferedReader(new InputStreamReader(is));
					String line;
					StringBuffer response = new StringBuffer();
					while ((line = rd.readLine()) != null) {
						response.append(line);
						response.append('\r');
					}
					rd.close();			
					String fullresponse = response.toString();
					String resultmsg = "<"+helper.getMethod()+"Result>";			
					if (!fullresponse.contains(resultmsg)) {
						Log.e("SoapClient", "Result does not contain results for the requested Method.");
						return;
					} else {
						String text = fullresponse.split(resultmsg)[1];
						String endmsg = "</"+helper.getMethod()+"Result>";
						text = text.split(endmsg)[0];
						SendResponseEvent(text);
					}
								
				} catch (IOException e) {
					Log.e("SoapClient", "File not found exception. Check your request.");
					e.printStackTrace();
				}						
			}
		});
		thread.start();		
		
	}
	
	/**
	 * 
	 *  Use this to set a new request for the SoapClient.
	 *  
	 * @param helper
	 */
	public void NewRequest(SoapHelper helper) {
		soapResponse = "";
		this.helper = helper;
	}
	
		
	/**
	 * 
	 *  This will return the response the SoapClient received from the
	 *  request. This should be run after the SoapResponse event fires.
	 * 
	 * @return the response from the soap server
	 */
	public String getResponse() {
		return soapResponse;
	}

	/**
	 *  This method is called internally when it receives a response
	 *  from the Soap server.
	 *  
	 * @param response
	 */
	public void SendResponseEvent(final String response) {				
		soapResponse = response;
		if (container == null) {
			container.$form().post(new Runnable() {			
				@Override
				public void run() {
					EventDispatcher.dispatchEvent(client, "SoapResponse");				
				}
			});
		} else {
			sContainer.$formService().post(new Runnable() {			
				@Override
				public void run() {
					EventDispatcher.dispatchEvent(client, "SoapResponse");				
				}
			});
		}
						
	}

}
