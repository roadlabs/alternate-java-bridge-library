package com.xiledsystems.AlternateJavaBridgelib.components;

/**
 * Interface for components that can be woken up by an outside alarm.  This is
 * typically used in conjunction with TimerInternal().
 */

public interface AlarmHandler {
  public void alarm();
}

