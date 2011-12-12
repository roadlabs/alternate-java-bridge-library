package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.view.View;

public class DrawerSlider extends AndroidViewComponent {

	private final SlideDrawer view;
	
	public DrawerSlider(ComponentContainer container, int resId) {
		super(container);
		view = (SlideDrawer) container.$context().findViewById(resId);		
		
	}
	
	@Override
	public View getView() {
		
		return view;
	}

}
