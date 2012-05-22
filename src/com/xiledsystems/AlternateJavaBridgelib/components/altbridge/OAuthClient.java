package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.io.IOException;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Bundle;
import android.content.Intent;

public class OAuthClient extends AndroidNonvisibleComponent {
	
	private final static String TAG = "OAuthClient";
	
	private String token;
	private String clientId;
	private String clientSecret;
	private String apiKey;
	private String url;
	private String tokenType;
	private String accountName;
	
	/**
	 * Constructor for connecting with OAuth services.
	 * Remember to add the INTERNET, and ACCOUNT_MANAGER
	 * permissions in your project's Android Manifest.
	 * 
	 * @param container
	 */
	public OAuthClient(ComponentContainer container) {
		super(container);
		
		AccountManager am = AccountManager.get(container.$context());
		
	}
	
	/**
	 * Use this method to setup the OAuth client for whichever service
	 * you need to connect to. The four strings to input into this method
	 * should be provided by the service provider.
	 * 
	 * @param clientId - Your app's client Id
	 * @param clientSecret - Your app's client Secret
	 * @param apiKey - The api key to use
	 * @param url
	 */
	public void setClient(String clientId, String clientSecret, String apiKey, String url) {
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.apiKey = apiKey;
		this.url = url;
	}
	
	/**
	 * Set this to tell the auth what type of credentials you are seeking.
	 * This is specific to the API. (Google tasks has "View your tasks" for
	 * read only access, and "Manage your tasks" for read/write access)
	 * 
	 * @param authType
	 */
	public void AuthType(String authType) {
		tokenType = authType;
	}
	
	public String AccountName() {
		return accountName;
	}
	
	public void AccountName(String accountName) {
		this.accountName = accountName;
	}
	
	public String Token() {
		return token;
	}
	
	public void Token(String token) {
		this.token = token;
	}
	
	
	private class TokenReceived implements AccountManagerCallback<Bundle> {

		@Override
		public void run(AccountManagerFuture<Bundle> result) {
			//Get the result from the bundle
			Bundle bundle = null;
			try {
				bundle = result.getResult();
			} catch (OperationCanceledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (AuthenticatorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			// Check to see if it needs more user input
			Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
			if (launch != null) {
				container.$context().startActivityForResult(launch, Form.OAUTH_REQUEST_CODE);
				return;
			}
			// Grab the token from the result bundle.
			Token(bundle.getString(AccountManager.KEY_AUTHTOKEN));
			AccountName(bundle.getString(AccountManager.KEY_ACCOUNT_NAME));
		}
		
	}

}
