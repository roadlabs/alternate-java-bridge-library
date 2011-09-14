package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;

import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

/**
 * Sensor that can measure absolute orientation in 3 dimensions.
 *
 * TODO(user): This implementation does not correct for acceleration
 * of the phone.  Make a better version that does this.
 */

public class OrientationSensor extends AndroidNonvisibleComponent
    implements SensorEventListener, Deleteable {
  private final SensorManager sensorManager;
  private Sensor orientationSensor;
  private boolean enabled;
  private float yaw;
  private float pitch;
  private float roll;
  private int accuracy;

  /**
   * Creates a new OrientationSensor component.
   *
   * @param container  ignored (because this is a non-visible component)
   */
  public OrientationSensor(ComponentContainer container) {
    super(container.$form());
    sensorManager =
      (SensorManager) container.$context().getSystemService(Context.SENSOR_SERVICE);
    orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    sensorManager.registerListener(this, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
    enabled = true;
  }

  // Events

  /**
   * Default OrientationChanged event handler.
   *
   * <p>This event is signalled when the device's orientation has changed.  It
   * reports the new values of yaw, pich, and roll, and it also sets the Yaw, Pitch,
   * and roll properties.</p>
   * <p>Yaw is the compass heading in degrees, pitch indicates how the device
   * is tilted from top to bottom, and roll indicates how much the device is tilted from
   * side to side.</p>
   */
  
  public void OrientationChanged(float yaw, float pitch, float roll) {
    EventDispatcher.dispatchEvent(this, "OrientationChanged", yaw, pitch, roll);
  }

  // Properties

  /**
   * Available property getter method (read-only property).
   *
   * @return {@code true} indicates that an orientation sensor is available,
   *         {@code false} that it isn't
   */
  
  public boolean Available() {
    List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
    return (sensors.size() > 0);
  }

  /**
   * Enabled property getter method.
   *
   * @return {@code true} indicates that the sensor generates events,
   *         {@code false} that it doesn't
   */
  
  public boolean Enabled() {
    return enabled;
  }

  /**
   * Enabled property setter method.
   *
   * @param enabled  {@code true} enables sensor event generation,
   *                 {@code false} disables it
   */
  
  public void Enabled(boolean enabled) {
    this.enabled = enabled;
  }

  /**
   * Pitch property getter method (read-only property).
   *
   * <p>To return meaningful values the sensor must be enabled.</p>
   *
   * @return  current pitch
   */
  
  public float Pitch() {
    return pitch;
  }

  /**
   * Roll property getter method (read-only property).
   *
   * <p>To return meaningful values the sensor must be enabled.</p>
   *
   * @return  current roll
   */
  
  public float Roll() {
    return roll;
  }

  /**
   * Yaw property getter method (read-only property).
   *
   * <p>To return meaningful values the sensor must be enabled.</p>
   *
   * @return  current yaw
   */
 
  public float Yaw() {
    return yaw;
  }

  /**
   * Angle property getter method (read-only property).  Specifically, this
   * provides the angle in which the orientation sensor is tilted, treating
   * {@link #Roll()} as the x-coordinate and {@link #Pitch()} as the
   * y-coordinate.  For the amount of the tilt, use {@link #Magnitude()}.
   *
   * <p>To return meaningful values the sensor must be enabled.</p>
   *
   * @return the angle in degrees
   */
  
  public float Angle() {
    return (float) (180.0 - Math.toDegrees(Math.atan2(pitch, roll)));
  }

  /**
   * Magnitude property getter method (read-only property).  Specifically, this
   * returns a number between 0 and 1, indicating how much the device
   * is tilted.  For the angle of tilt, use {@link #Angle()}.
   *
   * <p>To return meaningful values the sensor must be enabled.</p>
   *
   * @return the magnitude of the tilt, from 0 to 1
   */
  
  public float Magnitude() {
    // Limit pitch and roll to 90; otherwise, the phone is upside down.
    // The official documentation falsely claims that the range of pitch and
    // roll is [-90, 90].  If the device is upside-down, it can range from
    // -180 to 180.  We restrict it to the range [-90, 90].
    // With that restriction, if the pitch and roll angles are P and R, then
    // the force is given by 1 - cos(P)cos(R).  I have found a truly wonderful
    // proof of this theorem, but the margin enforced by Lint is too small to
    // contain it.
    final int MAX_VALUE = 90;
    double npitch = Math.toRadians(Math.min(MAX_VALUE, Math.abs(pitch)));
    double nroll = Math.toRadians(Math.min(MAX_VALUE, Math.abs(roll)));
    return (float) (1.0 - Math.cos(npitch) * Math.cos(nroll));
  }

  // SensorListener implementation

  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
//    Log.d("OrientationSensor", "SensorEvent: " + sensorEvent.sensor.getName() + ":" + sensorEvent.toString());
    if (enabled) {
      final float[] values = sensorEvent.values;
      yaw = values[0];
      pitch = values[1];
      roll = values[2];
      accuracy = sensorEvent.accuracy;
//      Log.d("OrientationSensor", "yaw, pitch, roll: " + yaw + ", " + pitch + ", " + roll);
      OrientationChanged(yaw, pitch, roll);
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // TODO(user): Figure out if we actually need to do something here.
  }

  // Deleteable implementation

  @Override
  public void onDelete() {
    sensorManager.unregisterListener(this);
  }
}
