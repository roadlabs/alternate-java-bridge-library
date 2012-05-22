package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.BluetoothReflection;
import com.xiledsystems.AlternateJavaBridgelib.components.util.ErrorMessages;

/**
 * An abstract base class for the BluetoothClient and BluetoothServer
 * component.
 *
 */

public abstract class BluetoothConnectionBase extends AndroidNonvisibleComponent
    implements Component, Deleteable {

  protected final String logTag;
  private final List<BluetoothConnectionListener> bluetoothConnectionListeners =
      new ArrayList<BluetoothConnectionListener>();

  private ByteOrder byteOrder;
  private String encoding;
  private byte delimiter;

  private Object connectedBluetoothSocket;
  private InputStream inputStream;
  private OutputStream outputStream;

  /**
   * Creates a new BluetoothConnectionBase.
   */
  protected BluetoothConnectionBase(ComponentContainer container, String logTag) {
    this(container, logTag, 0);
  }

  private BluetoothConnectionBase(ComponentContainer container, String logTag, int i) {
    super(container);
    this.logTag = logTag;

    HighByteFirst(false); // Lego Mindstorms NXT is low-endian, so false is a good default.
    CharacterEncoding("UTF-8");
    DelimiterByte(0);
  }

  /**
   * This constructor is for testing purposes only.
   */
  protected BluetoothConnectionBase(OutputStream outputStream, InputStream inputStream) {
    this((Form) null, (String) null);
    this.connectedBluetoothSocket = "Not Null";
    this.outputStream = outputStream;
    this.inputStream = inputStream;
  }

  /**
   * Adds a {@link BluetoothConnectionListener} to the listener list.
   *
   * @param listener  the {@code BluetoothConnectionListener} to be added
   */
  void addBluetoothConnectionListener(BluetoothConnectionListener listener) {
    bluetoothConnectionListeners.add(listener);
  }

  /**
   * Removes a {@link BluetoothConnectionListener} from the listener list.
   *
   * @param listener  the {@code BluetoothConnectionListener} to be removed
   */
  void removeBluetoothConnectionListener(BluetoothConnectionListener listener) {
    bluetoothConnectionListeners.remove(listener);
  }

  private void fireAfterConnectEvent() {
    for (BluetoothConnectionListener listener : bluetoothConnectionListeners) {
      listener.afterConnect(this);
    }
  }

  private void fireBeforeDisconnectEvent() {
    for (BluetoothConnectionListener listener : bluetoothConnectionListeners) {
      listener.beforeDisconnect(this);
    }
  }

  /**
   * Default Initialize
   */
  public final void Initialize() {
  }

  
  public void BluetoothError(String functionName, String message) {
  }

  protected void bluetoothError(String functionName, int errorNumber, Object... messageArgs) {
    container.$form().dispatchErrorOccurredEvent(this, functionName, errorNumber, messageArgs);
  }

  /**
   * Returns true if Bluetooth is available on the device, false otherwise.
   *
   * @return true if Bluetooth is available on the device, false otherwise
   */
  
  public boolean Available() {
    Object bluetoothAdapter = BluetoothReflection.getBluetoothAdapter();
    if (bluetoothAdapter != null) {
      return true;
    }
    return false;
  }

  /**
   * Returns true if Bluetooth is enabled, false otherwise.
   *
   * @return true if Bluetooth is enabled, false otherwise
   */
  
  public boolean Enabled() {
    Object bluetoothAdapter = BluetoothReflection.getBluetoothAdapter();
    if (bluetoothAdapter != null) {
      if (BluetoothReflection.isBluetoothEnabled(bluetoothAdapter)) {
        return true;
      }
    }
    return false;
  }

  protected final void setConnection(Object bluetoothSocket) throws IOException {
    connectedBluetoothSocket = bluetoothSocket;
    inputStream = new BufferedInputStream(
        BluetoothReflection.getInputStream(connectedBluetoothSocket));
    outputStream = new BufferedOutputStream(
        BluetoothReflection.getOutputStream(connectedBluetoothSocket));
    fireAfterConnectEvent();
  }

  /**
   * Disconnects from the connected Bluetooth device.
   */
  
  public final void Disconnect() {
    if (connectedBluetoothSocket != null) {
      fireBeforeDisconnectEvent();
      try {
        BluetoothReflection.closeBluetoothSocket(connectedBluetoothSocket);
        Log.i(logTag, "Disconnected from Bluetooth device.");
      } catch (IOException e) {
        Log.w(logTag, "Error while disconnecting: " + e.getMessage());
      }
      connectedBluetoothSocket = null;
    }
    inputStream = null;
    outputStream = null;
  }

  /**
   * Returns true if a connection to a Bluetooth device has been made.
   */
  
  public final boolean IsConnected() {
    return (connectedBluetoothSocket != null);
  }

  /**
   * Returns true if numbers are sent and received with the most significant
   * byte first.
   *
   * @return  {@code true} for high byte first, {@code false} for low byte
   *          first
   */
  
  public boolean HighByteFirst() {
    return byteOrder == ByteOrder.BIG_ENDIAN;
  }

  /**
   * Specifies whether numbers are sent and received with the most significant
   * byte first.
   *
   * @param highByteFirst  {@code true} for high byte first, {@code false} for
   *        low byte first
   */
  
  public void HighByteFirst(boolean highByteFirst) {
    byteOrder = highByteFirst ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
  }

  /**
   * Sets the character encoding to use when sending and receiving text.
   */
  
  public void CharacterEncoding(String encoding) {
    try {
      // Check whether the new encoding is supported.
      "check".getBytes(encoding);
      this.encoding = encoding;
    } catch (UnsupportedEncodingException e) {
      bluetoothError("CharacterEncoding",
          ErrorMessages.ERROR_BLUETOOTH_UNSUPPORTED_ENCODING, encoding);
    }
  }

  /**
   * Returns the character encoding to use when sending and receiving text.
   */
 
  public String CharacterEncoding() {
    return encoding;
  }

  /**
   * Sets the delimiter byte to use when passing a negative number for the
   * numberOfBytes parameter when calling ReceiveText, ReceiveSignedBytes, or
   * ReceiveUnsignedBytes.
   */
  
  public void DelimiterByte(int number) {
    String functionName = "DelimiterByte";
    int n = number;
    byte b = (byte) n;
    n = n >> 8;
    if (n != 0 && n != -1) {
      bluetoothError(functionName,
          ErrorMessages.ERROR_BLUETOOTH_COULD_NOT_FIT_NUMBER_IN_BYTE, number);
      return;
    }
    delimiter = b;
  }

  /**
   * Returns the delimiter byte to use when passing a negative number for the
   * numberOfBytes parameter when calling ReceiveText, ReceiveSignedBytes, or
   * ReceiveUnsignedBytes.
   */
  
  public int DelimiterByte() {
    return delimiter;
  }

  /**
   * Converts the given text to bytes and writes them to the output stream.
   *
   * @param text the text to write
   */
  
  public void SendText(String text) {
    byte[] bytes;
    try {
      bytes = text.getBytes(encoding);
    } catch (UnsupportedEncodingException e) {
      Log.w(logTag, "UnsupportedEncodingException: " + e.getMessage());
      bytes = text.getBytes();
    }
    write("SendText", bytes);
  }

  // TODO(user) - The following three methods take a number as a String parameter so that the
  // user can easily enter hex numbers as 0x12AB. After the blocks editor allows numbers to be
  // entered as hex, we can change String to int/long.

  /**
   * Decodes the given number String to an integer and writes it as one byte
   * to the output stream.
   *
   * If the number could not be decoded to an integer, or the integer would not
   * fit in one byte, then the Form's ErrorOccurred event is triggered and this
   * method returns without writing any bytes to the output stream.
   *
   * @param number the number to write
   */
  
  public void Send1ByteNumber(String number) {
    String functionName = "Send1ByteNumber";
    int n;
    try {
      n = Integer.decode(number);
    } catch (NumberFormatException e) {
      bluetoothError(functionName,
          ErrorMessages.ERROR_BLUETOOTH_COULD_NOT_DECODE, number);
      return;
    }
    byte b = (byte) n;
    n = n >> 8;
    if (n != 0 && n != -1) {
      bluetoothError(functionName,
          ErrorMessages.ERROR_BLUETOOTH_COULD_NOT_FIT_NUMBER_IN_BYTE, number);
      return;
    }
    write(functionName, b);
  }

  /**
   * Decodes the given number String to an integer and writes it as two bytes
   * to the output stream.
   *
   * If the number could not be decoded to an integer, or the integer would not
   * fit in two bytes, then the Form's ErrorOccurred event is triggered and this
   * method returns without writing any bytes to the output stream.
   *
   * @param number the number to write
   */
 
  public void Send2ByteNumber(String number) {
    String functionName = "Send2ByteNumber";
    int n;
    try {
      n = Integer.decode(number);
    } catch (NumberFormatException e) {
      bluetoothError(functionName,
          ErrorMessages.ERROR_BLUETOOTH_COULD_NOT_DECODE, number);
      return;
    }
    byte[] bytes = new byte[2];
    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      bytes[1] = (byte) (n & 0xFF); // low byte
      n = n >> 8;
      bytes[0] = (byte) (n & 0xFF); // high byte
    } else {
      bytes[0] = (byte) (n & 0xFF); // low byte
      n = n >> 8;
      bytes[1] = (byte) (n & 0xFF); // high byte
    }
    n = n >> 8;
    if (n != 0 && n != -1) {
      bluetoothError(functionName,
          ErrorMessages.ERROR_BLUETOOTH_COULD_NOT_FIT_NUMBER_IN_BYTES, number, 2);
      return;
    }
    write(functionName, bytes);
  }

  /**
   * Decodes the given number String to an integer and writes it as four bytes
   * to the output stream.
   *
   * If the number could not be decoded to an integer, or the integer would not
   * fit in four bytes, then the Form's ErrorOccurred event is triggered and this
   * method returns without writing any bytes to the output stream.
   *
   * @param number the number to write
   */
  
  public void Send4ByteNumber(String number) {
    String functionName = "Send4ByteNumber";
    long n;
    try {
      n = Long.decode(number);
    } catch (NumberFormatException e) {
      bluetoothError(functionName,
          ErrorMessages.ERROR_BLUETOOTH_COULD_NOT_DECODE, number);
      return;
    }
    byte[] bytes = new byte[4];
    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      bytes[3] = (byte) (n & 0xFF); // low byte
      n = n >> 8;
      bytes[2] = (byte) (n & 0xFF);
      n = n >> 8;
      bytes[1] = (byte) (n & 0xFF);
      n = n >> 8;
      bytes[0] = (byte) (n & 0xFF); // high byte
    } else {
      bytes[0] = (byte) (n & 0xFF); // low byte
      n = n >> 8;
      bytes[1] = (byte) (n & 0xFF);
      n = n >> 8;
      bytes[2] = (byte) (n & 0xFF);
      n = n >> 8;
      bytes[3] = (byte) (n & 0xFF); // high byte
    }
    n = n >> 8;
    if (n != 0 && n != -1) {
      bluetoothError(functionName,
          ErrorMessages.ERROR_BLUETOOTH_COULD_NOT_FIT_NUMBER_IN_BYTES, number, 4);
      return;
    }
    write(functionName, bytes);
  }

  /**
   * Takes each element from the given list, converts it to a String, decodes
   * the String to an integer, and writes it as one byte to the output stream.
   *
   * If an element could not be decoded to an integer, or the integer would not
   * fit in one byte, then the Form's ErrorOccurred event is triggered and this
   * method returns without writing any bytes to the output stream.
   *
   * @param list the list of numeric values to write
   */
  
  public void SendBytes(ArrayList<Object> list) {
    String functionName = "SendBytes";
    Object[] array = list.toArray();
    byte[] bytes = new byte[array.length];
    for (int i = 0; i < array.length; i++) {
      // We use Object.toString here because the element might be a String or it might be some
      // numeric class.
      Object element = array[i];
      String s = element.toString();
      int n;
      try {
        n = Integer.decode(s);
      } catch (NumberFormatException e) {
        bluetoothError(functionName,
            ErrorMessages.ERROR_BLUETOOTH_COULD_NOT_DECODE_ELEMENT, i + 1);
        return;
      }
      bytes[i] = (byte) (n & 0xFF);
      n = n >> 8;
      if (n != 0 && n != -1) {
        bluetoothError(functionName,
            ErrorMessages.ERROR_BLUETOOTH_COULD_NOT_FIT_ELEMENT_IN_BYTE, i + 1);
        return;
      }
    }
    write(functionName, bytes);
  }

  /**
   * Writes the given byte to the output stream.
   *
   * @param functionName the name of the SimpleFunction calling this method
   * @param b the byte to write
   */
  protected void write(String functionName, byte b) {
    if (!IsConnected()) {
      bluetoothError(functionName,
          ErrorMessages.ERROR_BLUETOOTH_NOT_CONNECTED_TO_DEVICE);
      return;
    }

    try {
      outputStream.write(b);
      outputStream.flush();
    } catch (IOException e) {
      bluetoothError(functionName,
          ErrorMessages.ERROR_BLUETOOTH_UNABLE_TO_WRITE, e.getMessage());
    }
  }

  /**
   * Writes the given bytes to the output stream.
   *
   * @param functionName the name of the SimpleFunction calling this method
   * @param bytes the bytes to write
   */
  protected void write(String functionName, byte[] bytes) {
    if (!IsConnected()) {
      bluetoothError(functionName,
          ErrorMessages.ERROR_BLUETOOTH_NOT_CONNECTED_TO_DEVICE);
      return;
    }

    try {
      outputStream.write(bytes);
      outputStream.flush();
    } catch (IOException e) {
      bluetoothError(functionName,
          ErrorMessages.ERROR_BLUETOOTH_UNABLE_TO_WRITE, e.getMessage());
    }
  }

  /**
   * Returns number of bytes available from the input stream.
   */
  
  public int BytesAvailableToReceive() {
    String functionName = "BytesAvailableToReceive";
    if (!IsConnected()) {
      bluetoothError(functionName,
          ErrorMessages.ERROR_BLUETOOTH_NOT_CONNECTED_TO_DEVICE);
      return 0;
    }

    try {
      return inputStream.available();
    } catch (IOException e) {
      bluetoothError(functionName,
          ErrorMessages.ERROR_BLUETOOTH_UNABLE_TO_READ, e.getMessage());
      return 0;
    }
  }

  /**
   * Reads a number of bytes from the input stream and converts them to text.
   *
   * If numberOfBytes is negative, read until a delimiter byte value is read.
   *
   * @param numberOfBytes the number of bytes to read; a negative number
   *        indicates to read until a delimiter byte value is read
   */
  
  public String ReceiveText(int numberOfBytes) {
    byte[] bytes = read("ReceiveText", numberOfBytes);
    try {
      if (numberOfBytes < 0) {
        // bytes contains a trailing delimiter byte that we ignore when converting to String.
        return new String(bytes, 0, bytes.length - 1, encoding);
      } else {
        return new String(bytes, encoding);
      }
    } catch (UnsupportedEncodingException e) {
      Log.w(logTag, "UnsupportedEncodingException: " + e.getMessage());
      return new String(bytes);
    }
  }

  /**
   * Reads a signed 1-byte number.
   */
  
  public int ReceiveSigned1ByteNumber() {
    byte[] bytes = read("ReceiveSigned1ByteNumber", 1);
    if (bytes.length != 1) {
      return 0; // an error occurred
    }

    return bytes[0];
  }

  /**
   * Reads an unsigned 1-byte number.
   */
  
  public int ReceiveUnsigned1ByteNumber() {
    byte[] bytes = read("ReceiveUnsigned1ByteNumber", 1);
    if (bytes.length != 1) {
      return 0; // an error occurred
    }

    return bytes[0] & 0xFF;
  }

  /**
   * Reads a signed 2-byte number.
   */
  
  public int ReceiveSigned2ByteNumber() {
    byte[] bytes = read("ReceiveSigned2ByteNumber", 2);
    if (bytes.length != 2) {
      return 0; // an error occurred
    }

    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      return (bytes[1] & 0xFF) | (bytes[0] << 8);
    } else {
      return (bytes[0] & 0xFF) | (bytes[1] << 8);
    }
  }

  /**
   * Reads an unsigned 2-byte number.
   */
  
  public int ReceiveUnsigned2ByteNumber() {
    byte[] bytes = read("ReceiveUnsigned2ByteNumber", 2);
    if (bytes.length != 2) {
      return 0; // an error occurred
    }

    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      return (bytes[1] & 0xFF) | ((bytes[0] & 0xFF) << 8);
    } else {
      return (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8);
    }
  }

  /**
   * Reads a signed 4-byte number.
   */
  
  public long ReceiveSigned4ByteNumber() {
    byte[] bytes = read("ReceiveSigned4ByteNumber", 4);
    if (bytes.length != 4) {
      return 0; // an error occurred
    }

    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      return (bytes[3] & 0xFF) |
          ((bytes[2] & 0xFF) << 8) |
          ((bytes[1] & 0xFF) << 16) |
          (bytes[0] << 24);
    } else {
      return (bytes[0] & 0xFF) |
          ((bytes[1] & 0xFF) << 8) |
          ((bytes[2] & 0xFF) << 16) |
          (bytes[3] << 24);
    }
  }

  /**
   * Reads an unsigned 4-byte number.
   */
  
  public long ReceiveUnsigned4ByteNumber() {
    byte[] bytes = read("ReceiveUnsigned4ByteNumber", 4);
    if (bytes.length != 4) {
      return 0; // an error occurred
    }

    if (byteOrder == ByteOrder.BIG_ENDIAN) {
      return (bytes[3] & 0xFFL) |
          ((bytes[2] & 0xFFL) << 8) |
          ((bytes[1] & 0xFFL) << 16) |
          ((bytes[0] & 0xFFL) << 24);
    } else {
      return (bytes[0] & 0xFFL) |
          ((bytes[1] & 0xFFL) << 8) |
          ((bytes[2] & 0xFFL) << 16) |
          ((bytes[3] & 0xFFL) << 24);
    }
  }

  /**
   * Reads a number of signed bytes from the input stream and returns them as
   * a List.
   *
   * If numberOfBytes is negative, this method reads until a delimiter byte
   * value is read. The delimiter byte value is included in the returned list.
   *
   * @param numberOfBytes the number of bytes to read; a negative number
   *        indicates to read until a delimiter byte value is read
   */
  
  public List<Integer> ReceiveSignedBytes(int numberOfBytes) {
    byte[] bytes = read("ReceiveSignedBytes", numberOfBytes);
    List<Integer> list = new ArrayList<Integer>();
    for (int i = 0; i < bytes.length; i++) {
      int n = bytes[i];
      list.add(n);
    }
    return list;
  }

  /**
   * Reads a number of unsigned bytes from the input stream and returns them as
   * a List.
   *
   * If numberOfBytes is negative, this method reads until a delimiter byte
   * value is read. The delimiter byte value is included in the returned list.
   *
   * @param numberOfBytes the number of bytes to read; a negative number
   *        indicates to read until a delimiter byte value is read
   */
  
  public List<Integer> ReceiveUnsignedBytes(int numberOfBytes) {
    byte[] bytes = read("ReceiveUnsignedBytes", numberOfBytes);
    List<Integer> list = new ArrayList<Integer>();
    for (int i = 0; i < bytes.length; i++) {
      int n = bytes[i] & 0xFF;
      list.add(n);
    }
    return list;
  }

  /**
   * Reads a number of bytes from the input stream.
   *
   * If numberOfBytes is negative, this method reads until a delimiter byte
   * value is read. The delimiter byte is included in the returned array.
   *
   * @param functionName the name of the SimpleFunction calling this method
   * @param numberOfBytes the number of bytes to read; a negative number
   *        indicates to read until a delimiter byte value is read
   */
  protected final byte[] read(String functionName, int numberOfBytes) {
    if (!IsConnected()) {
      bluetoothError(functionName,
          ErrorMessages.ERROR_BLUETOOTH_NOT_CONNECTED_TO_DEVICE);
      return new byte[0];
    }

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    if (numberOfBytes >= 0) {
      // Read <numberOfBytes> bytes.
      byte[] bytes = new byte[numberOfBytes];
      int totalBytesRead = 0;
      while (totalBytesRead < numberOfBytes) {
        try {
          int numBytesRead = inputStream.read(bytes, totalBytesRead, bytes.length - totalBytesRead);
          if (numBytesRead == -1) {
            bluetoothError(functionName,
                ErrorMessages.ERROR_BLUETOOTH_END_OF_STREAM);
            break;
          }
          totalBytesRead += numBytesRead;
        } catch (IOException e) {
          bluetoothError(functionName,
              ErrorMessages.ERROR_BLUETOOTH_UNABLE_TO_READ, e.getMessage());
          break;
        }
      }
      buffer.write(bytes, 0, totalBytesRead);
    } else {
      // Read one byte at a time until a delimiter byte is read.
      while (true) {
        try {
          int value = inputStream.read();
          if (value == -1) {
            bluetoothError(functionName,
                ErrorMessages.ERROR_BLUETOOTH_END_OF_STREAM);
            break;
          }
          buffer.write(value);
          if (value == delimiter) {
            break;
          }
        } catch (IOException e) {
          bluetoothError(functionName,
              ErrorMessages.ERROR_BLUETOOTH_UNABLE_TO_READ, e.getMessage());
          break;
        }
      }
    }

    return buffer.toByteArray();
  }

  // Deleteable implementation

  @Override
  public void onDelete() {
    if (connectedBluetoothSocket != null) {
      Disconnect();
    }
  }
}
