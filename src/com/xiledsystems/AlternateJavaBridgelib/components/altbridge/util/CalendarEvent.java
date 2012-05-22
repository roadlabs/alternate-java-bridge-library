package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.util.Calendar;

public class CalendarEvent {
	
	private long startTime;
	private long endTime;
	private String title;
	private String description;
	
	public void Start(int year, int month, int day, int hour, int min) {
		Calendar begin = Calendar.getInstance();
		begin.set(year, month, day, hour, min);
		startTime = begin.getTimeInMillis();
	}
	
	public void End(int year, int month, int day, int hour, int min) {
		Calendar end = Calendar.getInstance();
		end.set(year, month, day, hour, min);
		endTime = end.getTimeInMillis();
	}
	
	public void Title(String title) {
		this.title = title;
	}
	
	public void Description(String description) {
		this.description = description;
	}
	
	public long startTime() {
		return startTime;
	}
	
	public long endTime() {
		return endTime;
	}
	
	public String Title() {
		return title;
	}
	
	public String Description() {
		return description;
	}

}
