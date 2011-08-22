package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.ArrayList;
import java.util.List;

// TODO(user) - We need to document what configuration of robot this component will work
// with.

/**
 * A component that provides a high-level interface to a LEGO MINDSTORMS NXT
 * robot, with functions that can move and turn the robot.
 *
 */

public class NxtDrive extends LegoMindstormsNxtBase {

  // Constants for setOutputState parameters.
  private static final int MODE_MOTORON = 0x01;
  private static final int MODE_BRAKE = 0x02;
  private static final int MODE_REGULATED = 0x04;
  private static final int REGULATION_MODE_IDLE = 0x00;
  private static final int REGULATION_MODE_MOTOR_SPEED = 0x01;
  private static final int REGULATION_MODE_MOTOR_SYNC = 0x02;
  private static final int MOTOR_RUN_STATE_IDLE = 0x00;
  private static final int MOTOR_RUN_STATE_RAMPUP = 0x10;
  private static final int MOTOR_RUN_STATE_RUNNING = 0x20;
  private static final int MOTOR_RUN_STATE_RAMPDOWN = 0x40;


  private String driveMotors;
  private List<Integer> driveMotorPorts;
  private double wheelDiameter;
  private boolean stopBeforeDisconnect;

  /**
   * Creates a new NxtDrive component.
   */
  public NxtDrive(ComponentContainer container) {
    super(container, "NxtDrive");

    DriveMotors("CB");  // C & B are the left & right drive motors of the ShooterBot robot.
    WheelDiameter(4.32f);
    StopBeforeDisconnect(true);
  }

  @Override
  public void beforeDisconnect(BluetoothConnectionBase bluetoothConnection) {
    if (stopBeforeDisconnect) {
      for (int port : driveMotorPorts) {
        setOutputState("Disconnect", port, 0,
            MODE_BRAKE, REGULATION_MODE_IDLE, 0, MOTOR_RUN_STATE_IDLE, 0);
      }
    }
  }

  /**
   * Returns the motor ports that are used for driving.
   */
  
  public String DriveMotors() {
    return driveMotors;
  }

  /**
   * Specifies the motor ports that are used for driving.
   */
  
  public void DriveMotors(String motorPortLetters) {
    driveMotors = motorPortLetters;
    driveMotorPorts = new ArrayList<Integer>();
    for (int i = 0; i < motorPortLetters.length(); i++) {
      char ch = motorPortLetters.charAt(i);
      try {
        driveMotorPorts.add(convertMotorPortLetterToNumber(ch));
      } catch (IllegalArgumentException e) {
        form.dispatchErrorOccurredEvent(this, "DriveMotors",
            ErrorMessages.ERROR_NXT_INVALID_MOTOR_PORT, ch);
      }
    }
  }

  /**
   * Returns the diameter of the wheels used for driving.
   */
  
  public float WheelDiameter() {
    return (float) wheelDiameter;
  }

  /**
   * Returns the diameter of the wheels used for driving.
   *
   * @param wheelDiameter the diameter of the wheel
   */
  
  public void WheelDiameter(float wheelDiameter) {
    this.wheelDiameter = wheelDiameter;
  }

  /**
   * Returns whether to stop the drive motors before disconnecting.
   */
  
  public boolean StopBeforeDisconnect() {
    return stopBeforeDisconnect;
  }

  /**
   * Specifies whether to stop the drive motors before disconnecting.
   *
   * @param stopBeforeDisconnect whether to stop the drive motors before disconnecting
   */
 
  public void StopBeforeDisconnect(boolean stopBeforeDisconnect) {
    this.stopBeforeDisconnect = stopBeforeDisconnect;
  }

  
  public void MoveForwardIndefinitely(int power) {
    move("MoveForwardIndefinitely", power, 0L);
  }

  
  public void MoveBackwardIndefinitely(int power) {
    move("MoveBackwardIndefinitely", -power, 0L);
  }

 
  public void MoveForward(int power, double distance) {
    long tachoLimit = (long) (360.0 * distance / (wheelDiameter * Math.PI));
    // This doesn't work accurately, but it is the best we can do with bluetooth direct commands.
    move("MoveForward", power, tachoLimit);
  }

  
  public void MoveBackward(int power, double distance) {
    long tachoLimit = (long) (360.0 * distance / (wheelDiameter * Math.PI));
    // This doesn't work accurately, but it is the best we can do with bluetooth direct commands.
    move("MoveBackward", -power, tachoLimit);
  }

  private void move(String functionName, int power, long tachoLimit) {
    if (!checkBluetooth(functionName)) {
      return;
    }

    for (int port : driveMotorPorts) {
      setOutputState(functionName, port, power,
          MODE_MOTORON, REGULATION_MODE_MOTOR_SPEED, 0, MOTOR_RUN_STATE_RUNNING, tachoLimit);
    }
  }

 
  public void TurnClockwiseIndefinitely(int power) {
    int numDriveMotors = driveMotorPorts.size();
    if (numDriveMotors >= 2) {
      int forwardMotorIndex = 0;                    // left
      int backwardMotorIndex = numDriveMotors - 1;  // right
      turnIndefinitely("TurnClockwiseIndefinitely", power, forwardMotorIndex, backwardMotorIndex);
    }
  }

  
  public void TurnCounterClockwiseIndefinitely(int power) {
    int numDriveMotors = driveMotorPorts.size();
    if (numDriveMotors >= 2) {
      int forwardMotorIndex = numDriveMotors - 1;  // right
      int backwardMotorIndex = 0;                  // left
      turnIndefinitely("TurnCounterClockwiseIndefinitely", power, forwardMotorIndex,
          backwardMotorIndex);
    }
  }

  private void turnIndefinitely(String functionName, int power, int forwardMotorIndex,
      int reverseMotorIndex) {
    if (!checkBluetooth(functionName)) {
      return;
    }

    setOutputState(functionName, driveMotorPorts.get(forwardMotorIndex), power,
        MODE_MOTORON, REGULATION_MODE_MOTOR_SPEED, 0, MOTOR_RUN_STATE_RUNNING, 0);
    setOutputState(functionName, driveMotorPorts.get(reverseMotorIndex), -power,
        MODE_MOTORON, REGULATION_MODE_MOTOR_SPEED, 0, MOTOR_RUN_STATE_RUNNING, 0);
  }

  // TODO(user) - it would be nice to have TurnClockwise and TurnCounterClockwise (or
  // TurnRight and TurnLeft?) methods that take an angle (in degrees). I think we'd need to know
  // the distance between the drive wheels, so that could be a property (similar to
  // the WheelDiameter property).

  
  public void Stop() {
    String functionName = "Stop";
    if (!checkBluetooth(functionName)) {
      return;
    }

    for (int port : driveMotorPorts) {
      setOutputState(functionName, port, 0,
          MODE_BRAKE, REGULATION_MODE_IDLE, 0, MOTOR_RUN_STATE_IDLE, 0);
    }
  }
}
