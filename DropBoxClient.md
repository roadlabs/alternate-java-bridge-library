# DropBoxClient #

**Make sure to go to https://www.dropbox.com/developers and sign up. You will need to get an app key and app secret from them. The AltBridge also does NOT contain the dropbox jar file. You will need to get this from the dropbox site, and place it in your project's /libs folder.
You will also need the json\_simple jar file (json-simple-1.1.1.jar, or json-simple-1.1.jar will do) which can be downloaded here: http://code.google.com/p/json-simple/downloads/list**

```

// Instantiate the Dropbox Client with your dropbox app key and app secret.
DropBoxClient dbClient = new DropBoxClient(this, "MY_APP_KEY", "MY_APP_SECRET");

// Set the access type. Most of the time this will be APP_FOLDER, your
// other option is DROPBOX. See the DropBox documentation on the difference.
dbClient.AccessType(AccessType.APP_FOLDER);

// Have the user login, so the client can save the user's credentials
dbClient.Login();

// For file uploads/downloads, you must pass the absolute path of the file. 
// The first option, we are just taking the name of the file. This will
// put it in a folder with your app's name in the user's Apps folder.
// The second option is the absolute path of the file to upload.
// The third option allows you to attach a progress listener, or null
// if none desired. The last option is a boolean specifying if it should
// overwrite the file, if it exists.
File file = new File(getFilesDir(), "myfile.file");
dbClient.UploadFile(file.getName(), file.getAbsolutePath(), null, true);

// Download works the same way
dbClient.DownloadFile("filepathindropbox", file.getAbsolutePath(), null, true);

```

Both of the last two methods above throw the Response event, with a boolean signifying if the transer completed successfully or not. An error is also output to the logcat.

## Manifest Modification ##

Dropbox also requires that you also add the following to your project's AndroidManifest.xml file. Make sure to change the YOUR\_APP\_KEY\_HERE with your DropBox app key.

```
<activity
      android:name="com.dropbox.client2.android.AuthActivity"
      android:launchMode="singleTask"
      android:configChanges="orientation|keyboard">
      <intent-filter>
        <!-- Change this to be db- followed by your app key -->
        <data android:scheme="db-YOUR_APP_KEY_HERE" />        
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.BROWSABLE"/>
        <category android:name="android.intent.category.DEFAULT" />
      </intent-filter>
    </activity>
```