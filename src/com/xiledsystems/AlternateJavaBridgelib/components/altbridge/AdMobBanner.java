package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.view.View;
import android.widget.RelativeLayout;

import com.google.ads.*;

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
	
	public void PlaceAd(int x, int y) {
		if (isInRelArgmnt()) {
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) view.getLayoutParams();
			lp.leftMargin = x;
			lp.topMargin = y;
			view.requestLayout();
		}
	}
	
	public void addTestDevice(String dev_id) {
		adreq.addTestDevice(dev_id);
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
