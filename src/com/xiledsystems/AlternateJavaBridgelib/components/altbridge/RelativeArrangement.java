package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.view.View;

public class RelativeArrangement extends RVArrangement {

	/**
	 * 
	 * Non GLE Constructor
	 * 
	 * @param container The container to place the arrangement into
	 */
	public RelativeArrangement(ComponentContainer container) {
		super(container, 0);
	}
	
	/**
	 * 
	 * GLE constructor. This is an arragement which allows you to place
	 * components wherever you want.
	 * All visible components have the MoveTo(x, y) method for moving
	 * the component inside of it's parent RelativeArrangement.
	 * 
	 * @param container This should always be this
	 * @param resourceId The resource id of the relative arrangement you placed in the GLE
	 */
	public RelativeArrangement(ComponentContainer container, int resourceId) {
		super(container, 0, resourceId);
	}

}
