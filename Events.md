# Events #

This wiki is a refernce page for the components in the bridge, and what events they raise. It's organized by events (and the components that throw that event, as some components throw the same event).

## "AccelerationChanged" ##

Indicates a change in acceleration in any of the three dimensions. Also throws a float of the accelration for all 3 dimensions (x, y, z)
  * AccelerometerSensor

## "AfterChoosing" ##

Event after the user has made a selection for ShowChooseDialog. Returns the choice in a string.
  * Notifier

## "AfterGettingText" ##

Simple event to raise after the VoiceReco activity has returned. The result is returned in a string.
  * SpeechRecognizer

## "AfterPicking" ##

Simple event to be raised after the picker activity returns its result and the properties have been filled in.
  * All picker components (ListPicker, PhoneNumberPicker, etc)

## "AfterScan" ##

Event thrown after scanning something, a string of the result is returned.
  * BarcodeScanner

## "AfterSelection" ##

Event thrown after something has been selected from the component.
  * DropDown

## "AfterSoundRecorded" ##

Event thrown after a sound has been recorded. Returns the filename in a string.
  * SoundRecorder

## "AfterTextInput" ##

Event raised after the user has responded to ShowTextDialog. Returns the response in a string.
  * Notifier

## "AnimationStopped" ##

Event throw to indicate the animation has stopped.
  * ImageSprite

## "BeforeGettingText" ##

Simple event to raise when VoiceReco is invoked but before the VoiceReco activity is started.
  * SpeechRecognizer

## "BeforePicking" ##

Event thrown when the component is clicked, but before the picker is started.
  * All picker components (ListPicker, PhoneNumberPicker, etc)

## "Changed" ##

Indicates a change with the component
  * CheckBox

## "Click" ##

Indicates the component has been clicked.
  * Button
  * ListPicker
  * PhoneNumberPicker
  * EmailPicker
  * ImageGallery (This also sends the selection number in the event)
  * ImagePicker

## "CollidedWith" ##

Event to indicate this component has collided with another.
  * Ball
  * ImageSprite

## "Completed" ##

Indicates that the video has reached the end.
  * VideoPlayer

## "DownState" ##

Indicates the component is in the process of being clicked.
  * Same as "Click"

## "Dragged" ##

Indicates a dragging motion on this component.
  * Canvas
  * AnimCanvas
  * Ball
  * ImageSprite

## "EdgeReached" ##

Indicates the component has reached the edge of the screen. Also throws the int equivalent of the edge.
  * Ball
  * ImageSprite

## "ErrorOccured" ##

Indicates an error has occured. This also returns the errornumber, and error message. Note: This won't catch all errors.
  * Form
  * FormService

## "FormServiceMessage" ##

Indicates that a message has been sent to the Form from a FormService. The data is passed via a string.

  * Form (Although the message is sent by the FormService, this event fires in the currently active form.)

## "GotFocus" ##

Indicates the cursor has now moved over the component.
  * Same as "Click"
  * CheckBox
  * PasswordTextBox
  * TextBox

## "GotResult" ##

Indicates this component has received a result. Also returns a string value of the result.
  * FusiontablesControl

## "GotValue" ##

Indicates that a GetValue server request has succeeded. The tag is returned as a string, the value is an object.
  * TinyWebDB

## "Initialize" ##

Event for running code after the screen has been initialized, when it's run for the first time.
  * Form
  * FormService (Service's can't touch the UI, so this runs after the $define method. This is deprecated)

## "LocationChanged" ##

Indicates the current location has changed. Returns the latitude, longitude, and altitude as doubles
  * LocationSensor

## "LostFocus" ##

Indicates the cursor has moved away from the component.
  * Same as "Click"
  * CheckBox
  * PasswordTextBox
  * TextBox

## "MessageReceived" ##

Event that's raised when a new text message is received by the phone. Both the number it's from, and the message are returned as strings.
  * Texting

## "onStartCommand" ##

Event thrown when a service is started (or restarted when already running)
  * FormService

## "OrientationChanged" ##

This event is signalled when the device's orientation has changed. It reports the new values of yaw, pich, and roll as floats.
  * OrientationSensor

## "PositionChanged" ##

This is thrown when the app user manually slides the slide bar.

  * SliderBar

## "Response" ##

Event thrown when a response has been received from a server.

  * DropBoxClient (This returns a boolean with the event)
  * FTClient (This returns the response as a string with this event)


## "ScreenOrientationChanged" ##

Indicates a change in the screen orientation.
  * Form

## "Shaking" ##

Indicates the device either started to shake, or is still shaking.
  * AccelerometerSensor

## "StartedRecording" ##

  * SoundRecorder

## "StatusChanged" ##

Indicates the location sensor's status has changed. Returns the provider, and status as strings
  * LocationSensor

## "StoppedRecording" ##

  * SoundRecorder

## "ThreadRunning" ##

Event for running code in a seperate thread in a service.
  * FormService

## "Timer" ##

A timer's firing event.
  * Clock
  * ThreadTimer

## "Touched" ##

Indicates the component has been touched (similar to the click event)
  * Canvas
  * Dragged
  * Ball
  * ImageSprite

## "UpState" ##

Indicates the component is no longer being clicked.
  * Same as "Click"

## "ValueStored" ##

Event thrown when a value is successfully stored in TinyWebDB.
  * TinyWebDB

## "WebServiceError" ##

Indicates that the communication with the Web service signaled an error. The error message is returned as a string.
  * TinyWebDB