package com.xiledsystems.AlternateJavaBridgelib;

import android.view.View;
import com.google.ads.*;

import com.google.devtools.simple.runtime.components.android.AndroidViewComponent;
import com.google.devtools.simple.runtime.components.android.ComponentContainer;
import com.google.devtools.simple.runtime.components.android.Form;
import com.google.devtools.simple.runtime.components.android.OnStopListener;

public class AdMobBanner extends AndroidViewComponent implements OnStopListener {

	private final AdView view;
	private final Form form;
	private AdRequest adreq;
	
	
	public AdMobBanner(ComponentContainer container, String dev_id) {
		super(container);
		view = new AdView(container.$context(), AdSize.BANNER, dev_id);
		adreq = new AdRequest();
		form = container.$form();
		container.$add(this);
		form.registerForOnStop(this);
	}
	
	public void stopLoadingAd() {
		
		view.stopLoading();
				
	}
	
	public void startAd() {
		
		view.loadAd(adreq);
		
	}
	
	public void destroy() {
		
		view.destroy();
		
	}
	
	@Override
	public View getView() {
		
		return view;
	}

	@Override
	public void onStop() {
		
		view.stopLoading();
		
	}

}
