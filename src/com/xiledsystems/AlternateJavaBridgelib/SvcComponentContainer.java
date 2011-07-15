package com.xiledsystems.AlternateJavaBridgelib;

import com.google.devtools.simple.runtime.components.android.AndroidViewComponent;

import android.app.Application;
import android.app.Service;

public interface SvcComponentContainer {

	// Copied from ComponentContainer, but modified to work with services instead of activities.
	Service $context();
	
	FormService $formService();
	
	String $formSvcName();
	
	// I don't think this part is necessary, but I've left it in case. 
	
	void $add(AndroidViewComponent component);
	
	
}
