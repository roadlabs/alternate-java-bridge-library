package com.xiledsystems.AlternateJavaBridgelib;

import com.google.devtools.simple.runtime.components.android.Form;

/*
 * This library was put together because of a need for an alternative library other than the one which App Inventor
 * currently uses (which currently does not come with it). The idea here, was to recreate those components which didn't
 * work "out of the box" so that you wouldn't need this additional library. The reason is with this other library that
 * App Inventory normally uses, it slows things down when added to the build path. At least for me. So I figured if I
 * was having a problem with it, perhaps other will as well, so I may as well share what I've done. It's grown into
 * more of a complimentary library to the Java Bridge. This library now adds new functionality that is currently not
 * possible in App Inventor, such as the ability to run a service which uses components in this library (so writing
 * a service is pretty similar to writing an activity with java bridge). As App Inventor gets upgraded, I will go
 * through the new release, and adjust this library as necessary. (For instance, I added the LocationSensor2 component
 * simply to adjust the Min Time Interval for GPS updates. This is getting added for the next release, so I will 
 * just remove that component from this library to avoid confusion, and remain fully compatible.
 * 
 * If there's something you currently cannot do with a component from the Java Bridge, you can post a message
 * on the alternate-java-bridge-library discussion forum on Google.
 * 
 * Ryan Bis - www.xiledsystems.com
 * 
 * 
 * 
 */


public class AlternateJavaBridgelibActivity extends Form {
   
    void $define() {
        
    }
    
}
