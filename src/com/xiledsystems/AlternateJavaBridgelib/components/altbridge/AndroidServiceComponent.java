package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.HandlesEventDispatching;


	/**
	 * Base class for all Service components
	 * @author Ryan Bis - www.xiledsystems.com
	 *
	 */

public abstract class AndroidServiceComponent implements Component {
	
	protected FormService formService;
	
	/*
	 *  Creates a new AndroidServiceComponent.
	 *  
	 */
	
	protected AndroidServiceComponent(FormService formService) {
		this.formService = formService;
	}
	
	// Component implementation
	
	@Override
	public HandlesEventDispatching getDispatchDelegate() {
		return formService;
	}

}
