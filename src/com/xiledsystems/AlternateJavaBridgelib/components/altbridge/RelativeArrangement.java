package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.view.View;

public class RelativeArrangement extends RVArrangement {

	public RelativeArrangement(ComponentContainer container) {
		super(container, 0);
	}
	
	public RelativeArrangement(ComponentContainer container, int resourceId) {
		super(container, 0, resourceId);
	}

}
