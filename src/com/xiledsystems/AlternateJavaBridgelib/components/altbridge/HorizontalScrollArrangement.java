package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import com.xiledsystems.AlternateJavaBridgelib.components.common.ComponentConstants;



public class HorizontalScrollArrangement extends HSVArrangement {

	/**
	 * 
	 * Constructor for an arragement which scrolls horizontally. Use this
	 * constructor if you're NOT using the GLE to build your UI.
	 * 
	 * @param container The container to place this HorizontalScrollArrangement into
	 */
	public HorizontalScrollArrangement(ComponentContainer container) {
		super(container, ComponentConstants.LAYOUT_ORIENTATION_HORIZONTAL);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Constructor for an arrangement which scrolls horizontally. Use this
	 * constructor if you placed the arrangement with the GLE.
	 * 
	 * @param container Always use this
	 * @param resourceId The resource Id of the arrangement you place in the GLE
	 */
	public HorizontalScrollArrangement(ComponentContainer container, int resourceId) {
		super(container, ComponentConstants.LAYOUT_ORIENTATION_HORIZONTAL, resourceId);
		// TODO Auto-generated constructor stub
	}
	

}
