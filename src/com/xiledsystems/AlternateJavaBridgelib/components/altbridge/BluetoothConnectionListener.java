package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

/**
 * Callback for receiving Bluetooth connection events
 *
 */
interface BluetoothConnectionListener {
  /**
   *
   */
  void afterConnect(BluetoothConnectionBase bluetoothConnection);

  /**
   *
   */
  void beforeDisconnect(BluetoothConnectionBase bluetoothConnection);
}
