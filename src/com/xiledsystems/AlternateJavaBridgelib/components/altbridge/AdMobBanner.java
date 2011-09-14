package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.view.View;
import com.google.ads.*;

public class AdMobBanner extends AndroidViewComponent implements OnStopListener {

	private final AdView view;
	private final Form form;
	private AdRequest adreq;
	private boolean autoResize= false;
	private double widthMultiplier;
	private double heightMultiplier;
	
	
	public AdMobBanner(ComponentContainer container, String dev_id) {
		super(container);
		view = new AdView(container.$context(), AdSize.BANNER, dev_id);
		adreq = new AdRequest();
		form = container.$form();
		container.$add(this);
		form.registerForOnStop(this);
		form.registerForOnInitialize(this);
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
	
	@Override
	public void onInitialize() {
		
		if (autoResize) {
			Width((int) (form.scrnWidth * widthMultiplier));
			Height((int) (form.scrnHeight * heightMultiplier));
		}
		
	}
	
	public void setMultipliers(double widthmultiplier, double heightmultiplier) {
		
		autoResize=true;
		widthMultiplier = widthmultiplier;
		heightMultiplier = heightmultiplier;
		
	}

}
