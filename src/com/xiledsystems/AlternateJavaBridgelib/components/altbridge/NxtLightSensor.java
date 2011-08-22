package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.os.Handler;

/**
 * A component that provides a high-level interface to a light sensor on a LEGO
 * MINDSTORMS NXT robot.
 *
 */

public class NxtLightSensor extends LegoMindstormsNxtSensor implements Deleteable {

  private enum State { UNKNOWN, BELOW_RANGE, WITHIN_RANGE, ABOVE_RANGE }
  private static final String DEFAULT_SENSOR_PORT = "3";
  private static final int DEFAULT_BOTTOM_OF_RANGE = 256;
  private static final int DEFAULT_TOP_OF_RANGE = 767;

  private Handler handler;
  private final Runnable sensorReader;
  private State previousState;
  private int bottomOfRange;
  private int topOfRange;
  private boolean belowRangeEventEnabled;
  private boolean withinRangeEventEnabled;
  private boolean aboveRangeEventEnabled;
  private boolean generateLight;

  /**
   * Creates a new NxtLightSensor component.
   */
  public NxtLightSensor(ComponentContainer container) {
    super(container, "NxtLightSensor");
    handler = new Handler();
    previousState = State.UNKNOWN;
    sensorReader = new Runnable() {
      public void run() {
        if (bluetooth != null && bluetooth.IsConnected()) {
          SensorValue<Integer> sensorValue = getLightValue("");
          if (sensorValue.valid) {
            State currentState;
            if (sensorValue.value < bottomOfRange) {
              currentState = State.BELOW_RANGE;
            } else if (sensorValue.value > topOfRange) {
              currentState = State.ABOVE_RANGE;
            } else {
              currentState = State.WITHIN_RANGE;
            }

            if (currentState != previousState) {
              if (currentState == State.BELOW_RANGE && belowRangeEventEnabled) {
                BelowRange();
              }
              if (currentState == State.WITHIN_RANGE && withinRangeEventEnabled) {
                WithinRange();
              }
              if (currentState == State.ABOVE_RANGE && aboveRangeEventEnabled) {
                AboveRange();
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
    BottomOfRange(DEFAULT_BOTTOM_OF_RANGE);
    TopOfRange(DEFAULT_TOP_OF_RANGE);
    BelowRangeEventEnabled(false);
    WithinRangeEventEnabled(false);
    AboveRangeEventEnabled(false);
    GenerateLight(false);
  }

  @Override
  protected void initializeSensor(String functionName) {
    setInputMode(functionName, port,
        generateLight ? SENSOR_TYPE_LIGHT_ACTIVE : SENSOR_TYPE_LIGHT_INACTIVE,
        SENSOR_MODE_PCTFULLSCALEMODE);
  }

  /**
   * Specifies the sensor port that the sensor is connected to.
   */
  
  public void SensorPort(String sensorPortLetter) {
    setSensorPort(sensorPortLetter);
  }

  /**
   * Returns whether the light sensor should generate light.
   */
  
  public boolean GenerateLight() {
    return generateLight;
  }

  /**
   * Specifies whether the light sensor should generate light.
   */
  
  public void GenerateLight(boolean generateLight) {
    this.generateLight = generateLight;
    if (bluetooth != null && bluetooth.IsConnected()) {
      initializeSensor("GenerateLight");
    }
  }

  
  public int GetLightLevel() {
    String functionName = "GetLightLevel";
    if (!checkBluetooth(functionName)) {
      return -1;
    }

    SensorValue<Integer> sensorValue = getLightValue(functionName);
    if (sensorValue.valid) {
      return sensorValue.value;
    }

    // invalid response
    return -1;
  }

  private SensorValue<Integer> getLightValue(String functionName) {
    byte[] returnPackage = getInputValues(functionName, port);
    if (returnPackage != null) {
      boolean valid = getBooleanValueFromBytes(returnPackage, 4);
      if (valid) {
        int normalizedValue = getUWORDValueFromBytes(returnPackage, 10);
        return new SensorValue<Integer>(true, normalizedValue);
      }
    }

    // invalid response
    return new SensorValue<Integer>(false, null);
  }

  /**
   * Returns the bottom of the range used for the BelowRange, WithinRange,
   * and AboveRange events.
   */
  
  public int BottomOfRange() {
    return bottomOfRange;
  }

  /**
   * Specifies the bottom of the range used for the BelowRange, WithinRange,
   * and AboveRange events.
   */
  
  public void BottomOfRange(int bottomOfRange) {
    this.bottomOfRange = bottomOfRange;
    previousState = State.UNKNOWN;
  }

  /**
   * Returns the top of the range used for the BelowRange, WithinRange, and
   * AboveRange events.
   */
  
  public int TopOfRange() {
    return topOfRange;
  }

  /**
   * Specifies the top of the range used for the BelowRange, WithinRange, and
   * AboveRange events.
   */
 
  public void TopOfRange(int topOfRange) {
    this.topOfRange = topOfRange;
    previousState = State.UNKNOWN;
  }

  /**
   * Returns whether the BelowRange event should fire when the light level
   * goes below the BottomOfRange.
   */
  
  public boolean BelowRangeEventEnabled() {
    return belowRangeEventEnabled;
  }

  /**
   * Specifies whether the BelowRange event should fire when the light level
   * goes below the BottomOfRange.
   */
 
  public void BelowRangeEventEnabled(boolean enabled) {
    boolean handlerWasNeeded = isHandlerNeeded();

    belowRangeEventEnabled = enabled;

    boolean handlerIsNeeded = isHandlerNeeded();
    if (handlerWasNeeded && !handlerIsNeeded) {
      handler.removeCallbacks(sensorReader);
    }
    if (!handlerWasNeeded && handlerIsNeeded) {
      previousState = State.UNKNOWN;
      handler.post(sensorReader);
    }
  }

  
  public void BelowRange() {
    EventDispatcher.dispatchEvent(this, "BelowRange");
  }

  /**
   * Returns whether the WithinRange event should fire when the light level
   * goes between the BottomOfRange and the TopOfRange.
   */
 
  public boolean WithinRangeEventEnabled() {
    return withinRangeEventEnabled;
  }

  /**
   * Specifies whether the WithinRange event should fire when the light level
   * goes between the BottomOfRange and the TopOfRange.
   */
 
  public void WithinRangeEventEnabled(boolean enabled) {
    boolean handlerWasNeeded = isHandlerNeeded();

    withinRangeEventEnabled = enabled;

    boolean handlerIsNeeded = isHandlerNeeded();
    if (handlerWasNeeded && !handlerIsNeeded) {
      handler.removeCallbacks(sensorReader);
    }
    if (!handlerWasNeeded && handlerIsNeeded) {
      previousState = State.UNKNOWN;
      handler.post(sensorReader);
    }
  }

  
  public void WithinRange() {
    EventDispatcher.dispatchEvent(this, "WithinRange");
  }

  /**
   * Returns whether the AboveRange event should fire when the light level
   * goes above the TopOfRange.
   */
  
  public boolean AboveRangeEventEnabled() {
    return aboveRangeEventEnabled;
  }

  /**
   * Specifies whether the AboveRange event should fire when the light level
   * goes above the TopOfRange.
   */
  
  public void AboveRangeEventEnabled(boolean enabled) {
    boolean handlerWasNeeded = isHandlerNeeded();

    aboveRangeEventEnabled = enabled;

    boolean handlerIsNeeded = isHandlerNeeded();
    if (handlerWasNeeded && !handlerIsNeeded) {
      handler.removeCallbacks(sensorReader);
    }
    if (!handlerWasNeeded && handlerIsNeeded) {
      previousState = State.UNKNOWN;
      handler.post(sensorReader);
    }
  }

  
  public void AboveRange() {
    EventDispatcher.dispatchEvent(this, "AboveRange");
  }

  private boolean isHandlerNeeded() {
    return belowRangeEventEnabled || withinRangeEventEnabled || aboveRangeEventEnabled;
  }

  // Deleteable implementation

  @Override
  public void onDelete() {
    handler.removeCallbacks(sensorReader);
  }
}
