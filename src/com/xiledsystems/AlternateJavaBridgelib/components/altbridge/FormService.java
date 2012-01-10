package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.SvcComponentContainer;
import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.Sets;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import com.xiledsystems.AlternateJavaBridgelib.components.util.ErrorMessages;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;
import android.os.HandlerThread;
import android.os.Process;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Most of this was copied from Java Bridge's Form class. Lots of stuff was left out, as this is a service,
 * which doesn't have a UI. Some modifications had to be made to adjust the "form" to a service instead
 * of an activity. I've also added the option to have the service process information through a thread.
 * If you need to use a thread (should only really use it for CPU intensive operations), then the service
 * needs to have the stickyVal set to START_STICKY. Then, you can capture the event the thread runs in
 * with "ThreadRunning". You also have to set the UseThread boolean to true. (this.UseThread(true);)
 * 
 * Ryan Bis - www.xiledsystems.com
 *
 */

public class FormService extends Service implements Component, SvcComponentContainer, HandlesEventDispatching {

	private static FormService activeFormService;
	private int stickyVal=START_NOT_STICKY;
	private static final String LOG_TAG = "FormService";
	public boolean isRunning=false;
	private String formServiceName;
	private final Handler serviceHandler = new Handler();
	private final Set<OnStartCommandListener> onStartCommandListeners = Sets.newHashSet();
	private final Set<OnDestroySvcListener> onDestroyListeners = Sets.newHashSet();
	private ServiceHandler mServiceHandler;
	private Looper mServiceLooper;
	private boolean useThread;
	
	// Handler that receives messages from the thread
	  private final class ServiceHandler extends Handler {
	      public ServiceHandler(Looper looper) {
	          super(looper);
	      }
	      @Override
	      public void handleMessage(Message msg) {
	          // We will leave this blank. Put the event here to process in the thread.
	          // 
	          EventDispatcher.dispatchEvent(FormService.this, "ThreadRunning");
	          
	          // Stop the service using the startId, so that we don't stop
	          // the service in the middle of handling another job
	          // Leave it to the one thread for now.
	          Log.d(LOG_TAG, "FormService " + formServiceName + " thread is stopping.");
	          stopSelf(msg.arg1);
	          
	      }
	  }
	
	@Override
    public void onCreate() {
       super.onCreate();
       
       // Figure out the name of this FormService
       String className = getClass().getName();
       int lastDot = className.lastIndexOf(".");
       formServiceName = className.substring(lastDot + 1);
       Log.d(LOG_TAG, "FormService " + formServiceName + " got onCreate");
       
       activeFormService = this;
       Log.i(LOG_TAG, "active FormService is now "+activeFormService.formServiceName);
       
      
       $define();
       Initialize();
    }
	
	void $define() {
	    throw new UnsupportedOperationException();
	  }	
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "FormService "+ formServiceName + " got onStartCommand" );
        activeFormService = this;
        for (OnStartCommandListener onStartCommandListener : onStartCommandListeners) {
        	onStartCommandListener.onStartCommand();
        }
        EventDispatcher.dispatchEvent(FormService.this, "onStartCommand", intent);
        // This only applies if the thread has started. The thread won't start unless stickyVal is set
        // to START_STICKY. This way, we don't have a thread running for no reason. Most services probably
        // won't need to use a seperate thread, but it's nice to have in case you run into a case where
        // you do need to use it. The user must also set the useThread boolean to true.
        if (stickyVal==START_STICKY && useThread){
        	Message msg = mServiceHandler.obtainMessage();
        	msg.arg1 = startId;
        	mServiceHandler.sendMessage(msg);
        }
       
        return stickyVal;
    }
	
	public void setStickyVal(int i) {
		stickyVal=i;
	}
	
	public void registerForOnStartCommand(OnStartCommandListener component) {
		onStartCommandListeners.add(component);
	}
	
	public void UseThread(boolean bool) {
		this.useThread = bool;
	}
	
	public boolean isUsingThread() {
		return useThread;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "FormService "+formServiceName+" got onDestroy");
		EventDispatcher.removeDispatchDelegate(this);
		for (OnDestroySvcListener onDestroyListener : onDestroyListeners) {
			onDestroyListener.onDestroy();
		}
		if (stickyVal==START_STICKY) {
			mServiceLooper = null;
			mServiceHandler = null;
		}
	}
	
	public void registerForOnDestroy(OnDestroySvcListener component) {
		onDestroyListeners.add(component);
	}
	
		
	@Override
	public IBinder onBind(Intent intent) {
		// Possibly thinking about adding an event here, so people can bind to the service. Will wait to see if it is
		// something I need, or people want.
		return null;
	}

	@Override
	public boolean canDispatchEvent(Component arg0, String arg1) {
		boolean canDisptach = isRunning;
		if (canDisptach) {
			activeFormService = this;
		}
		
		return canDisptach;
	}

	@Override
	public boolean dispatchEvent(Component component, String componentName, String eventName,
			Object[] args) {
		throw new UnsupportedOperationException();
		
	}
	
	public void Initialize() {
		
		serviceHandler.post(new Runnable() {					
			public void run() {
				EventDispatcher.dispatchEvent(FormService.this, "Initialize");
				isRunning=true;				
			}
		});		
		 if (stickyVal==START_STICKY && useThread) {
	        	// Now let's have the service start in it's own thread unrelated to the process's main thread.
	        	// This will allow for more processing within the service without it killing the UI
	        	// thread. It will also take background priority. The service will only run in a background
	        	// thread when it is set to START_STICKY, and the useThread boolean is true.
			 	// Send a note to log cat to indicate the thread is active.
			 	Log.d(LOG_TAG, "FormService " + formServiceName + " is starting it's thread.");
			 
	        	HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
	        	thread.start();
	        
	        	// Per the android dev site, we get the Handlerthread's looper and use it for our handler
	        	mServiceLooper = thread.getLooper();
	        	mServiceHandler = new ServiceHandler(mServiceLooper);
		 }
	}
	
	@Override
	public HandlesEventDispatching getDispatchDelegate() {
		// TODO Auto-generated method stub
		return this;
	}

		
	// Component Container implementation. We need this so we can initialize the app inventor components. Remember, as this
	// is a service, only use NON visible components! 

	
	@Override
	public FormService $formService() {
		
		return this;
	}
	
	@Override
	public String $formSvcName() {
		return formServiceName;
	}

		
	public static FormService getActiveFormService() {
		return activeFormService;
	}
	
	public static void destroyService() {
		if (activeFormService!=null) {
			activeFormService.stopSelf();
		} else {
			throw new IllegalStateException("activeFormService is null.");
		
		}
	}
	
	public void deleteComponent(Object component) {
		if (component instanceof Deleteable) {
			((Deleteable) component).onDelete();
		}
	}

	public void callInitialize(Object component) throws Throwable {
		Method method;
		try {
			method = component.getClass().getMethod("Initialize", (Class<?>[]) null);			
		} catch (SecurityException e){
			Log.d(LOG_TAG, "Security exception "+e.getMessage());
			return;
		} catch (NoSuchMethodException e) {
			return;
		}
		try {
			Log.d(LOG_TAG, "calling Initialize method for Object "+component.toString());
			method.invoke(component, (Object[]) null);
		} catch (InvocationTargetException e) {
			Log.d(LOG_TAG, "invoke exception: "+e.getMessage());
			throw e.getTargetException();			
		}
	}

	@Override
	public Service $context() {
		
		return this;
	}
	
	 public void ErrorOccurred(Component component, String functionName, int errorNumber,
		      String message) {
		    String componentType = component.getClass().getName();
		    componentType = componentType.substring(componentType.lastIndexOf(".") + 1);
		    Log.e(LOG_TAG, "FormService " + formServiceName + " ErrorOccurred, errorNumber = " + errorNumber +
		        ", componentType = " + componentType + ", functionName = " + functionName +
		        ", messages = " + message);
		    if ((!(EventDispatcher.dispatchEvent(
		        this, "ErrorOccurred", component, functionName, errorNumber, message)))
		        )  {
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
		    
		        String message = ErrorMessages.formatMessage(errorNumber, messageArgs);
		        ErrorOccurred(component, functionName, errorNumber, message);
		      
		    
		  }	
		  
	public final void runOnSvcThread(Runnable action) {
		
		action.run();
		
	}
}
