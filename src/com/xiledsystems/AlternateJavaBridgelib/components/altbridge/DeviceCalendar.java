package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.CalendarEvent;

public class DeviceCalendar extends AndroidNonvisibleComponent {
	
	private long calId;
	private String accountName;
	private String calendarDisplayName;
	private String calendarName;
	private int calendarColor;
		
	
	public DeviceCalendar(ComponentContainer container) {
		super(container);
	}
	
	/**
	 * This method is for pushing an event to the calendar
	 * through an intent. This way does not require any special
	 * permissions in your manifest.
	 * 
	 * @param event
	 */
	public void pushEvent(CalendarEvent event) {
		Intent intent = new Intent(Intent.ACTION_INSERT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.startTime());
		intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.endTime());
		intent.putExtra(Events.TITLE, event.Title());
		intent.putExtra(Events.DESCRIPTION, event.Description());
		container.$context().startActivity(intent);
	}
	
	/**
	 * This method will add the event to the user's calendar
	 * without the use of an intent. However, this requires
	 * you to set the READ_CALENDAR, and WRITE_CALENDAR
	 * permissions.
	 * 
	 * @param event
	 * 
	 * @return The ID for the new event that was added.
	 */
	public String addEvent(CalendarEvent event) {
		if (calId == 0) {
			getCalendarId();
		}
		ContentResolver cr = container.$context().getContentResolver();
		ContentValues values = new ContentValues();
		values.put(CalendarContract.Events.DTSTART, event.startTime());
		values.put(CalendarContract.Events.DTEND, event.endTime());
		values.put(CalendarContract.Events.TITLE, event.Title());
		values.put(CalendarContract.Events.DESCRIPTION, event.Description());
		values.put(CalendarContract.Events.CALENDAR_ID, calId);
		Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
		
		return uri.getLastPathSegment();
	}

	private void getCalendarId() {
		Uri uri = CalendarContract.Calendars.CONTENT_URI;
		String[] projection = new String[] { 
				CalendarContract.Calendars._ID,
				CalendarContract.Calendars.ACCOUNT_NAME,
				CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
				CalendarContract.Calendars.NAME,
				CalendarContract.Calendars.CALENDAR_COLOR
		};
		
		Cursor c = container.$context().managedQuery(uri, projection, null, null, null);
		while (c.moveToFirst()) {
			calId = c.getLong(0);
			accountName = c.getString(1);
			calendarDisplayName = c.getString(2);
			calendarName = c.getString(3);
			calendarColor = c.getInt(4);
		}
		
	}
	

}
