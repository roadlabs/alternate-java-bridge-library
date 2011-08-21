package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;


/**
 * BluetoothServer component
 *
 */

public final class BluetoothServer extends BluetoothConnectionBase {
  private static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

  private final Handler androidUIHandler;

  private final AtomicReference<Object> arBluetoothServerSocket;

  /**
   * Creates a new BluetoothServer.
   */
  public BluetoothServer(ComponentContainer container) {
    super(container, "BluetoothServer");
    androidUIHandler = new Handler();
    arBluetoothServerSocket = new AtomicReference<Object>();
  }

  /**
   * Accept an incoming connection.
   */
  
  public void AcceptConnection(String serviceName) {
    accept("AcceptConnection", serviceName, SPP_UUID);
  }

  /**
   * Accept an incoming connection with a specific UUID.
   */
 
  public void AcceptConnectionWithUUID(String serviceName, String uuid) {
    accept("AcceptConnectionWithUUID", serviceName, uuid);
  }

  private void accept(final String functionName, String name, String uuidString) {
    final Object bluetoothAdapter = BluetoothReflection.getBluetoothAdapter();
    if (bluetoothAdapter == null) {
      form.dispatchErrorOccurredEvent(this, functionName,
          ErrorMessages.ERROR_BLUETOOTH_NOT_AVAILABLE);
      return;
    }

    if (!BluetoothReflection.isBluetoothEnabled(bluetoothAdapter)) {
      form.dispatchErrorOccurredEvent(this, functionName,
          ErrorMessages.ERROR_BLUETOOTH_NOT_ENABLED);
      return;
    }

    UUID uuid;
    try {
      uuid = UUID.fromString(uuidString);
    } catch (IllegalArgumentException e) {
      form.dispatchErrorOccurredEvent(this, functionName,
          ErrorMessages.ERROR_BLUETOOTH_INVALID_UUID, uuidString);
      return;
    }

    try {
      Object bluetoothServerSocket = BluetoothReflection.listenUsingRfcommWithServiceRecord(
          bluetoothAdapter, name, uuid);
      arBluetoothServerSocket.set(bluetoothServerSocket);
    } catch (IOException e) {
      form.dispatchErrorOccurredEvent(this, functionName,
          ErrorMessages.ERROR_BLUETOOTH_UNABLE_TO_LISTEN);
      return;
    }

    AsynchUtil.runAsynchronously(new Runnable() {
      public void run() {
        Object acceptedBluetoothSocket = null;

        Object bluetoothServerSocket = arBluetoothServerSocket.get();
        if (bluetoothServerSocket != null) {
          try {
            try {
              acceptedBluetoothSocket = BluetoothReflection.accept(bluetoothServerSocket);
            } catch (IOException e) {
              androidUIHandler.post(new Runnable() {
                public void run() {
                  form.dispatchErrorOccurredEvent(BluetoothServer.this, functionName,
                      ErrorMessages.ERROR_BLUETOOTH_UNABLE_TO_ACCEPT);
                }
              });
              return;
            }
          } finally {
            StopAccepting();
          }
        }

        if (acceptedBluetoothSocket != null) {
          // Call setConnection and signal the event on the main thread.
          final Object bluetoothSocket = acceptedBluetoothSocket;
          androidUIHandler.post(new Runnable() {
            public void run() {
              try {
                setConnection(bluetoothSocket);
              } catch (IOException e) {
                Disconnect();
                form.dispatchErrorOccurredEvent(BluetoothServer.this, functionName,
                    ErrorMessages.ERROR_BLUETOOTH_UNABLE_TO_ACCEPT);
                return;
              }

              ConnectionAccepted();
            }
          });
        }
      }
    });
  }

  /**
   * Returns true if this BluetoothServer component is accepting an
   * incoming connection.
   */
  
  public final boolean IsAccepting() {
    return (arBluetoothServerSocket.get() != null);
  }

  /**
   * Stop accepting an incoming connection.
   */
  
  public void StopAccepting() {
    Object bluetoothServerSocket = arBluetoothServerSocket.getAndSet(null);
    if (bluetoothServerSocket != null) {
      try {
        BluetoothReflection.closeBluetoothServerSocket(bluetoothServerSocket);
      } catch (IOException e) {
        Log.w(logTag, "Error while closing bluetooth server socket: " + e.getMessage());
      }
    }
  }

  /**
   * Indicates that a bluetooth connection has been accepted.
   */
 
  public void ConnectionAccepted() {
    Log.i(logTag, "Successfullly accepted bluetooth connection.");
    EventDispatcher.dispatchEvent(this, "ConnectionAccepted");
  }
}
