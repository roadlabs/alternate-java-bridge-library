package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

/**
 * A component that provides a high-level interface to a color sensor on a LEGO
 * MINDSTORMS NXT robot.
 *
 */

public class NxtColorSensor extends LegoMindstormsNxtSensor implements Deleteable {

  private enum State { UNKNOWN, BELOW_RANGE, WITHIN_RANGE, ABOVE_RANGE }
  private static final String DEFAULT_SENSOR_PORT = "3";
  private static final int DEFAULT_BOTTOM_OF_RANGE = 256;
  private static final int DEFAULT_TOP_OF_RANGE = 767;

  static final int SENSOR_TYPE_COLOR_FULL = 0x0D;  // Color detector mode
  static final int SENSOR_TYPE_COLOR_RED = 0x0E;   // Light sensor mode with red light on
  static final int SENSOR_TYPE_COLOR_GREEN = 0x0F; // Light sensor mode with green light on
  static final int SENSOR_TYPE_COLOR_BLUE = 0x10;  // Light sensor mode with blue light on
  static final int SENSOR_TYPE_COLOR_NONE = 0x11;  // Light sensor mode with no light

  private static final Map<Integer, Integer> mapColorToSensorType;
  private static final Map<Integer, Integer> mapSensorValueToColor;
  static {
    mapColorToSensorType = new HashMap<Integer, Integer>();
    mapColorToSensorType.put(Component.COLOR_RED, SENSOR_TYPE_COLOR_RED);
    mapColorToSensorType.put(Component.COLOR_GREEN, SENSOR_TYPE_COLOR_GREEN);
    mapColorToSensorType.put(Component.COLOR_BLUE, SENSOR_TYPE_COLOR_BLUE);
    mapColorToSensorType.put(Component.COLOR_NONE, SENSOR_TYPE_COLOR_NONE);

    mapSensorValueToColor = new HashMap<Integer, Integer>();
    mapSensorValueToColor.put(0x01, Component.COLOR_BLACK);
    mapSensorValueToColor.put(0x02, Component.COLOR_BLUE);
    mapSensorValueToColor.put(0x03, Component.COLOR_GREEN);
    mapSensorValueToColor.put(0x04, Component.COLOR_YELLOW);
    mapSensorValueToColor.put(0x05, Component.COLOR_RED);
    mapSensorValueToColor.put(0x06, Component.COLOR_WHITE);
  }

  private boolean detectColor;
  private Handler handler;
  private final Runnable sensorReader;

  // Fields related to detecting color
  private int previousColor;
  private boolean colorChangedEventEnabled;

  // Fields related to detecting light
  private State previousState;
  private int bottomOfRange;
  private int topOfRange;
  private boolean belowRangeEventEnabled;
  private boolean withinRangeEventEnabled;
  private boolean aboveRangeEventEnabled;
  private int generateColor;

  /**
   * Creates a new NxtColorSensor component.
   */
  public NxtColorSensor(ComponentContainer container) {
    super(container, "NxtColorSensor");
    handler = new Handler();
    previousState = State.UNKNOWN;
    previousColor = Component.COLOR_NONE;
    sensorReader = new Runnable() {
      public void run() {
        if (bluetooth != null && bluetooth.IsConnected()) {
          if (detectColor) {
            // Detecting color
            SensorValue<Integer> sensorValue = getColorValue("");
            if (sensorValue.valid) {
              int currentColor = sensorValue.value;

              if (currentColor != previousColor) {
                ColorChanged(currentColor);
              }

              previousColor = currentColor;
            }

          } else {
            // Detecting light
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
        }
        if (isHandlerNeeded()) {
          handler.post(sensorReader);
        }
      }
    };

    SensorPort(DEFAULT_SENSOR_PORT);

    // Detecting color
    DetectColor(true);
    ColorChangedEventEnabled(false);

    // Detecting light
    BottomOfRange(DEFAULT_BOTTOM_OF_RANGE);
    TopOfRange(DEFAULT_TOP_OF_RANGE);
    BelowRangeEventEnabled(false);
    WithinRangeEventEnabled(false);
    AboveRangeEventEnabled(false);
    GenerateColor(Component.COLOR_NONE);
  }

  @Override
  protected void initializeSensor(String functionName) {
    int sensorType = detectColor ? SENSOR_TYPE_COLOR_FULL : mapColorToSensorType.get(generateColor);
    setInputMode(functionName, port, sensorType, SENSOR_MODE_RAWMODE);
    resetInputScaledValue(functionName, port);
  }

  /**
   * Specifies the sensor port that the sensor is connected to.
   */
  
  public void SensorPort(String sensorPortLetter) {
    setSensorPort(sensorPortLetter);
  }

  /**
   * Returns whether the sensor should detect color or light. True indicates that
   * the sensor should detect color; False indicates that the sensor should
   * detect light.
   *
   * The ColorChanged event will not occur if the DetectColor property is set
   * to False.
   * The BelowRange, WithinRange, and AboveRange events will not occur if the
   * DetectColor property is set to True.
   * The sensor will not generate color when the DetectColor property is set to
   * True.
   */
 
  public boolean DetectColor() {
    return detectColor;
  }

  /**
   * Specifies whether the sensor should detect color light. True indicates
   * that the sensor should detect color; False indicates that the sensor
   * should detect light.
   */
  
  public void DetectColor(boolean detectColor) {
    boolean handlerWasNeeded = isHandlerNeeded();

    this.detectColor = detectColor;
    if (bluetooth != null && bluetooth.IsConnected()) {
      initializeSensor("DetectColor");
    }

    boolean handlerIsNeeded = isHandlerNeeded();
    if (handlerWasNeeded && !handlerIsNeeded) {
      handler.removeCallbacks(sensorReader);
    }
    previousColor = Component.COLOR_NONE;
    previousState = State.UNKNOWN;
    if (!handlerWasNeeded && handlerIsNeeded) {
      handler.post(sensorReader);
    }
  }

  // Methods for detecting color

  
  public int GetColor() {
    String functionName = "GetColor";
    if (!checkBluetooth(functionName)) {
      return Component.COLOR_NONE;
    }
    if (!detectColor) {
      form.dispatchErrorOccurredEvent(this, functionName,
          ErrorMessages.ERROR_NXT_CANNOT_DETECT_COLOR);
      return Component.COLOR_NONE;
    }

    SensorValue<Integer> sensorValue = getColorValue(functionName);
    if (sensorValue.valid) {
      return sensorValue.value;
    }

    // invalid response
    return Component.COLOR_NONE;
  }

  private SensorValue<Integer> getColorValue(String functionName) {
    byte[] returnPackage = getInputValues(functionName, port);
    if (returnPackage != null) {
      boolean valid = getBooleanValueFromBytes(returnPackage, 4);
      if (valid) {
        int scaledValue = getSWORDValueFromBytes(returnPackage, 12);
        if (mapSensorValueToColor.containsKey(scaledValue)) {
          int color = mapSensorValueToColor.get(scaledValue);
          return new SensorValue<Integer>(true, color);
        }
      }
    }

    // invalid response
    return new SensorValue<Integer>(false, null);
  }

  /**
   * Returns whether the ColorChanged event should fire when the DetectColor
   * property is set to True and the detected color changes.
   */
  
  public boolean ColorChangedEventEnabled() {
    return colorChangedEventEnabled;
  }

  /**
   * Specifies whether the ColorChanged event should fire when the DetectColor
   * property is set to True and the detected color changes
   */
  
  public void ColorChangedEventEnabled(boolean enabled) {
    boolean handlerWasNeeded = isHandlerNeeded();

    colorChangedEventEnabled = enabled;

    boolean handlerIsNeeded = isHandlerNeeded();
    if (handlerWasNeeded && !handlerIsNeeded) {
      handler.removeCallbacks(sensorReader);
    }
    if (!handlerWasNeeded && handlerIsNeeded) {
      previousColor = Component.COLOR_NONE;
      handler.post(sensorReader);
    }
  }

  
  public void ColorChanged(int color) {
    EventDispatcher.dispatchEvent(this, "ColorChanged", color);
  }

  // Methods for detecting light

  
  public int GetLightLevel() {
    String functionName = "GetLightLevel";
    if (!checkBluetooth(functionName)) {
      return -1;
    }
    if (detectColor) {
      form.dispatchErrorOccurredEvent(this, functionName,
          ErrorMessages.ERROR_NXT_CANNOT_DETECT_LIGHT);
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
   * Returns whether the BelowRange event should fire when the DetectColor
   * property is set to False and the light level goes below the BottomOfRange.
   */
  
  public boolean BelowRangeEventEnabled() {
    return belowRangeEventEnabled;
  }

  /**
   * Specifies whether the BelowRange event should fire when the DetectColor
   * property is set to False and the light level goes below the BottomOfRange.
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
   * Returns whether the WithinRange event should fire when the DetectColor
   * property is set to False and the light level goes between the
   * BottomOfRange and the TopOfRange.
   */
  
  public boolean WithinRangeEventEnabled() {
    return withinRangeEventEnabled;
  }

  /**
   * Specifies whether the WithinRange event should fire when the DetectColor
   * property is set to False and the light level goes between the
   * BottomOfRange and the TopOfRange.
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
   * Returns whether the AboveRange event should fire when the DetectColor
   * property is set to False and the light level goes above the TopOfRange.
   */
  
  public boolean AboveRangeEventEnabled() {
    return aboveRangeEventEnabled;
  }

  /**
   * Specifies whether the AboveRange event should fire when the DetectColor
   * property is set to False and the light level goes above the TopOfRange.
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

  /**
   * Returns the color that should generated by the sensor.
   * Only None, Red, Green, or Blue are valid values.
   * The sensor will not generate color when the DetectColor property is set to
   * True.
   */
 
  public int GenerateColor() {
    return generateColor;
  }

  /**
   * Specifies the color that should generated by the sensor.
   * Only None, Red, Green, or Blue are valid values.
   * The sensor will not generate color when the DetectColor property is set to
   * True.
   */
  
  public void GenerateColor(int generateColor) {
    String functionName = "GenerateColor";
    if (mapColorToSensorType.containsKey(generateColor)) {
      this.generateColor = generateColor;
      if (bluetooth != null && bluetooth.IsConnected()) {
        initializeSensor(functionName);
      }
    } else {
      form.dispatchErrorOccurredEvent(this, functionName,
          ErrorMessages.ERROR_NXT_INVALID_GENERATE_COLOR);
    }
  }

  private boolean isHandlerNeeded() {
    if (detectColor) {
      return colorChangedEventEnabled;
    } else {
      return belowRangeEventEnabled || withinRangeEventEnabled || aboveRangeEventEnabled;
    }
  }

  // Deleteable implementation

  @Override
  public void onDelete() {
    handler.removeCallbacks(sensorReader);
  }
}
