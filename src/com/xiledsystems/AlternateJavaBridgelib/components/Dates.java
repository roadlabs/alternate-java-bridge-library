package com.xiledsystems.AlternateJavaBridgelib.components;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public final class Dates
{

  
  public static final int DATE_YEAR = 1;

  
  public static final int DATE_MONTH = 2;

  
  public static final int DATE_DAY = 5;

  
  public static final int DATE_WEEK = 3;

  
  public static final int DATE_HOUR = 11;

  
  public static final int DATE_MINUTE = 12;

  
  public static final int DATE_SECOND = 13;

  
  public static final int DATE_MONDAY = 2;

  
  public static final int DATE_TUESDAY = 3;

  
  public static final int DATE_WEDNESDAY = 4;

  
  public static final int DATE_THURSDAY = 5;

  
  public static final int DATE_FRIDAY = 6;

  
  public static final int DATE_SATURDAY = 7;

  
  public static final int DATE_SUNDAY = 1;

  
  public static final int DATE_JANUARY = 0;

  
  public static final int DATE_FEBRUARY = 1;

  
  public static final int DATE_MARCH = 2;

  
  public static final int DATE_APRIL = 3;

  
  public static final int DATE_MAY = 4;

  
  public static final int DATE_JUNE = 5;

  
  public static final int DATE_JULY = 6;

  
  public static final int DATE_AUGUST = 7;

  
  public static final int DATE_SEPTEMBER = 8;

  
  public static final int DATE_OCTOBER = 9;

  
  public static final int DATE_NOVEMBER = 10;

  
  public static final int DATE_DECEMBER = 11;

  
  public static void DateAdd(Calendar date, int intervalKind, int interval)
  {
    switch (intervalKind) { case 4:
    case 6:
    case 7:
    case 8:
    case 9:
    case 10:
    default:
      throw new IllegalArgumentException("illegal date/time interval kind in function DateAdd()");
    case 1:
    case 2:
    case 3:
    case 5:
    case 11:
    case 12:
    case 13:
    }
    date.add(intervalKind, interval);
  }

  
  public static Calendar DateValue(String value)
  {
    Calendar date = new GregorianCalendar();
    try {
      DateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
      dateTimeFormat.setLenient(true);
      date.setTime(dateTimeFormat.parse(value));
    } catch (ParseException e) {
      try {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(true);
        date.setTime(dateFormat.parse(value));
      } catch (ParseException e1) {
        try {
          DateFormat dateFormat = new SimpleDateFormat("HH:mm");
          dateFormat.setLenient(true);
          date.setTime(dateFormat.parse(value));
        } catch (ParseException pe) {
          throw new IllegalArgumentException("illegal date/time format in function DateValue()");
        }
      }
    }
    return date;
  }

   
  public static int Day(Calendar date)
  {
    return date.get(5);
  }

   
  public static String FormatDateTime(Calendar date)
  {
    return DateFormat.getDateTimeInstance(2, 2).format(date.getTime());
  }

   
  public static String FormatDate(Calendar date)
  {
    return DateFormat.getDateInstance(2).format(date.getTime());
  }

   
  public static String FormatTime(Calendar date)
  {
    return DateFormat.getTimeInstance(2).format(date.getTime());
  }

   
  public static int Hour(Calendar date)
  {
    return date.get(11);
  }

   
  public static int Minute(Calendar date)
  {
    return date.get(12);
  }

   
  public static int Month(Calendar date)
  {
    return date.get(2);
  }

   
  public static String MonthName(Calendar date)
  {
    return String.format("%1$tB", new Object[] { date });
  }

   
  public static Calendar Now()
  {
    return new GregorianCalendar();
  }

   
  public static int Second(Calendar date)
  {
    return date.get(13);
  }

   
  public static long Timer()
  {
    return System.currentTimeMillis();
  }

   
  public static int Weekday(Calendar date)
  {
    return date.get(7);
  }

   
  public static String WeekdayName(Calendar date)
  {
    return String.format("%1$tA", new Object[] { date });
  }

   
  public static int Year(Calendar date)
  {
    return date.get(1);
  }
}
