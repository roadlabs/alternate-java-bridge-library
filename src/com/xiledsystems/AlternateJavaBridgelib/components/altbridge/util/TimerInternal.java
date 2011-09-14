package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import com.xiledsystems.AlternateJavaBridgelib.components.AlarmHandler;

import android.os.Handler;

/**
 * Helper class for components containing timers, such as Timer and Sprite.
 *
 */
public final class TimerInternal implements Runnable {

  // Android message handler used as a timer
  private Handler handler;

  // Indicates whether the timer is running or not
  private boolean enabled = true;

  // Interval between timer events in ms
  private int interval = 1000;

  // Component that should be called by timer
  private AlarmHandler component;

  /**
   * Timer constructor
   *
   * @param component the component whose run() method should be called
   *        on timer intervals
   */
  public TimerInternal(AlarmHandler component) {
    handler = new Handler();
    handler.postDelayed(this, interval);
    this.component = component;
  }

  /**
   * Interval getter.
   *
   * @return  timer interval in ms
   */
  public int Interval() {
    return interval;
  }

  /**
   * Interval property setter method: sets the interval between timer events.
   *
   * @param interval  timer interval in ms
   */
  public void Interval(int interval) {
    this.interval = interval;
    if (enabled) {
      handler.removeCallbacks(this);
      handler.postDelayed(this, interval);
    }
  }

  /**
   * Enabled property getter method.
   *
   * @return  {@code true} indicates a running timer, {@code false} a stopped
   *          timer
   */
  public boolean Enabled() {
    return enabled;
  }

  /**
   * Enabled property setter method: starts or stops the timer.
   *
   * @param enabled  {@code true} starts the timer, {@code false} stops it
   */
  public void Enabled(boolean enabled) {
    if (this.enabled) {
      handler.removeCallbacks(this);
    }

    this.enabled = enabled;

    if (enabled) {
      handler.postDelayed(this, interval);
    }
  }

  // Runnable implementation

  public void run() {
    if (enabled) {
      component.alarm();
      handler.postDelayed(this, interval);
    }
  }
}
