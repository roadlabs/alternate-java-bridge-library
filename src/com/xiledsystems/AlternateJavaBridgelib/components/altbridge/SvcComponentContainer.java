package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.app.Service;


public interface SvcComponentContainer {
	
	Service $context();
	
	FormService $formService();
	
	String $formSvcName();

}
