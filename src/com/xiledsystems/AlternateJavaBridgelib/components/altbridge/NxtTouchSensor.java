package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.os.Handler;

/**
 * A component that provides a high-level interface to a touch sensor on a LEGO
 * MINDSTORMS NXT robot.
 *
 */

public class NxtTouchSensor extends LegoMindstormsNxtSensor implements Deleteable {

  private enum State { UNKNOWN, PRESSED, RELEASED }
  private static final String DEFAULT_SENSOR_PORT = "1";

  private Handler handler;
  private State previousState;
  private final Runnable sensorReader;
  private boolean pressedEventEnabled;
  private boolean releasedEventEnabled;

  /**
   * Creates a new NxtTouchSensor component.
   */
  public NxtTouchSensor(ComponentContainer container) {
    super(container, "NxtTouchSensor");
    handler = new Handler();
    previousState = State.UNKNOWN;
    sensorReader = new Runnable() {
      public void run() {
        if (bluetooth != null && bluetooth.IsConnected()) {
          SensorValue<Boolean> sensorValue = getPressedValue("");
          if (sensorValue.valid) {
            State currentState = sensorValue.value ? State.PRESSED : State.RELEASED;

            if (currentState != previousState) {
              if (currentState == State.PRESSED && pressedEventEnabled) {
                Pressed();
              }
              if (currentState == State.RELEASED && releasedEventEnabled) {
                Released();
              }
            }

            previousState = currentState;
          }
        }
        if (isHandlerNeeded()) {
          handler.post(sensorReader);
        }
      }
    };

    SensorPort(DEFAULT_SENSOR_PORT);
    PressedEventEnabled(false);
    ReleasedEventEnabled(false);
  }

  @Override
  protected void initializeSensor(String functionName) {
    setInputMode(functionName, port, SENSOR_TYPE_SWITCH, SENSOR_MODE_BOOLEANMODE);
  }

  /**
   * Specifies the sensor port that the sensor is connected to.
   */
  
  public void SensorPort(String sensorPortLetter) {
    setSensorPort(sensorPortLetter);
  }

  
  public boolean IsPressed() {
    String functionName = "IsPressed";
    if (!checkBluetooth(functionName)) {
      return false;
    }

    SensorValue<Boolean> sensorValue = getPressedValue(functionName);
    if (sensorValue.valid) {
      return sensorValue.value;
    }

    // invalid response
    return false;
  }

  private SensorValue<Boolean> getPressedValue(String functionName) {
    byte[] returnPackage = getInputValues(functionName, port);
    if (returnPackage != null) {
      boolean valid = getBooleanValueFromBytes(returnPackage, 4);
      if (valid) {
        int scaledValue = getSWORDValueFromBytes(returnPackage, 12);
        return new SensorValue<Boolean>(true, (scaledValue != 0));
      }
    }

    // invalid response
    return new SensorValue<Boolean>(false, null);
  }

  /**
   * Returns whether the Pressed event should fire when the touch sensor is
   * pressed.
   */
  
  public boolean PressedEventEnabled() {
    return pressedEventEnabled;
  }

  /**
   * Specifies whether the Pressed event should fire when the touch sensor is
   * pressed.
   */
  
  public void PressedEventEnabled(boolean enabled) {
    boolean handlerWasNeeded = isHandlerNeeded();

    pressedEventEnabled = enabled;

    boolean handlerIsNeeded = isHandlerNeeded();
    if (handlerWasNeeded && !handlerIsNeeded) {
      handler.removeCallbacks(sensorReader);
    }
    if (!handlerWasNeeded && handlerIsNeeded) {
      previousState = State.UNKNOWN;
      handler.post(sensorReader);
    }
  }

  
  public void Pressed() {
    EventDispatcher.dispatchEvent(this, "Pressed");
  }

  /**
   * Returns whether the Released event should fire when the touch sensor is
   * released.
   */
  
  public boolean ReleasedEventEnabled() {
    return releasedEventEnabled;
  }

  /**
   * Specifies whether the Released event should fire when the touch sensor is
   * released.
   */
  
  public void ReleasedEventEnabled(boolean enabled) {
    boolean handlerWasNeeded = isHandlerNeeded();

    releasedEventEnabled = enabled;

    boolean handlerIsNeeded = isHandlerNeeded();
    if (handlerWasNeeded && !handlerIsNeeded) {
      handler.removeCallbacks(sensorReader);
    }
    if (!handlerWasNeeded && handlerIsNeeded) {
      previousState = State.UNKNOWN;
      handler.post(sensorReader);
    }
  }

  
  public void Released() {
    EventDispatcher.dispatchEvent(this, "Released");
  }

  private boolean isHandlerNeeded() {
    return pressedEventEnabled || releasedEventEnabled;
  }

  // Deleteable implementation

  @Override
  public void onDelete() {
    handler.removeCallbacks(sensorReader);
  }
}
