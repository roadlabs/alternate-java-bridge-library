package com.xiledsystems.AlternateJavaBridgelib;

import java.util.Calendar;

import com.google.devtools.simple.runtime.Dates;
import com.google.devtools.simple.runtime.components.AlarmHandler;
import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.components.android.Deleteable;
import com.google.devtools.simple.runtime.components.android.util.TimerInternal;
import com.google.devtools.simple.runtime.events.EventDispatcher;

/**
 * 
 * This is just copied from the Java Bridge's Clock class, with it's constructor modified to
 * be put into a SvcComponentContainer. Also the way it gets context is different.
 * 
 *  Ryan Bis - www.xiledsystems.com
 *
 */

public final class Clocksvc extends AndroidServiceComponent implements Component, AlarmHandler, Deleteable {

private TimerInternal timerInternal;
private boolean timerAlwaysFires = true;
private boolean onScreen = false;


/**
* Creates a new Clock service component.
*
* @param container ignored (because this is a non-visible component)
*/
public Clocksvc(SvcComponentContainer container) {
	super(container.$formService());
	timerInternal = new TimerInternal(this);

	}

// Only the above constructor should be used in practice.
public Clocksvc() {
super(null);
// To allow testing without Timer
}


public void Timer() {
if (timerAlwaysFires || onScreen) {
  EventDispatcher.dispatchEvent(this, "Timer");
}
}

/**
* Interval property getter method.
*
* @return timer interval in ms
*/

public int TimerInterval() {
return timerInternal.Interval();
}

/**
* Interval property setter method: sets the interval between timer events.
*
* @param interval timer interval in ms
*/

public void TimerInterval(int interval) {
timerInternal.Interval(interval);
}

/**
* Enabled property getter method.
*
* @return {@code true} indicates a running timer, {@code false} a stopped
*         timer
*/

public boolean TimerEnabled() {
return timerInternal.Enabled();
}

/**
* Enabled property setter method: starts or stops the timer.
*
* @param enabled {@code true} starts the timer, {@code false} stops it
*/

public void TimerEnabled(boolean enabled) {
timerInternal.Enabled(enabled);
}

/**
* TimerAlwaysFires property getter method.
*
*  return {@code true} if the timer event will fire even if the application
*   is not on the screen
*/

public boolean TimerAlwaysFires() {
return timerAlwaysFires;
}

/**
* TimerAlwaysFires property setter method: instructs when to disable
*
*  @param always {@code true} if the timer event should fire even if the
*  application is not on the screen
*/

public void TimerAlwaysFires(boolean always) {
timerAlwaysFires = always;
}

// AlarmHandler implementation

@Override
public void alarm() {
Timer();
}

/**
* Returns the current system time in milliseconds.
*
* @return  current system time in milliseconds
*/

public static long SystemTime() {
return Dates.Timer();
}


public static Calendar Now() {
return Dates.Now();
}

/**
* An instant in time specified by MM/DD/YYYY hh:mm:ss or MM/DD/YYYY or hh:mm
* where MM is the month (01-12), DD the day (01-31), YYYY the year
* (0000-9999), hh the hours (00-23), mm the minutes (00-59) and ss
* the seconds (00-59).
*
* @param from  string to convert
* @return  date
*/

public static Calendar MakeInstant(String from) {
try {
  return Dates.DateValue(from);
} catch (IllegalArgumentException e) {
  throw new RuntimeException(
      "Argument to MakeInstant should have form MM/DD/YYYY, hh:mm:ss, or MM/DD/YYYY or hh:mm"+
      "  ...  Sorry to be so picky.");

  }
}

/**
* Create an Calendar from ms since 1/1/1970 00:00:00.0000
* Probably should go in Calendar.
*
* @param millis raw millisecond number.
*/

public static Calendar MakeInstantFromMillis(long millis) {
Calendar instant = Dates.Now(); // just to get our hands on an instant
instant.setTimeInMillis(millis);
return instant;
}

/**
* Calendar property getter method: gets the raw millisecond representation of
*  a Calendar.
* @param instant Calendar
* @return milliseconds since 1/1/1970.
*/

public static long GetMillis(Calendar instant) {
return instant.getTimeInMillis();
}


public static Calendar AddSeconds(Calendar instant, int seconds) {
Calendar newInstant = (Calendar) instant.clone();
Dates.DateAdd(newInstant, Calendar.SECOND, seconds);
return newInstant;
}


public static Calendar AddMinutes(Calendar instant, int minutes) {
Calendar newInstant = (Calendar) instant.clone();
Dates.DateAdd(newInstant, Calendar.MINUTE, minutes);
return newInstant;
}


public static Calendar AddHours(Calendar instant, int hours) {
Calendar newInstant = (Calendar) instant.clone();
Dates.DateAdd(newInstant, Calendar.HOUR_OF_DAY, hours);
return newInstant;
}


public static Calendar AddDays(Calendar instant, int days) {
Calendar newInstant = (Calendar) instant.clone();
Dates.DateAdd(newInstant, Calendar.DATE, days);
return newInstant;
}


public static Calendar AddWeeks(Calendar instant, int weeks) {
Calendar newInstant = (Calendar) instant.clone();
Dates.DateAdd(newInstant, Calendar.WEEK_OF_YEAR, weeks);
return newInstant;
}


public static Calendar AddMonths(Calendar instant, int months) {
Calendar newInstant = (Calendar) instant.clone();
Dates.DateAdd(newInstant, Calendar.MONTH, months);
return newInstant;
}


public static Calendar AddYears(Calendar instant, int years) {
Calendar newInstant = (Calendar) instant.clone();
Dates.DateAdd(newInstant, Calendar.YEAR, years);
return newInstant;
}

/**
* Returns the milliseconds by which end follows start (+ or -)
*
* @param start beginning instant
* @param end ending instant
* @return  milliseconds
*/

public static long Duration(Calendar start, Calendar end) {
return end.getTimeInMillis() - start.getTimeInMillis();
}

/**
* Returns the seconds for the given instant.
*
* @param instant  instant to use seconds of
* @return  seconds (range 0 - 59)
*/

public static int Second(Calendar instant) {
return Dates.Second(instant);
}

/**
* Returns the minutes for the given date.
*
* @param instant instant to use minutes of
* @return  minutes (range 0 - 59)
*/

public static int Minute(Calendar instant) {
return Dates.Minute(instant);
}

/**
* Returns the hours for the given date.
*
* @param instant Calendar to use hours of
* @return  hours (range 0 - 23)
*/

public static int Hour(Calendar instant) {
return Dates.Hour(instant);
}

/**
* Returns the day of the month.
*
* @param instant  instant to use day of the month of
* @return  day: [1...31]
*/

public static int DayOfMonth(Calendar instant) {
return Dates.Day(instant);
}

/**
* Returns the weekday for the given instant.
*
* @param instant  instant to use day of week of
* @return day of week: [1...7] starting with Sunday
*/

public static int Weekday(Calendar instant) {
return Dates.Weekday(instant);
}

/**
* Returns the name of the weekday for the given instant.
*
* @param instant  instant to use weekday of
* @return  weekday, as a string.
*/

public static String WeekdayName(Calendar instant) {
return Dates.WeekdayName(instant);
}

/**
* Returns the number of the month for the given instant.
*
* @param instant  instant to use month of
* @return  number of month
*/

public static int Month(Calendar instant) {
return Dates.Month(instant) + 1;
}

/**
* Returns the name of the month for the given instant.
*
* @param instant  instant to use month of
* @return  name of month
*/

public static String MonthName(Calendar instant) {
return Dates.MonthName(instant);
}

/**
* Returns the year of the given instant.
*
* @param instant  instant to use year of
* @return  year
*/

public static int Year(Calendar instant) {
return Dates.Year(instant);
}

/**
* Converts and formats the given instant into a string.   *
*
* @param instant  instant to format
* @return  formatted instant
*/

public static String FormatDateTime(Calendar instant) {
return Dates.FormatDateTime(instant);
}

/**
* Converts and formats the given instant into a string.
*
* @param instant  instant to format
* @return  formatted instant
*/

public static String FormatDate(Calendar instant) {
return Dates.FormatDate(instant);
}

/**
* Converts and formats the given instant into a string.
*
* @param instant  instant to format
* @return  formatted instant
*/

public static String FormatTime(Calendar instant) {
return Dates.FormatTime(instant);
}

@Override
public void onDelete() {
	
	TimerEnabled(false);
	
}
}
