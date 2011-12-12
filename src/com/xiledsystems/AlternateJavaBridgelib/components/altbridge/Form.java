package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.OnInitializeListener;
import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Lists;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Maps;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Sets;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.MediaUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.SdkLevel;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.ViewUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.util.ErrorMessages;


/**
 * Component underlying activities and UI apps, not directly accessible to Simple programmers.
 *
 * <p>This is the root container of any Android activity and also the
 * superclass for for Simple/Android UI applications.
 *
 * The main form is always named "Screen1".
 *
 */

public class Form extends Activity
    implements Component, ComponentContainer, HandlesEventDispatching {
  /**
   * When {@code true}, attempts to set the title of this Activity via
   * {@link #setTitle(CharSequence)} will result in the new title being
   * <i>appended</i> to the existing title.
   * <p>
   * This is useful for debugging, as {@code setTitle()} can be used
   * like a debug print statement.
   */
  private static final boolean APPEND_TITLES = false;

  private static final String LOG_TAG = "Form";

  private static final String RESULT_NAME = "APP_INVENTOR_RESULT";

  private static final String ARGUMENT_NAME = "APP_INVENTOR_START";

  public static final String APPINVENTOR_URL_SCHEME = "appinventor";

  // Keep track of the current form object.
  // activeForm always holds the Form that is currently handling event dispatching so runtime.scm
  // can lookup symbols in the correct environment.
  private static Form activeForm;

  private final Handler androidUIHandler = new Handler();

  private String formName;

  private boolean screenInitialized;

  private static final int SWITCH_FORM_REQUEST_CODE = 1;
  private static int nextRequestCode = SWITCH_FORM_REQUEST_CODE + 1;

  // Backing for background color
  private int backgroundColor;

  private String backgroundImagePath = "";
  private Drawable backgroundDrawable;

  // Layout
  private Layout viewLayout;
  private FrameLayout frameLayout;
  private android.widget.LinearLayout xmlLayout;
  private boolean xmlUI=false;
  private boolean scrollable;

  // Application lifecycle related fields
  private final HashMap<Integer, ActivityResultListener> activityResultMap = Maps.newHashMap();
  private final Set<OnStopListener> onStopListeners = Sets.newHashSet();
  private final Set<OnDestroyListener> onDestroyListeners = Sets.newHashSet();
  private final Set<OnResumeListener> onResumeListeners = Sets.newHashSet();
  
  // AJB change - Set for the Initialize Listeners (mainly used for resizing components
  private final Set<OnInitializeListener> initializeListeners = Sets.newHashSet();

  // Set to the optional String-valued Extra passed in via an Intent on startup.
  private String startupValue = "";

  // To control volume of error complaints
  private static long minimumToastWait = 10000000000L; // 10 seconds
  private long lastToastTime = System.nanoTime() - minimumToastWait;

  // In a multiple screen application, when a secondary screen is opened, nextFormName is set to
  // the name of the secondary screen. It is saved so that it can be passed to the OtherScreenClosed
  // event.
  private String nextFormName;
  
  // ints for manipulating visible component sizes automatically -- AJB change
  public int scrnWidth;
  public int scrnHeight;
  

  @Override
  public void onCreate(Bundle icicle) {
    // Called when the activity is first created
    super.onCreate(icicle);

    // Figure out the name of this form.
    String className = getClass().getName();
    int lastDot = className.lastIndexOf('.');
    formName = className.substring(lastDot + 1);
    Log.d(LOG_TAG, "Form " + formName + " got onCreate");

    activeForm = this;
    Log.i(LOG_TAG, "activeForm is now " + activeForm.formName);

    viewLayout = new LinearLayout(this, ComponentConstants.LAYOUT_ORIENTATION_VERTICAL);

    // Default property values
    Scrollable(true); // frameLayout is created in Scrollable()
    BackgroundColor(Component.COLOR_WHITE);

    // Get startup text if any before adding components
    Intent startIntent = getIntent();
    if (startIntent != null && startIntent.hasExtra(ARGUMENT_NAME)) {
      startupValue = startIntent.getStringExtra(ARGUMENT_NAME);
    }

    // Add application components to the form
    $define();

    // Special case for Event.Initialize(): all other initialize events are triggered after
    // completing the constructor. This doesn't work for Android apps though because this method
    // is called after the constructor completes and therefore the Initialize event would run
    // before initialization finishes. Instead the compiler suppresses the invocation of the
    // event and leaves it up to the library implementation.
    Initialize();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);

    final int newOrientation = newConfig.orientation;
    if (newOrientation == Configuration.ORIENTATION_LANDSCAPE ||
        newOrientation == Configuration.ORIENTATION_PORTRAIT) {
      // At this point, the screen has not be resized to match the new orientation.
      // We use Handler.post so that we'll dispatch the ScreenOrientationChanged event after the
      // screen has been resized to match the new orientation.

      androidUIHandler.post(new Runnable() {
        public void run() {
          boolean dispatchEventNow = false;
          if (frameLayout != null) {
            if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {
              if (frameLayout.getWidth() >= frameLayout.getHeight()) {
                dispatchEventNow = true;
              }
            } else { // Portrait
              if (frameLayout.getHeight() >= frameLayout.getWidth()) {
                dispatchEventNow = true;
              }
            }
          }
          if (xmlUI) {
        	  if (xmlLayout != null) {
        		  if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {
        			  if (xmlLayout.getWidth() >= xmlLayout.getHeight()) {
        				  dispatchEventNow = true;
        			  }
        		  } else { // Portrait
        			  if (xmlLayout.getHeight() >= xmlLayout.getWidth()) {
        				  dispatchEventNow = true;
        			  }
        		  }
        	  }
          }
          if (dispatchEventNow) {
            ScreenOrientationChanged();
          } else {
            // Try again later.
            androidUIHandler.post(this);
          }
        }
      });
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(LOG_TAG, "Form " + formName + " got onActivityResult, requestCode = " +
        requestCode + ", resultCode = " + resultCode);
    if (requestCode == SWITCH_FORM_REQUEST_CODE) {
      // In a multiple screen application, a secondary screen has closed.
      String result;
      if (data != null && data.hasExtra(RESULT_NAME)) {
        result = data.getStringExtra(RESULT_NAME);
      } else {
        result = "";
      }
      OtherScreenClosed(nextFormName, result);

    } else {
      // Another component (such as a ListPicker, ActivityStarter, etc) is expecting this result.
      ActivityResultListener component = activityResultMap.get(requestCode);
      if (component != null) {
        component.resultReturned(requestCode, resultCode, data);
      }
    }
  }
  
  // AJB change - Add way to grab hold of the main UIs handler to post runnables
  
  public Handler getHandler() {
		
		return androidUIHandler;
		
	}
  
  public void post(Runnable action) {
		androidUIHandler.post(action);
	}

  public int registerForActivityResult(ActivityResultListener listener) {
    int requestCode = generateNewRequestCode();
    activityResultMap.put(requestCode, listener);
    return requestCode;
  }

  public void unregisterForActivityResult(ActivityResultListener listener) {
    List<Integer> keysToDelete = Lists.newArrayList();
    for (Map.Entry<Integer, ActivityResultListener> mapEntry : activityResultMap.entrySet()) {
      if (listener.equals(mapEntry.getValue())) {
        keysToDelete.add(mapEntry.getKey());
      }
    }
    for (Integer key : keysToDelete) {
      activityResultMap.remove(key);
    }
  }

  private static int generateNewRequestCode() {
    return nextRequestCode++;
  }


  @Override
  protected void onResume() {
    super.onResume();
    Log.d(LOG_TAG, "Form " + formName + " got onResume");
    activeForm = this;
    for (OnResumeListener onResumeListener : onResumeListeners) {
      onResumeListener.onResume();
    }
  }

  public void registerForOnResume(OnResumeListener component) {
    onResumeListeners.add(component);
  }
  
  public void registerForOnDestroy(OnDestroyListener component) {
	  onDestroyListeners.add(component);
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.d(LOG_TAG, "Form " + formName + " got onStop");
    for (OnStopListener onStopListener : onStopListeners) {
      onStopListener.onStop();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    // for debugging and future growth
    Log.d(LOG_TAG, "Form " + formName + " got onPause");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // for debugging and future growth
    Log.d(LOG_TAG, "Form " + formName + " got onDestroy");
    for (OnDestroyListener onDestroyListener : onDestroyListeners) {
    	onDestroyListener.onDestroy();
    }

    // Unregister events for components in this form.
    EventDispatcher.removeDispatchDelegate(this);
  }

  public void registerForOnStop(OnStopListener component) {
    onStopListeners.add(component);
  }

  /**
   * Compiler-generated method to initialize and add application components to
   * the form.  We just provide an implementation here to artificially make
   * this class concrete so that it is included in the documentation and
   * Codeblocks language definition file generated by
   * {@link com.google.devtools.simple.scripts.DocumentationGenerator} and
   * {@link com.google.devtools.simple.scripts.LangDefXmlGenerator},
   * respectively.  The actual implementation appears in {@code runtime.scm}.
   */
  void $define() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean canDispatchEvent(Component component, String eventName) {
    // Events can only be dispatched after the screen initialized event has completed.
    boolean canDispatch = screenInitialized ||
        (component == this && eventName.equals("Initialize"));

    if (canDispatch) {
      // Set activeForm to this before the event is dispatched.
      // runtime.scm will call getActiveForm() when the event handler executes.
      activeForm = this;
    }

    return canDispatch;
  }

  /**
   * A trivial implementation to artificially make this class concrete so
   * that it is included in the documentation and
   * Codeblocks language definition file generated by
   * {@link com.google.devtools.simple.scripts.DocumentationGenerator} and
   * {@link com.google.devtools.simple.scripts.LangDefXmlGenerator},
   * respectively.  The actual implementation appears in {@code runtime.scm}.
   */
  @Override
  public boolean dispatchEvent(Component component, String componentName, String eventName,
      Object[] args) {
    throw new UnsupportedOperationException();
  }


  /**
   * Initialize event handler.
   */
  
  public void Initialize() {
    // Dispatch the Initialize event only after the screen's width and height are no longer zero.
    if (!xmlUI) {
    	androidUIHandler.post(new Runnable() {    	
    		public void run() {
    			if (frameLayout != null && frameLayout.getWidth() != 0 && frameLayout.getHeight() != 0) {
    				setScrnVars();
    				for (OnInitializeListener oninitializelistener : initializeListeners) {
    					oninitializelistener.onInitialize();
    				}
    				EventDispatcher.dispatchEvent(Form.this, "Initialize");
    				screenInitialized = true;
    			} else {
          // 	Try again later.
    				androidUIHandler.post(this);
    			}
    		}
    	});
    } else {
    	androidUIHandler.post(new Runnable() {    	
    		public void run() {
    			if (xmlLayout != null && xmlLayout.getWidth() != 0 && xmlLayout.getHeight() != 0) {
    				setScrnVars();
    				for (OnInitializeListener oninitializelistener : initializeListeners) {
    					oninitializelistener.onInitialize();
    				}
    				EventDispatcher.dispatchEvent(Form.this, "Initialize");
    				screenInitialized = true;
    			} else {
          // 	Try again later.
    				androidUIHandler.post(this);
    			}
    		}
    	});
    }
  }

  public void registerForOnInitialize(OnInitializeListener component) {
		initializeListeners.add(component);
	}
  
  public void setScrnVars() {
		
		Display display = getWindowManager().getDefaultDisplay();
		scrnWidth = display.getWidth();
		scrnHeight = display.getHeight();
		
	}
  
  public void ScreenOrientationChanged() {
    EventDispatcher.dispatchEvent(this, "ScreenOrientationChanged");
  }

  /**
   * ErrorOccurred event handler.
   */
  
  public void ErrorOccurred(Component component, String functionName, int errorNumber,
      String message) {
    String componentType = component.getClass().getName();
    componentType = componentType.substring(componentType.lastIndexOf(".") + 1);
    Log.e(LOG_TAG, "Form " + formName + " ErrorOccurred, errorNumber = " + errorNumber +
        ", componentType = " + componentType + ", functionName = " + functionName +
        ", messages = " + message);
    if ((!(EventDispatcher.dispatchEvent(
        this, "ErrorOccurred", component, functionName, errorNumber, message)))
        && screenInitialized)  {
      // If dispatchEvent returned false, then no user-supplied error handler was run.
      // If in addition, the screen initializer was run, then we assume that the
      // user did not provide an error handler.   In this case, we run a default
      // error handler, namely, showing a notification to the end user of the app.
      // The app writer can override this by providing an error handler.
      new Notifier(this).ShowAlert("Error " + errorNumber + ": " + message);
    }
  }


  public void dispatchErrorOccurredEvent(final Component component, final String functionName,
      final int errorNumber, final Object... messageArgs) {
    runOnUiThread(new Runnable() {
      public void run() {
        String message = ErrorMessages.formatMessage(errorNumber, messageArgs);
        ErrorOccurred(component, functionName, errorNumber, message);
      }
    });
  }

  /**
   * Scrollable property getter method.
   *
   * @return  true if the screen is vertically scrollable
   */
  
  public boolean Scrollable() {
    return scrollable;
  }
  
  
  /**
   * Scrollable property setter method.
   *
   * @param scrollable  true if the screen should be vertically scrollable
   */
  
  public void Scrollable(boolean scrollable) {
	  if (!xmlUI) {
		  if (this.scrollable == scrollable && frameLayout != null) {
			  return;
		  }

    // 	Remove our view from the current frameLayout.
		  if (frameLayout != null) {
			  frameLayout.removeAllViews();
		  }

		  this.scrollable = scrollable;

		  frameLayout = scrollable ? new ScrollView(this) : new FrameLayout(this);
		  frameLayout.addView(viewLayout.getLayoutManager(), new ViewGroup.LayoutParams(
				  ViewGroup.LayoutParams.FILL_PARENT,
				  ViewGroup.LayoutParams.FILL_PARENT));

		  frameLayout.setBackgroundColor(backgroundColor);
		  if (backgroundDrawable != null) {
			  ViewUtil.setBackgroundImage(frameLayout, backgroundDrawable);
		  }
		  setContentView(frameLayout);
		  frameLayout.requestLayout();
	  }
  }
  
  @Override
  public void setContentView(int layoutresourceId) {
	  super.setContentView(layoutresourceId);
	  int temp = getResources().getIdentifier("formlayout1", "id", getPackageName());
	  xmlUI = true;
	  
	  frameLayout = null;
	  xmlLayout = (android.widget.LinearLayout) findViewById(temp);
	  backgroundColor = xmlLayout.getSolidColor();
	  this.scrollable = false;
	  xmlLayout.requestLayout();
	  /*
	  frameLayout.setBackgroundColor(backgroundColor);
	    if (backgroundDrawable != null) {
	      ViewUtil.setBackgroundImage(frameLayout, backgroundDrawable);
	    }
	    */
	  //backgroundColor = frameLayout.getSolidColor();
	  //frameLayout.requestLayout();
	  
  }

  /**
   * BackgroundColor property getter method.
   *
   * @return  background RGB color with alpha
   */
  
  public int BackgroundColor() {
    return backgroundColor;
  }

  /**
   * BackgroundColor property setter method.
   *
   * @param argb  background RGB color with alpha
   */
  
  public void BackgroundColor(int argb) {
    backgroundColor = argb;
    if (argb != Component.COLOR_DEFAULT) {
    	if (!xmlUI) {
    		viewLayout.getLayoutManager().setBackgroundColor(argb);
    		// Just setting the background color on the layout manager is insufficient.
    		frameLayout.setBackgroundColor(argb);
    	} else {
    		xmlLayout.setBackgroundColor(argb);
    	}
    } else {
    	if (!xmlUI) {
    		viewLayout.getLayoutManager().setBackgroundColor(Component.COLOR_WHITE);
    		// Just setting the background color on the layout manager is insufficient.
    		frameLayout.setBackgroundColor(Component.COLOR_WHITE);
    	} else {
    	xmlLayout.setBackgroundColor(Component.COLOR_WHITE);
    	}
    }
  }

  /**
   * Returns the path of the background image.
   *
   * @return  the path of the background image
   */
  
  public String BackgroundImage() {
    return backgroundImagePath;
  }


  /**
   * Specifies the path of the background image.
   *
   * <p/>See {@link MediaUtil#determineMediaSource} for information about what
   * a path can be.
   *
   * @param path the path of the background image
   */
  
  public void BackgroundImage(String path) {
    backgroundImagePath = (path == null) ? "" : path;

    try {
      backgroundDrawable = MediaUtil.getDrawable(this, backgroundImagePath);
    } catch (IOException ioe) {
      Log.e(LOG_TAG, "Unable to load " + backgroundImagePath);
      backgroundDrawable = null;
    }

    if (!xmlUI) {
    	ViewUtil.setBackgroundImage(frameLayout, backgroundDrawable);
    	frameLayout.invalidate();
    } else {
    	ViewUtil.setBackgroundImage(xmlLayout, backgroundDrawable);
    	xmlLayout.invalidate();
    }
  }

  /**
   * Title property getter method.
   *
   * @return  form caption
   */
  
  public String Title() {
    return getTitle().toString();
  }

  /**
   * Title property setter method: sets a new caption for the form in the
   * form's title bar.
   *
   * @param title  new form caption
   */
  
  public void Title(String title) {
    setTitle(title);
  }

  /**
   * ScreenOrientation property getter method.
   *
   * @return  screen orientation
   */
  
  public String ScreenOrientation() {
    switch (getRequestedOrientation()) {
      case ActivityInfo.SCREEN_ORIENTATION_BEHIND:
        return "behind";
      case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
        return "landscape";
      case ActivityInfo.SCREEN_ORIENTATION_NOSENSOR:
        return "nosensor";
      case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
        return "portrait";
      case ActivityInfo.SCREEN_ORIENTATION_SENSOR:
        return "sensor";
      case ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED:
        return "unspecified";
      case ActivityInfo.SCREEN_ORIENTATION_USER:
        return "user";
      case 10: // ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        return "fullSensor";
      case 8: // ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        return "reverseLandscape";
      case 9: // ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
        return "reversePortrait";
      case 6: // ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        return "sensorLandscape";
      case 7: // ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        return "sensorPortrait";
    }

    return "unspecified";
  }

  /**
   * ScreenOrientation property setter method: sets the screen orientation for
   * the form.
   *
   * @param screenOrientation  the screen orientation as a string
   */
  
  public void ScreenOrientation(String screenOrientation) {
    if (screenOrientation.equalsIgnoreCase("behind")) {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);
    } else if (screenOrientation.equalsIgnoreCase("landscape")) {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    } else if (screenOrientation.equalsIgnoreCase("nosensor")) {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
    } else if (screenOrientation.equalsIgnoreCase("portrait")) {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    } else if (screenOrientation.equalsIgnoreCase("sensor")) {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    } else if (screenOrientation.equalsIgnoreCase("unspecified")) {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    } else if (screenOrientation.equalsIgnoreCase("user")) {
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    } else if (SdkLevel.getLevel() >= SdkLevel.LEVEL_GINGERBREAD) {
      if (screenOrientation.equalsIgnoreCase("fullSensor")) {
        setRequestedOrientation(10); // ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
      } else if (screenOrientation.equalsIgnoreCase("reverseLandscape")) {
        setRequestedOrientation(8); // ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
      } else if (screenOrientation.equalsIgnoreCase("reversePortrait")) {
        setRequestedOrientation(9); // ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
      } else if (screenOrientation.equalsIgnoreCase("sensorLandscape")) {
        setRequestedOrientation(6); // ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
      } else if (screenOrientation.equalsIgnoreCase("sensorPortrait")) {
        setRequestedOrientation(7); // ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
      } else {
        dispatchErrorOccurredEvent(this, "ScreenOrientation",
            ErrorMessages.ERROR_INVALID_SCREEN_ORIENTATION, screenOrientation);
      }
    } else {
      dispatchErrorOccurredEvent(this, "ScreenOrientation",
          ErrorMessages.ERROR_INVALID_SCREEN_ORIENTATION, screenOrientation);
    }
  }

  /**
   * Specifies the name of the application icon.
   *
   * @param name the name of the application icon
   */
  
  public void Icon(String name) {
    // We don't actually need to do anything.
  }

  /**
   * Width property getter method.
   *
   * @return  width property used by the layout
   */
  
  public int Width() {
	  if (!xmlUI) {
		  return frameLayout.getWidth();
	  } else {
		  return xmlLayout.getWidth();
	  }
  }

  /**
   * Height property getter method.
   *
   * @return  height property used by the layout
   */
  
  public int Height() {
	  if (!xmlUI) {
		  return frameLayout.getHeight();
	  } else {
		  return xmlLayout.getHeight();
	  }
  }

  /**
   * Display a new form.
   *
   * @param nextFormName the name of the new form to display
   */
  // This is called from runtime.scm when a "open another screen" block is executed.
  public static void switchForm(String nextFormName) {
    if (activeForm != null) {
      activeForm.startNewForm(nextFormName, null);
    } else {
      throw new IllegalStateException("activeForm is null");
    }
  }

  /**
   * Display a new form and pass a startup value to the new form.
   *
   * @param nextFormName the name of the new form to display
   * @param startupValue the startup value to pass to the new form
   */
  // This is called from runtime.scm when a "open another screen with start text" block is
  // executed.
  public static void switchFormWithStartupValue(String nextFormName, String startupValue) {
    if (activeForm != null) {
      activeForm.startNewForm(nextFormName, startupValue);
    } else {
      throw new IllegalStateException("activeForm is null");
    }
  }

  protected void startNewForm(String nextFormName, String startupValue) {
    Intent activityIntent = new Intent();
    // Note that the following is dependent on form generated class names being the same as
    // their form names and all forms being in the same package.
    activityIntent.setClassName(this, getPackageName() + "." + nextFormName);
    if (startupValue != null) {
      activityIntent.putExtra(ARGUMENT_NAME, startupValue);
    }
    // Save the nextFormName so that it can be passed to the OtherScreenClosed event in the
    // future.
    this.nextFormName = nextFormName;
    try {
      startActivityForResult(activityIntent, SWITCH_FORM_REQUEST_CODE);
    } catch (ActivityNotFoundException e) {
      String functionName = (startupValue == null) ? "open another screen" :
          "open another screen with start text";
      dispatchErrorOccurredEvent(this, functionName,
          ErrorMessages.ERROR_SCREEN_NOT_FOUND, nextFormName);
    }
  }

  
  public void OtherScreenClosed(String otherScreenName, String result) {
    Log.i(LOG_TAG, "Form " + formName + " OtherScreenClosed, otherScreenName = " + otherScreenName +
        ", result = " + result);
    EventDispatcher.dispatchEvent(this, "OtherScreenClosed", otherScreenName, result);
  }


  // Component implementation

  @Override
  public HandlesEventDispatching getDispatchDelegate() {
    return this;
  }

  // ComponentContainer implementation

  @Override
  public Activity $context() {
    return this;
  }

  @Override
  public Form $form() {
    return this;
  }

  @Override
  public void $add(AndroidViewComponent component) {
    viewLayout.add(component);
  }

  @Override
  public void setChildWidth(AndroidViewComponent component, int width) {
    // A form is a vertical layout.
    ViewUtil.setChildWidthForVerticalLayout(component.getView(), width);
  }

  @Override
  public void setChildHeight(AndroidViewComponent component, int height) {
    // A form is a vertical layout.
    ViewUtil.setChildHeightForVerticalLayout(component.getView(), height);
  }

  @Override
  public void setTitle(CharSequence newTitle) {
    if (APPEND_TITLES) {
      CharSequence oldTitle = super.getTitle();
      if (oldTitle != null) {
        super.setTitle(oldTitle + ", " + newTitle);
      } else {
        super.setTitle(newTitle);
      }
    } else {
      super.setTitle(newTitle);
    }
  }

  /*
   * This is called from runtime.scm at the beginning of each event handler.
   * It allows runtime.scm to know which form environment should be used for
   * looking up symbols. The active form is the form that is currently
   * (or was most recently) dispatching an event.
   */
  public static Form getActiveForm() {
    return activeForm;
  }

  // This is called from runtime.scm when a "get start text" block is executed.
  public static String getStartupValue() {
    if (activeForm != null) {
      return activeForm.startupValue;
    } else {
      throw new IllegalStateException("activeForm is null");
    }
  }

  // This is called from runtime.scm when a "close screen" block is executed.
  public static void finishActivity() {
    if (activeForm != null) {
      activeForm.finish();
    } else {
      throw new IllegalStateException("activeForm is null");
    }
  }

  // This is called from runtime.scm when a "close screen with result" block is executed.
  public static void finishActivityWithResult(String result) {
    if (activeForm != null) {
      Intent resultIntent = new Intent();
      resultIntent.putExtra(RESULT_NAME, result);
      activeForm.setResult(Activity.RESULT_OK, resultIntent);
      activeForm.finish();
    } else {
      throw new IllegalStateException("activeForm is null");
    }
  }

  // This is called from runtime.scm when a "close application" block is executed.
  public static void finishApplication() {
    if (activeForm != null) {
      activeForm.finish();
      // I know that this is frowned upon in Android circles but I really think that it's
      // confusing to users if the exit button doesn't really stop everything, including other
      // forms in the app (when we support them), non-UI threads, etc.  We might need to be
      // careful about this is we ever support services that start up on boot (since it might
      // mean that the only way to restart that service) is to reboot but that's a long way off.
      System.exit(0);
    } else {
      throw new IllegalStateException("activeForm is null");
    }
  }

  // Configure the system menu to include a button to kill the application

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // This procedure is called only once.  To change the items dynamically
    // we would use onPrepareOptionsMenu.
    super.onCreateOptionsMenu(menu);
    // add the menu items
    // Comment out the next line if we don't want the exit button
    addExitButtonToMenu(menu);
    return true;
  }

  public void addExitButtonToMenu(Menu menu) {
    MenuItem stopApplicationItem = menu.add(Menu.NONE, Menu.NONE, Menu.FIRST,
    "Stop this application")
    .setOnMenuItemClickListener(new OnMenuItemClickListener() {
      public boolean onMenuItemClick(MenuItem item) {
        showExitApplicationNotification();
        return true;
      }
    });
    stopApplicationItem.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
  }

  private void showExitApplicationNotification() {
    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    alertDialog.setTitle("Stop application?");
    // prevents the user from escaping the dialog by hitting the Back button
    alertDialog.setCancelable(false);
    alertDialog.setMessage("Stop this application and exit?  You'll need to relaunch " +
    "the application to use it again.");
    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Stop and exit",
        new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        finishApplication();
      }});
    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Don't stop",
        new DialogInterface.OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        // nothing to do here
      }
    });
    alertDialog.show();
  }

  public void clear() {
    viewLayout.getLayoutManager().removeAllViews();
    screenInitialized = false;
  }

  public void deleteComponent(Object component) {
    if (component instanceof Deleteable) {
      ((Deleteable) component).onDelete();
    }
  }

  public void dontGrabTouchEventsForComponent() {
    // The following call results in the Form not grabbing our events and
    // handling dragging on its own, which it wants to do to handle scrolling.
    // Its effect only lasts long as the current set of motion events
    // generated during this touch and drag sequence.  Consequently, if a
    // component wants to handle dragging it needs to call this in the
    // onTouchEvent of its View.
	  if (!xmlUI) {
		  frameLayout.requestDisallowInterceptTouchEvent(true);
	  } else {
		  xmlLayout.requestDisallowInterceptTouchEvent(true);
	  }
  }


  // This is used by Repl to throttle error messages which can get out of
  // hand, e.g. if triggered by Accelerometer.
  protected boolean toastAllowed() {
    long now = System.nanoTime();
    if (now > lastToastTime + minimumToastWait) {
      lastToastTime = now;
      return true;
    }
    return false;
  }

  // This is used by runtime.scm to call the Initialize of a component.
  public void callInitialize(Object component) throws Throwable {
    Method method;
    try {
      method = component.getClass().getMethod("Initialize", (Class<?>[]) null);
    } catch (SecurityException e) {
      Log.d(LOG_TAG, "Security exception " + e.getMessage());
      return;
    } catch (NoSuchMethodException e) {
      //This is OK.
      return;
    }
    try {
      Log.d(LOG_TAG, "calling Initialize method for Object " + component.toString());
      method.invoke(component, (Object[]) null);
    } catch (InvocationTargetException e){
      Log.d(LOG_TAG, "invoke exception: " + e.getMessage());
      throw e.getTargetException();
    }
  }

}