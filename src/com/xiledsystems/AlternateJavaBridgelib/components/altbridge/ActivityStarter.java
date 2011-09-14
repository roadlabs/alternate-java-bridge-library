package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.util.ErrorMessages;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

/**
 * Implementation of a general Android Activity component.
 *
 */

public class ActivityStarter extends AndroidNonvisibleComponent
    implements ActivityResultListener, Component, Deleteable {

  private String action;
  private String dataUri;
  private String dataType;
  private String activityPackage;
  private String activityClass;
  private String extraKey;
  private String extraValue;
  private String resultName;
  private Intent resultIntent;
  private String result;
  private int requestCode;
  private final ComponentContainer container;

  /**
   * Creates a new ActivityStarter component.
   *
   * @param container  container, kept for access to form and context
   */
  public ActivityStarter(ComponentContainer container) {
    super(container.$form());
    // Save the container for later
    this.container = container;
    result = "";
    Action(Intent.ACTION_MAIN);
    ActivityPackage("");
    ActivityClass("");
    DataUri("");
    DataType("");
    ExtraKey("");
    ExtraValue("");
    ResultName("");
  }

  /**
   * Returns the action that will be used to start the activity.
   */
  
  public String Action() {
    return action;
  }

  /**
   * Specifies the action that will be used to start the activity.
   */
  
  public void Action(String action) {
    this.action = action.trim();
  }

  // TODO(user) - currently we support just one extra name/value pair that will be passed to
  // the activity. The user specifies the ExtraKey and ExtraValue properties.
  // We should allow more extra name/value pairs, but we'd need a different interface with regard
  // to properties and functions.
  // In the documentation for Intent, they use the term "name", not "key", and we might want to use
  // the term "name", also.
  // There are backwards compatibility issues with removing the ExtraKey and ExtraValue properties.
  // Also, while extra names are always Strings, the values can be other types. We'd need to know
  // the correct type of the value in order to call the appropriate Intent.putExtra method.
  // Adding multiple functions like PutStringExtra, PutStringArrayExtra, PutCharExtra,
  // PutCharArrayExtra, PutBooleanExtra, PutBooleanArrayExtra, PutByteExtra, PutByteArrayExtra,
  // PutShortExtra, PutShortArrayExtra, PutIntExtra, PutIntArrayExtra, PutLongExtra,
  // PutLongArrayExtra, PutFloatExtra, PutFloatArrayExtra, PutDoubleExtra, PutDoubleArrayExtra,
  // etc, seems like a bad idea.

  /**
   * Returns the extra key that will be passed to the activity.
   */
  
  public String ExtraKey() {
    return extraKey;
  }

  /**
   * Specifies the extra key that will be passed to the activity.
   */
  
  public void ExtraKey(String extraKey) {
    this.extraKey = extraKey.trim();
  }


  /**
   * Returns the extra value that will be passed to the activity.
   */
  
  public String ExtraValue() {
    return extraValue;
  }

  /**
   * Specifies the extra value that will be passed to the activity.
   */
  
  public void ExtraValue(String extraValue) {
    this.extraValue = extraValue.trim();
  }

  // TODO(user) - currently we support retrieving just one string extra result from the
  // activity. The user specifies the ResultName property and, then after the activity finishes,
  // the string extra result corresponding to ResultName is passed as the result parameter to the
  // AfterActivity event and is also available from the Result property getter.
  // We should allow access to more extra results, but we'd need a different interface with regard
  // to properties, functions, and events parameters.
  // There are backwards compatibility issues with removing the AfterActivity event's result
  // parameter and the Result property.
  // Also, while extra names are always Strings, the values can be other types. We'd need to know
  // the correct type of the value in order to call the appropriate Intent.get...Extra method.
  // Adding multiple functions like GetStringExtra, GetStringArrayExtra, GetCharExtra,
  // GetCharArrayExtra, GetBooleanExtra, GetBooleanArrayExtra, GetByteExtra, GetByteArrayExtra,
  // GetShortExtra, GetShortArrayExtra, GetIntExtra, GetIntArrayExtra, GetLongExtra,
  // GetLongArrayExtra, GetFloatExtra, GetFloatArrayExtra, GetDoubleExtra, GetDoubleArrayExtra,
  // etc, seems like a bad idea.

  /**
   * Returns the name that will be used to retrieve a result from the activity.
   */
  
  public String ResultName() {
    return resultName;
  }

  /**
   * Specifies the name that will be used to retrieve a result from the
   * activity.
   */
  
  public void ResultName(String resultName) {
    this.resultName = resultName.trim();
  }

  /**
   * Returns the result from the activity.
   */
  
  public String Result() {
    return result;
  }

  /**
   * Returns the data URI that will be used to start the activity.
   */
  
  public String DataUri() {
    return dataUri;
  }

  /**
   * Specifies the data URI that will be used to start the activity.
   */
  
  public void DataUri(String dataUri) {
    this.dataUri = dataUri.trim();
  }

  /**
   * Returns the MIME type to pass to the activity.
   */
  
  public String DataType() {
    return dataType;
  }

  /**
   * Specifies the MIME type to pass to the activity.
   */
  
  public void DataType(String dataType) {
    this.dataType = dataType.trim();
  }

  /**
   * Returns the package part of the specific component that will be started.
   */
  
  public String ActivityPackage() {
    return activityPackage;
  }

  /**
   * Specifies the package part of the specific component that will be started.
   */
  
  public void ActivityPackage(String activityPackage) {
    this.activityPackage = activityPackage.trim();
  }

  /**
   * Returns the class part of the specific component that will be started.
   */
  
  public String ActivityClass() {
    return activityClass;
  }

  /**
   * Specifies the class part of the specific component that will be started.
   */
  
  public void ActivityClass(String activityClass) {
    this.activityClass = activityClass.trim();
  }

  
  public void AfterActivity(String result) {
    EventDispatcher.dispatchEvent(this, "AfterActivity", result);
  }

  /**
   * Returns the MIME type from the activity.
   */
  
  public String ResultType() {
    if (resultIntent != null) {
      String resultType = resultIntent.getType();
      if (resultType != null) {
        return resultType;
      }
    }
    return "";
  }

  /**
   * Returns the URI from the activity.
   */
  
  public String ResultUri() {
    if (resultIntent != null) {
      String resultUri = resultIntent.getDataString();
      if (resultUri != null) {
        return resultUri;
      }
    }
    return "";
  }


  /**
   * Returns the name of the activity that corresponds to this ActivityStarer,
   * or an empty string if no corresponding activity can be found.
   */
  
  public String ResolveActivity() {
    Intent intent = buildActivityIntent();
    PackageManager pm = container.$context().getPackageManager();
    ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
    if (resolveInfo != null && resolveInfo.activityInfo != null) {
      return resolveInfo.activityInfo.name;
    }
    return "";
  }

  /**
   * Start the activity.
   */
  
  public void StartActivity() {
    resultIntent = null;
    result = "";

    Intent intent = buildActivityIntent();

    if (requestCode == 0) {
      // First time, we need to register this as an ActivityResultListener with the Form.
      // The Form's onActivityResult method will be called when the activity returns. If we
      // register with the Form and then use the requestCode when we start an activity, the Form
      // will call our resultReturned method.
      requestCode = form.registerForActivityResult(this);
    }

    try {
      container.$context().startActivityForResult(intent, requestCode);
    } catch (ActivityNotFoundException e) {
      form.dispatchErrorOccurredEvent(this, "StartActivity",
          ErrorMessages.ERROR_ACTIVITY_STARTER_NO_CORRESPONDING_ACTIVITY);
    }
  }

  private Intent buildActivityIntent() {
    Uri uri = (dataUri.length() != 0) ? Uri.parse(dataUri) : null;
    Intent intent = (uri != null) ? new Intent(action, uri) : new Intent(action);

    if (dataType.length() != 0) {
      if (uri != null) {
        intent.setDataAndType(uri, dataType);
      } else {
        intent.setType(dataType);
      }
    }

    if (activityPackage.length() != 0 || activityClass.length() != 0) {
      ComponentName component = new ComponentName(activityPackage, activityClass);
      intent.setComponent(component);
    }

    if (extraKey.length() != 0 && extraValue.length() != 0) {
      intent.putExtra(extraKey, extraValue);
    }

    return intent;
  }

  @Override
  public void resultReturned(int requestCode, int resultCode, Intent data) {
    if (requestCode == this.requestCode) {
      Log.i("ActivityStarter", "resultReturned - resultCode = " + resultCode);
      if (resultCode == Activity.RESULT_OK) {
        resultIntent = data;
        if (resultName.length() != 0 && resultIntent != null &&
            resultIntent.hasExtra(resultName)) {
          result = resultIntent.getStringExtra(resultName);
        } else {
          result = "";
        }
        // call user's AfterActivity event handler
        AfterActivity(result);
      }
    }
  }

  
  public void ActivityError(String message) {
  }

  // Deleteable implementation

  @Override
  public void onDelete() {
    form.unregisterForActivityResult(this);
  }
}
