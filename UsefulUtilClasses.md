# NetworkUtil #

This is handy for checking the network connectivity.

These methods can be called from either a Form, or FormSevice.

```
NetworkUtil.isConnected(this);
```
- This returns true if the device is connected to a network.

```
NetworkUtil.getNetworkType(this);
```
- This returns a String describing the device's currently connected network type (to give you an idea of how strong a connection the device has).

The five possible strings are:
```
NetworkUtil.UNKNOWN // The network connection type is unknown.
NetworkUtil.MOBILE_SLOW // (14 - 100kbps; basically 2G connection)
NetworkUtil.MOBILE_FAST // (400kbps on up; 3G and some 4G connections)
NetworkUtil.LTE // 4G LTE
NetworkUtil.WIFI // Wifi
```

# DeviceUtil #

This class is used to get the screensize, and density folders the device is pulling from. (For instance, a BlackBerry Playbook actually pulls from the xlarge folder, even though it is a large screen.)

These methods should be called in a Form, although the get methods should work in a FormService.

```
DeviceUtil.showScreenSize(this);
```
- This will display a Toast message on screen stating the device's screen size (small, normal, large, xlarge, unknown).

```
DeviceUtil.getScreenSize(this);
```
- This will return a String of the screen size

```
DeviceUtil.showDensity(this);
```
- This will display a Toast message on screen stating the device's density (low, medium, high, xhigh)

```
DeviceUtil.getDensity(this);
```
- This will return a String of the density.

# EncryptUtil #

This is useful if you want to encrypt your data using AES encryption. This is the class that SecureTinyDB uses to encrypt/decrypt data. This requires a little more work, as it deals with byte arrays.

```
EncryptUtil.getEncryptionBits();
```
- This returns the bit encryption the class is using (default is 128bit)

```
EncryptUtil.setEncryptionBits(256);
```
- This will set the class to use 256bit encryption instead of 128. 128 is the safest to use, as most devices support 128bit encryption.

```
EncryptionUtil.encrypt("Myencryptionkey", mybytearray);
```
- This returns the encrypted data. The second argument is the data in byte array type (byte[.md](.md)). The encrypted data is returned in the same type. null will be returned on any failures. (Check logcat output)

```
EncryptionUtil.decrypt("Myencryptionkey", encryptedbytearray);
```
- This returns the decrypted byte array. null will be returned if there was a failure anywhere. (Check logcat output)

# MemUtil #

This class isn't quite as useful as I was hoping it to be. It's useful to see the heap size your app gets on a device. The other methods aren't quite as reliable (bitmap memory won't show in your app's heap, fooling you into thinking you have more memory available than you actually do). All of these methods return their values in MB.

```
MemUtil.Available();
```
- This returns a double of the heap size available to your app.

```
MemUtil.Allocated();
```
- This shows how much memory is being used up by your app. Note that bitmaps won't show up here (and bitmaps take up a LOT of memory. A bitmap takes up width x height x 4 bytes), so this method isn't very reliable.

```
MemUtil.Free();
```
- This returns how much memory is free in your app's heap. Again, because the Allocated() method isn't very reliable in telling you how much memory your ACTUALLY taking up, this method as well isn't reliable.

# SdkLevel #

This is used to check the device's SDK level (OS).

```
SdkLevel.getLevel();
```
- This returns an int representing the device's OS level

SdkLevel also contains static ints for different OS levels:
```
SdkLevel.LEVEL_CUPCAKE                // API 3
SdkLevel.LEVEL_DONUT                  // API 4
SdkLevel.LEVEL_ECLAIR                 // API 5
SdkLevel.LEVEL_ECLAIR_0_1             // API 6
SdkLevel.LEVEL_ECLAIR_MR1             // API 7
SdkLevel.LEVEL_FROYO                  // API 8
SdkLevel.LEVEL_GINGERBREAD            // API 9
SdkLevel.LEVEL_GINGERBREAD_MR1        // API 10
SdkLevel.LEVEL_HONEYCOMB              // API 11
SdkLevel.LEVEL_HONEYCOMB_MR2          // API 13
SdkLevel.LEVEL_ICE_CREAM_SANDWICH     // API 14
SdkLevel.LEVEL_ICE_CREAM_SANDWICH_MR1 // API 15
SdkLevel.LEVEL_JELLY_BEAN             // API 16
SdkLevel.LEVEL_JELLY_BEAN_MR1         // API 17
```

# SensorUtil #

This is only one method right now, but it's still useful.

```
SensorUtil.Smooth(float[] sensorvals, float[] trackedvals, float alpha);
```
- This returns a float array of the smoothed out values. The alpha expects a number from 0 to 1. A lower number (like say, 0.2f) means more smoothing. This method is useful when working with the AccelerometerSensor.

# UIEvent #

This class is useful if you want to fire your own Event in the UI thread. This class can be instantiated in a Form, or FormService.

```
UIEvent event = new UIEvent(this);
event.fireEvent("MyCustomEvent");
```
- This will cause the "MyCustomEvent" to be thrown on the UI thread (with UIEvent being the component). This is useful if you are doing something in a separate thread, and need to post something in the UI thread. (More so useful if you are making your own component)