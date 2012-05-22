package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.RemoteViews;

public class WidgetUtil {

	private final Class<?> widgetClass;
	private final int layoutId;
	private int componentResId;
	
	/**
	 *  Constructor for the WidgetUtil. Use one instance per widget.
	 *  
	 * @param widgetClass The class name of the widget (ex NewWidget.class)
	 * @param layoutResId - The resource Id of the widget's layout
	 * @param componentResId - The resource Id of the initial component to modify. This can be changed later.
	 */
	public WidgetUtil(Class<?> widgetClass, int layoutResId, int componentResId) {
		this.widgetClass = widgetClass;
		this.layoutId = layoutResId;
		this.componentResId = componentResId;
	}
	
	
	/**
	 * Change which component you want to manipulate.
	 * 
	 * @param resourceId
	 */
	public void Component(int resourceId) {
		this.componentResId = resourceId;
	}
	
	/**
	 *  Use this to change the image of a component in a widget
	 *  
	 * @param context - Usually "this".
	 * @param componentResId - The resource Id of the component to change (ImageView, Button, etc)
	 * @param imageResId - The resource Id of the image to use
	 */
	public void Image(Context context, int imageResId) {
		setImage(context, componentResId, imageResId, layoutId, widgetClass);
	}
	
	/**
	 *  Use this to change the text in a textview within a widget
	 *  
	 * @param context - Usually "this"
	 * @param componentResId - The resource Id of the textview to modify
	 * @param text - The text to put in the textview
	 */
	public void Text(Context context, String text) {
		setText(context, componentResId, text, layoutId, widgetClass);
	}
	
	/**
	 * Use this to set the text color of a TextView in a widget
	 * 
	 * @param context
	 * @param componentResId
	 * @param color
	 */
	public void TextColor(Context context, int color) {
		setTextColor(context, componentResId, color, layoutId, widgetClass);
	}
	
	/**
	 * Use this method to change a components visibility
	 * 
	 * @param context
	 * @param componentResId
	 * @param visible
	 */
	public void Visible(Context context, boolean visible) {
		setVisibility(context, componentResId, visible, layoutId, widgetClass);
	}
	
	/**
	 * Static method to set visibility of a component in a widget.
	 * Use this method if you don't want to instantiate an instance
	 * of the WidgetUtil class.
	 * 
	 * @param context
	 * @param componentResId
	 * @param visible
	 * @param layoutId
	 * @param widgetClass
	 */
	public static void setVisibility(Context context, int componentResId, boolean visible, int layoutId, Class<?> widgetClass) {
		RemoteViews view = new RemoteViews(context.getPackageName(), layoutId);
		int vis;
		if (visible) {
			vis = View.VISIBLE;
		} else {
			vis = View.GONE;
		}
		view.setViewVisibility(componentResId, vis);	
		AppWidgetManager man = AppWidgetManager.getInstance(context);
		ComponentName widget = new ComponentName(context.getApplicationContext(), widgetClass);
		man.updateAppWidget(widget, view);
	}
	
	/**
	 * 
	 * Static method to set the text color of a TextView in a widget.
	 * Use this method if you don't want to instantiate an instance
	 * of the WidgetUtil class.
	 * 
	 * @param context
	 * @param componentResId
	 * @param color
	 * @param layoutId
	 * @param widgetClass
	 */
	public static void setTextColor(Context context, int componentResId, int color, int layoutId, Class<?> widgetClass) {
		RemoteViews view = new RemoteViews(context.getPackageName(), layoutId);
		view.setTextColor(componentResId, color);		
		AppWidgetManager man = AppWidgetManager.getInstance(context);
		ComponentName widget = new ComponentName(context.getApplicationContext(), widgetClass);
		man.updateAppWidget(widget, view);
	}
	
	/**
	 * 
	 * Static method to set the image of a component in a widget.
	 * Use this method if you don't want to instantiate an instance
	 * of the WidgetUtil class.
	 * 
	 * @param context
	 * @param componentResId
	 * @param imageResId
	 * @param layoutId
	 * @param widgetClass
	 */
	public static void setImage(Context context, int componentResId, int imageResId, int layoutId, Class<?> widgetClass) {
		RemoteViews view = new RemoteViews(context.getPackageName(), layoutId);
		Bitmap draw = BitmapFactory.decodeResource(context.getResources(), imageResId);
		view.setBitmap(componentResId, "setImageBitmap", draw);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		ComponentName widget = new ComponentName(context.getApplicationContext(), widgetClass);
		manager.updateAppWidget(widget, view);		
	}
	
	/**
	 * 
	 * Static method to set the text of a TextView in a widget.
	 * Use this method if you don't want to instantiate an instance
	 * of the WidgetUtil class.
	 * 
	 * @param context
	 * @param componentResId
	 * @param text
	 * @param layoutId
	 * @param widgetClass
	 */
	public static void setText(Context context, int componentResId, String text, int layoutId, Class<?> widgetClass) {
		RemoteViews view = new RemoteViews(context.getPackageName(), layoutId);
		view.setTextViewText(componentResId, text);
		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		ComponentName widget = new ComponentName(context.getApplicationContext(), widgetClass);
		manager.updateAppWidget(widget, view);
	}	
		
}
