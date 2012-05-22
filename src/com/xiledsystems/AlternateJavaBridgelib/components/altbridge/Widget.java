package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.ArrayList;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.DoubleList;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class Widget extends AppWidgetProvider {

	private Context c;
	private AppWidgetManager manager;
	private int widgetId;
	private int layout;
	private DoubleList formClicks;
	private DoubleList formServiceClicks;
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
	
		final int N = appWidgetIds.length;
		c = context;
		manager = appWidgetManager;		
		formClicks = new DoubleList();
		formServiceClicks = new DoubleList();
		
		for (int i=0;i<N;i++) {
			int appWidgetId = appWidgetIds[i];
			
			// Dev defines components in the appwidget
			$define();	
			
			// Create the remote view using the layout resource id provided from dev
			RemoteViews views = new RemoteViews(context.getPackageName(), layout);
			
			// Set the form/formservice click listeners (dev must run addformclick/addformserviceclick)
			setClicks(context, views);
			
			// Update the widget with the changes made.
			appWidgetManager.updateAppWidget(appWidgetId, views);
			
		}
		c = null;
		manager = null;
	}
	
	
	private void setClicks(Context context, RemoteViews views) {
		if (formClicks != null && formClicks.size() > 0) {
			for (int i = 0; i < formClicks.size(); i++) {
				Intent intent = new Intent(context, (Class<?>) formClicks.get(i)[1]);
				PendingIntent pendIntent = PendingIntent.getActivity(context, 0, intent, 0);
				views.setOnClickPendingIntent(Convert.Int(formClicks.get(i)[0]), pendIntent);				
			}
		}
		if (formServiceClicks != null && formServiceClicks.size() > 0) {
			for (int i = 0; i < formServiceClicks.size(); i++) {
				Intent intent = new Intent(context, (Class<?>) formServiceClicks.get(i)[1]);
				PendingIntent pendIntent = PendingIntent.getService(context, 0, intent, 0);
				views.setOnClickPendingIntent(Convert.Int(formServiceClicks.get(i)[0]), pendIntent);				
			}
		}
	}

	/**
	 * Use this method to set an ImageView, ImageButton, or Button's 
	 * "Click" to open a Form.
	 * 
	 * @param resourceId The resource Id of the clicked object
	 * @param formToOpen The form to open
	 */
	public void addFormClick(int resourceId, Class<?> formToOpen) {
		formClicks.add(resourceId, formToOpen);		
	}
	
	/**
	 * Use this method to set an ImageView, ImageButton, or Button's 
	 * "Click" to open a FormService.
	 * 
	 * @param resourceId The resource Id of the clicked object
	 * @param formServiceToOpen The formservice to open
	 */
	public void addFormServiceClick(int resourceId, Class<?> formServiceToOpen) {
		formServiceClicks.add(resourceId, formServiceToOpen);
	}
	
	/**
	 * Method for defining the widget
	 * 
	 */
	void $define() {		
	}
	
	/**
	 * Use this to set the widget's layout.
	 * 
	 * @param layoutId Resource Id of the widget's layout
	 */
	public void Layout(int layoutId) {
		layout = layoutId;
	}
	
	
}
