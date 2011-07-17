package com.xiledsystems.AlternateJavaBridgelib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import com.google.devtools.simple.runtime.components.android.AndroidViewComponent;
import com.google.devtools.simple.runtime.components.android.Deleteable;
import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.components.HandlesEventDispatching;
import com.google.devtools.simple.runtime.components.android.collect.Sets;
import com.google.devtools.simple.runtime.events.EventDispatcher;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * Most of this was copied from Java Bridge's Form class. Lots of stuff was left out, as this is a service,
 * which doesn't have a UI. Some modifications had to be made to adjust the "form" to a service instead
 * of an activity. 
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
  private final Set<OnDestroyListener> onDestroyListeners = Sets.newHashSet();
	
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
        return stickyVal;
    }
	
	public void setStickyVal(int i) {
		stickyVal=i;
	}
	
	public void registerForOnStartCommand(OnStartCommandListener component) {
		onStartCommandListeners.add(component);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(LOG_TAG, "FormService "+formServiceName+" got onDestroy");
		
		EventDispatcher.removeDispatchDelegate(this);
    for (OnDestroyListener onDestroyListener : onDestroyListeners) {
            onDestroyListener.onDestroy();
        }
	}
   
  public void registerForOnDestroy(OnDestroyListener component) {
        onDestroyListeners.add(component);
    }
	
		
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
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
	}
	
	@Override
	public HandlesEventDispatching getDispatchDelegate() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public void $add(AndroidViewComponent arg0) {
		// TODO Auto-generated method stub
		
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
	

}
