package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;



public class VerticalScrollArrangement extends SVArrangement {

	/**
	 * 
	 * Non GLE component constructor
	 * 
	 * @param container The container to place the arrangement into
	 */
	public VerticalScrollArrangement(ComponentContainer container) {
		super(container, 0);
		
	}
	
	/**
	 * 
	 * GLE component constructor.
	 * This is an arrangement which scrolls vertically.
	 * 
	 * @param container Use this
	 * @param resourceId the resource id of the arrangement you placed in the GLE
	 */
	public VerticalScrollArrangement(ComponentContainer container, int resourceId) {
		super(container, 0, resourceId);		
	}
	
	

}
