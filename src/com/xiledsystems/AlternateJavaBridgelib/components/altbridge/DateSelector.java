package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.Calendar;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;
import android.app.DatePickerDialog;
import android.widget.DatePicker;


public class DateSelector extends ButtonBase {

	DatePickerDialog dialog;
	private int mYear;
	private int mMonth;
	private int mDay;
	
	private DatePickerDialog.OnDateSetListener selectListener = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			dateSet(year, (monthOfYear+1), dayOfMonth);			
		}
	};
	
	/**
	 * Constructor for the DateSelector component when doing the UI through code.
	 * 
	 * @param container The container this component will be placed in.
	 */
	public DateSelector(ComponentContainer container) {
		super(container);				
		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * Constructor for the DateSelector component when using the GLE.
	 * 
	 * @param container This should always be "this" (without the quotes)
	 * @param resId The resource id of the DateSelector you dropped in the GLE
	 */
	public DateSelector(ComponentContainer container, int resId) {
		super(container, resId);				
		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * Sets the date that the date selector will default to when
	 * opened. If this is not run, then the default date will be
	 * the current date. 
	 * 
	 * @param year
	 * @param month
	 * @param day
	 */
	public void DefaultDate(int year, int month, int day) {
		mYear = year;
		mMonth = (month-1);
		mDay = day;		
	}
	
	/**
	 * 
	 * @return the DateSelector's default date
	 */
	public int[] DefaultDate() {
		return new int[] { mYear, (mMonth+1), mDay };
	}
	
	private void dateSet(int year, int monthOfYear, int dayOfMonth) {
		EventDispatcher.dispatchEvent(this, "DateSet", year, monthOfYear, dayOfMonth);
	}
	
	/**
	 * Internal method, not to be called manually.
	 */
	@Override
	public void click() {		
		dialog = new DatePickerDialog(container.$context(), selectListener, mYear, mMonth, mDay);
		dialog.show();		
	}


}
