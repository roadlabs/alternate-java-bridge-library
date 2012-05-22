package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.DropboxInputStream;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;


public class DropBoxClient extends AndroidNonvisibleComponent implements OnResumeListener, OnDestroyListener {
	
	private final static String DB_PREFS = "DropBoxClient.prefs";
	private final static String KEY = "tokensKey";
	private final static String SECRET = "tokensSecret";
	private final static String TAG = "DropBoxClient";
	
	private final String APP_KEY;
	private final String APP_SECRET;
	private AccessType access_type = AccessType.APP_FOLDER;	
	private DropboxAPI<AndroidAuthSession> dbApi;
	private boolean loggedIn;
	
	
	/**
	 * DropBox Client constructor. You must set the
	 * accesstype before using Login().
	 * 
	 * Make sure you have the dropbox api jar file in your
	 * project's /libs folder (on adt versions older than
	 * r17, you will have to manually add it to the build
	 * path of the project).
	 * 
	 * Also, make sure to add the uses permissions for
	 * INTERNET in your applications manifest.
	 * 
	 * @param form "this"
	 * @param app_key - Your dropbox app key
	 * @param app_secret - Your dropbox app secret
	 */
	public DropBoxClient(Form form, String app_key, String app_secret) {
		super(form);
		APP_KEY = app_key;
		APP_SECRET = app_secret;		
		form.registerForOnResume(this);
	}
	
	/**
	 * Use this method to set the access type. This must be
	 * set before using Login()
	 * 
	 * @param type
	 */
	public void AccessType(AccessType type) {
		this.access_type = type;
	}
	
	/**
	 * 
	 * @return Whether the api has been logged in or not.
	 */
	public boolean LoggedIn() {
		return loggedIn;
	}
	
	private void storeKeys(String key, String secret) {
		SharedPreferences prefs;
		if (container == null) {
			prefs = sContainer.$formService().getSharedPreferences(DB_PREFS, Context.MODE_PRIVATE);
		} else {
			prefs = container.$form().getSharedPreferences(DB_PREFS, Context.MODE_PRIVATE);
		}
    
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(KEY, key);
		edit.putString(SECRET, secret);	 
		edit.commit();
	}
	
	private String[] getKeys() {
		SharedPreferences prefs;
		if (container == null) {
			prefs = sContainer.$formService().getSharedPreferences(DB_PREFS, Context.MODE_PRIVATE);
		} else {
			prefs = container.$form().getSharedPreferences(DB_PREFS, Context.MODE_PRIVATE);
		}
        String key = prefs.getString(KEY, null);
        String secret = prefs.getString(SECRET, null);
        if (key != null && secret != null) {
        	String[] ret = new String[2];
        	ret[0] = key;
        	ret[1] = secret;
        	return ret;
        } else {
        	return null;
        }
	}
	
	/**
	 * Have the user login to their dropbox account.
	 * If the access tokens are saved from a previous
	 * login, they will be used, otherwise the user
	 * will have to login again.
	 * 
	 */
	public void Login() {
		if (access_type != null) {			
	 		if (!loggedIn) {
	 			AndroidAuthSession session = buildSession();
	 			dbApi = new DropboxAPI<AndroidAuthSession>(session);
	 			if (container==null) {
	 				dbApi.getSession().startAuthentication(sContainer.$formService());
	 			} else {
	 				dbApi.getSession().startAuthentication(container.$form());
	 			}
	 		}
		} else {
			Log.e(TAG, "AccessType is null! You MUST provide an AccessType to connect and login.");
		}
	}
	
	/**
	 * Use this when you want to log the user out, and clear the access
	 * tokens.
	 * 
	 */
	public void Logout() {
		dbApi.getSession().unlink();
		clearKeys();
		loggedIn = false;
	}
	
		
	private void clearKeys() {
		SharedPreferences prefs;
		if (container == null) {
			prefs = sContainer.$formService().getSharedPreferences(DB_PREFS, Context.MODE_PRIVATE);
		} else {
			prefs = container.$form().getSharedPreferences(DB_PREFS, Context.MODE_PRIVATE);
		}
		SharedPreferences.Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
	}

	/**
	 * Use this method to transfer a file to the user's connected dropbox account.
	 * 
	 * @param dropboxFileName - The path to save the file to on dropbox
	 * @param uploadFileFullPath - The absolute path of the file to upload
	 * @param listener - This can be null. You can also send a progresslistener in here to
	 * hook into a progressbar, or dialog.
	 * @param overWrite - True if you want to overwrite the file.
	 * 
	 * The "Response" event will be thrown when it is known if the
	 * file was transferred successfully or not. So, args[0] will be a boolean
	 * 
	 */
	public void UploadFile(final String dropboxFileName, final String uploadFileFullPath,
			final ProgressListener listener, final boolean overWrite) {
		
		// Run network operations in a seperate thread
		final DropBoxClient c = this;
		ThreadTimer.runOneTimeThread(new Runnable() {			
			@Override
			public void run() {
				String pushFileFullPath = uploadFileFullPath;
				File push = new File(pushFileFullPath);		
				if (!push.exists()) {
					Log.e(TAG, "Push file does not exist. Make sure you are passing the absolute path of the file.");			
				} else {
					prepareDbApi();
					try {
						InputStream in = new FileInputStream(push);				
						if (overWrite) {					
							Entry en = dbApi.putFileOverwrite(dropboxFileName, in, push.length(), listener);
							if (en != null) {
								postResponse(c, true, dropboxFileName);
							} else {
								postResponse(c, false, "");
							}
						} else {
							Entry en = dbApi.putFile(dropboxFileName, in, push.length(), null, listener);
							if (en != null) {
								postResponse(c, true, dropboxFileName);
							} else {
								postResponse(c, false, "");
							}
						}
					} catch (FileNotFoundException e) {				
						e.printStackTrace();				
					} catch (DropboxException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
	}
	
	private void postResponse(final DropBoxClient client, final boolean success, final String fileName) {		
		container.$form().post(new Runnable() {			
			@Override
			public void run() {
				EventDispatcher.dispatchEvent(client, "Response", success, fileName);
			}
		});		
	}
	
	/**
	 * Use this method to download a file from dropbox.
	 * 
	 * @param dropBoxFileName The dropbox path to the file.
	 * @param downloadPath - The path to save the file to
	 * @param listener - Optional progresslistener, can be null
	 * @param overWrite - set to true if you want to overwrite local file, if it exists
	 * 
	 * The "Response" event will be thrown when it is known if the
	 * file was transferred successfully or not. So, args[0] will be a boolean
	 */
	public void DownloadFile(final String dropBoxFileName, final String downloadPath,
			final ProgressListener listener, final boolean overWrite) {
		final DropBoxClient c = this;
		ThreadTimer.runOneTimeThread(new Runnable() {			
			@Override
			public void run() {
				String path = downloadPath;
				int lastIndex = dropBoxFileName.lastIndexOf("/");
				if (lastIndex == 0) {			
				} else {
					// Use the name of the file from dropbox for the download file			 
					// This is unnecessary I think.
					//String s;
					//if (downloadPath.endsWith("/")) {
					//	s = dropBoxFileName.substring(lastIndex + 1, dropBoxFileName.length());
					//} else {
					//	s = dropBoxFileName.substring(lastIndex, dropBoxFileName.length());
					//}
					//path = path + s; 
				}
				File dlFile = new File(path);
				if (dlFile.exists()) {
					if (overWrite) {
						dlFile.delete();
					} else {
						Log.e(TAG, "Download location exists, and overwrite is not set to true.");
						postResponse(c, false, "");
						return;
					}
				}
				prepareDbApi();		
				try {
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					@SuppressWarnings("unused")
					DropboxFileInfo info = dbApi.getFile(dropBoxFileName, null, bout, listener);			
					byte[] contents = bout.toByteArray();
					ByteArrayInputStream in = new ByteArrayInputStream(contents);
					//DropboxInputStream din = dbApi.getFileStream(dropBoxFileName, null);
					//dlFile.createNewFile();
					FileOutputStream out = new FileOutputStream(dlFile);
					byte[] buffer = new byte[1024];
					int bytesRead = 0;
					while ((bytesRead = in.read(buffer)) >= 0) {
						out.write(buffer, 0, bytesRead);
					}
					out.flush();
					out.close();
					in.close();
					postResponse(c, true, downloadPath);		
					return;
				} catch (DropboxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}		
				postResponse(c, false, "");
			}
		});
		
	}
	
	private AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;
		String tokens[] = getKeys();
		
		if (tokens != null) {
			AccessTokenPair accessToken = new AccessTokenPair(tokens[0], tokens[1]);
			session = new AndroidAuthSession(appKeyPair, access_type, accessToken);
		} else {
			session = new AndroidAuthSession(appKeyPair, access_type);
		}
		return session;
	}
	
	private void prepareDbApi() {
		String keys[] = getKeys();
		if (keys == null) {
			Log.e(TAG, "No stored tokens, please have the user login.");
			
		} else {
		
			String key = keys[0];
			String secret = keys[1];
			if (key.length() < 1 || secret.length() < 1) {
				Log.e(TAG, "Stored tokens are empty! Have the user login again.");
			}			
			AccessTokenPair access = new AccessTokenPair(key, secret);
			dbApi.getSession().setAccessTokenPair(access);
		}
	}
		
			
	@Override
	public void onResume() {
		if (dbApi != null) {
			if (dbApi.getSession().authenticationSuccessful()) {
				try {
	            // 	MANDATORY call to complete auth.
	            // 	Sets the access token on the session
					dbApi.getSession().finishAuthentication();
					
					AccessTokenPair tokens = dbApi.getSession().getAccessTokenPair();
	            
					// Store the tokens in this app's prefs file to grab later.					
	            
					storeKeys(tokens.key, tokens.secret);
					loggedIn = true;
					EventDispatcher.dispatchEvent(this, "LoginResponse", true);
				} catch (IllegalStateException e) {
					Log.i("DropBoxClient", "Error authenticating", e);
					EventDispatcher.dispatchEvent(this, "LoginResponse", false);
				}
			} 
		}
	}

	@Override
	public void onDestroy() {
		
	}

}
