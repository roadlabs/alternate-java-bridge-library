package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.Calendar;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.widget.TimePicker;


public class TimeSelector extends ButtonBase {

	private TimePickerDialog view;
	private int hour;
	private int min;
	private boolean amPm=true;
	
	private OnTimeSetListener listener = new OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			hour = hourOfDay;
			min = minute;
			timeSet();			
		}		
	};
	
	/**
	 * Constructor for the DateSelector component when doing the UI through code.
	 * 
	 * @param container The container this component will be placed in.
	 */
	public TimeSelector(ComponentContainer container) {
		super(container);
		Calendar c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR_OF_DAY);
		min = c.get(Calendar.MINUTE);
		
	}
	
	/**
	 * Constructor for the DateSelector component when using the GLE.
	 * 
	 * @param container This should always "this" (without the quotes)
	 * @param resId The resource id of the DateSelector you dropped in the GLE
	 */
	public TimeSelector(ComponentContainer container, int resourceId) {
		super(container, resourceId);
		Calendar c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR_OF_DAY);
		min = c.get(Calendar.MINUTE);
	}
	
	/**
	 * 
	 * @return An int array which returns the current saved time in the time
	 * selector. The first index is the hour, the second is the minute.
	 * 
	 */
	public int[] GetTime() {
		return new int[] { hour, min };
	}
	
	/**
	 * 
	 * Set this to true if you want to use astronomical time
	 * (24 hour clock)
	 *  
	 * @param astronomicalTime
	 */
	public void AstronomicalTime(boolean astronomicalTime) {
		amPm = !astronomicalTime;
	}
	
	/**
	 * 
	 * @return Whether using 24hr clock or not
	 */
	public boolean AstrologicalTime() {
		return !amPm;
	}
	
	/**
	 *  Set the default time that the time selector is set to
	 *  when first opened. Default is the time that this component
	 *  got initialized.
	 *  
	 * @param hour
	 * @param minute
	 */
	public void DefaultTime(int hour, int minute) {
		this.hour = hour;
		this.min = minute;
	}
	
	private void timeSet() {
		EventDispatcher.dispatchEvent(this, "TimeSet", hour, min);		
	}
		

	@Override
	public void click() {
		//view = new TimePickerDialog(container.$context(), listener, hour, min, amPm);		
		view = new TimePickerDialog(container.$context(), listener, hour, min, !amPm);		
		view.show();
	}


}
