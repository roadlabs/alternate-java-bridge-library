package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.xiledsystems.AlternateJavaBridgelib.components.SensorComponent;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.AccSensorListener;
import com.xiledsystems.AlternateJavaBridgelib.components.events.EventDispatcher;

/**
 * Physical world component that can detect shaking and measure
 * acceleration in three dimensions.  It is implemented using
 * android.hardware.SensorListener
 * (http://developer.android.com/reference/android/hardware/SensorListener.html).
 *
 * <p>From the Android documentation:
 * "Sensor values are acceleration in the X, Y and Z axis, where the X axis
 * has positive direction toward the right side of the device, the Y axis has
 * positive direction toward the top of the device and the Z axis has
 * positive direction toward the front of the device. The direction of the
 * force of gravity is indicated by acceleration values in the X, Y and Z
 * axes. The typical case where the device is flat relative to the surface of
 * the Earth appears as -STANDARD_GRAVITY in the Z axis and X and Y values
 * close to zero. Acceleration values are given in SI units (m/s^2)."
 *
 */
// TODO(user): ideas - event for knocking

public class AccelerometerSensor extends AndroidNonvisibleComponent
    implements OnStopListener, OnResumeListener, SensorComponent, SensorEventListener, Deleteable, OnDestroySvcListener {

  // Shake threshold - derived by trial
  private static final double SHAKE_THRESHOLD = 8.0;

  // Cache for shake detection
  private static final int SENSOR_CACHE_SIZE = 10;
  private final Queue<Float> X_CACHE = new LinkedList<Float>();
  private final Queue<Float> Y_CACHE = new LinkedList<Float>();
  private final Queue<Float> Z_CACHE = new LinkedList<Float>();

  // Backing for sensor values
  private float xAccel;
  private float yAccel;
  private float zAccel;
  private int delaySpeed = SensorManager.SENSOR_DELAY_GAME;
  private int accuracy;

  // Sensor manager
  private final SensorManager sensorManager;

  // Indicates whether the accelerometer should generate events
  private boolean enabled;
  
  private AccSensorListener listener = null;

  private Sensor accelerometerSensor;

  /**
   * Creates a new AccelerometerSensor component.
   *
   * @param container  ignored (because this is a non-visible component)
   */
  public AccelerometerSensor(ComponentContainer container) {
    super(container);
    container.$form().registerForOnResume(this);
    container.$form().registerForOnStop(this);

    enabled = true;
    sensorManager = (SensorManager) container.$context().getSystemService(Context.SENSOR_SERVICE);
    accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    startListening();
  }
  
  public AccelerometerSensor(SvcComponentContainer sContainer) {
	  super(sContainer);
	  
	  sContainer.$formService().registerForOnDestroy(this);
	  
	  enabled = true;
	  sensorManager = (SensorManager) sContainer.$context().getSystemService(Context.SENSOR_SERVICE);
	  accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	  startListening();	  
  }

  /**
   * Indicates the acceleration changed in the X, Y, and/or Z dimensions.
   */
  
  public void AccelerationChanged(float xAccel, float yAccel, float zAccel) {
    this.xAccel = xAccel;
    this.yAccel = yAccel;
    this.zAccel = zAccel;

    addToSensorCache(X_CACHE, xAccel);
    addToSensorCache(Y_CACHE, yAccel);
    addToSensorCache(Z_CACHE, zAccel);

    if (isShaking(X_CACHE, xAccel) || isShaking(Y_CACHE, yAccel) || isShaking(Z_CACHE, zAccel)) {
      Shaking();
    }
 
    if (listener == null) {
    	EventDispatcher.dispatchEvent(this, "AccelerationChanged", xAccel, yAccel, zAccel);
    } else {
    	listener.sensorReceived(xAccel, yAccel, zAccel);
    }
  }

  /**
   * Indicates the device started being shaken or continues to be shaken.
   */
  
  public void Shaking() {
    EventDispatcher.dispatchEvent(this, "Shaking");
  }
  
  /**
   * Set a custom listener for the acceleration changed event.
   * It will NOT throw the AccelerationChanged event, but instead
   * run the sensorReceived() method insted. (implement 
   * AccSensorListener in the Form).
   * 
   * @param listener
   */
  public void Listener(AccSensorListener listener) {
	  this.listener = listener;
  }

  /**
   * Available property getter method (read-only property).
   *
   * @return {@code true} indicates that an accelerometer sensor is available,
   *         {@code false} that it isn't
   */
  
  public boolean Available() {
    List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
    return (sensors.size() > 0);
  }

  /**
   * If true, the sensor will generate events.  Otherwise, no events
   * are generated even if the device is accelerated or shaken.
   *
   * @return {@code true} indicates that the sensor generates events,
   *         {@code false} that it doesn't
   */
  
  public boolean Enabled() {
    return enabled;
  }
  
  public void MaxSpeed(boolean maxSpeed) {
	  if (maxSpeed) {
		  delaySpeed = SensorManager.SENSOR_DELAY_FASTEST;
		  if (enabled) {
			  stopListening();
			  startListening();
		  }
	  } else {
		  delaySpeed = SensorManager.SENSOR_DELAY_GAME;
		  if (enabled) {
			  stopListening();
			  startListening();
		  }
	  }
  }

  // Assumes that sensorManager has been initialized, which happens in constructor
  private void startListening() {
    sensorManager.registerListener(this, accelerometerSensor, delaySpeed);
  }

  // Assumes that sensorManager has been initialized, which happens in constructor
  private void stopListening() {
    sensorManager.unregisterListener(this);
  }

  /**
   * Specifies whether the sensor should generate events.  If true,
   * the sensor will generate events.  Otherwise, no events are
   * generated even if the device is accelerated or shaken.
   *
   * @param enabled  {@code true} enables sensor event generation,
   *                 {@code false} disables it
   */
  
  public void Enabled(boolean enabled) {
    if (this.enabled == enabled) {
      return;
    }
    this.enabled = enabled;
    if (enabled) {
      startListening();
    } else {
      stopListening();
    }
  }

  /**
   * Returns the acceleration in the X-dimension in SI units (m/s^2).
   * The sensor must be enabled to return meaningful values.
   *
   * @return  X acceleration
   */
  
  public float XAccel() {
    return xAccel;
  }

  /**
   * Returns the acceleration in the Y-dimension in SI units (m/s^2).
   * The sensor must be enabled to return meaningful values.
   *
   * @return  Y acceleration
   */
  
  public float YAccel() {
    return yAccel;
  }

  /**
   * Returns the acceleration in the Z-dimension in SI units (m/s^2).
   * The sensor must be enabled to return meaningful values.
   *
   * @return  Z acceleration
   */
  
  public float ZAccel() {
    return zAccel;
  }

  /*
   * Updating sensor cache, replacing oldest values.
   */
  private void addToSensorCache(Queue<Float> cache, float value) {
    if (cache.size() >= SENSOR_CACHE_SIZE) {
      cache.remove();
    }
    cache.add(value);
  }

  /*
   * Indicates whether there was a sudden, unusual movement.
   */
  // TODO(user): Maybe this can be improved.
  // See http://www.utdallas.edu/~rxb023100/pubs/Accelerometer_WBSN.pdf.
  private boolean isShaking(Queue<Float> cache, float currentValue) {
    float average = 0;
    for (float value : cache) {
      average += value;
    }

    average /= cache.size();

    return Math.abs(average - currentValue) > SHAKE_THRESHOLD;
  }

  // SensorListener implementation
  @Override
  public void onSensorChanged(SensorEvent sensorEvent) {
    if (enabled) {
      final float[] values = sensorEvent.values;
      xAccel = values[0];
      yAccel = values[1];
      zAccel = values[2];
      accuracy = sensorEvent.accuracy;
      AccelerationChanged(xAccel, yAccel, zAccel);
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // TODO(user): Figure out if we actually need to do something here.
  }

  // OnResumeListener implementation

  @Override
  public void onResume() {
    if (enabled) {
      startListening();
    }
  }

  // OnStopListener implementation

  @Override
  public void onStop() {
    if (enabled) {
      stopListening();
    }
  }

  // Deleteable implementation

  @Override
  public void onDelete() {
    if (enabled) {
      stopListening();
    }
  }

@Override
public void onDestroy() {
	if (enabled) {
		stopListening();
	}
}
}
