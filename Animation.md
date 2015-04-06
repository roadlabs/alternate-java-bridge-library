There are a few different types of animation available in the AltBridge. I will detail each below.

# Activity Animation #

This is the animation you see when opening a new Form, or traveling back to an already opened Form. The basic android one is for the whole screen to slide from right to left, then when going back, it goes left to right. Below are the current different types available in the AltBridge currently.

### Fade ###

Fade the activity into view.

Run the following method after calling startActivity(intent) to fade into view:

```
Intent intent = new Intent(this, NextForm.class);
startActivity(intent);
FadeToActivity(false);
```

If you run the method with true after closing a Form, it will fade in the opposite direction.

### Zoom ###

Zoom the activity into view.

```
Intent intent = new Intent(this, NextForm.class);
startActivity(intent);
ZoomToActivity(false);
```

This works the same way as FadeToActivity does. (set it to true to reverse the animation)

### Rotate ###

While I think the coolest of the three, this one comes with a caveat. It can't be run in reverse. There is an issue in the android SDK where it ignores values in XML for the animation so it doesn't run properly. This results in this animation containing a workaround, and a slightly different way of using it.
To rotate to an activity, you must pass the intent to it (so, don't call startActivity at all, this method will run it for you).

```
Intent intent = new Intent(this, NextForm.class);
RotateActivity(intent);
```


# View Animation #

View animation is animation applied to a view, or visible component. This can be applied to components such as Label, Button, Image, Canvas, Vertical and Horizontal Arrangements (anything related to the UI really). There are various animation types to play with. All the animations come preset with defaults to make animation easy to implement. There are plenty of methods to also set your own values, so they're also pretty flexible.

### Shake ###

This is the easiest of all animations to implement. There are no options (although you can always modify the xml files in the /res/anim directory). Simply run the ShakeComponent() method when you want a visible component to shake.

```
button.ShakeComponent();
```

All of the following animation types are operated the same way:

  * Accelerate
  * Decelerate
  * Accelerate and Decelerate
  * Anticipate
  * Overshoot
  * Anticipate and Overshoot
  * Bounce
  * Flip `*`

`*` - Ignores the animation direction attribute


The following methods are used for further control over the animation:

```
// This changes the direction of the animation.
// The flip animation ignores this value.
button.AnimationDirection(AnimationUtil.TOP_TO_BOTTOM);

// This specifies how long the animation lasts in ms
button.AnimationDuration(500);

// This allows you to specify the start, and end points of the view's animation in relation to it's parent. 
// If you use this, there's no need to use the AnimationDirection() method.
button.AnimationPoints(30, 50, 320, 85);

// This method allows you to set how many times an animation is performed. You can also set it to be
// inifinte (never stops).
button.AnimationRepeatCount(AnimationUtil.ANIM_REPEAT_INFINITE);

// Set the amount of time in ms to delay before showing the
// animation after starting it. Default is 300ms
button.AnimationStartOffset(0);

// This method is only used for the Flip animation. The flip animation
// rotates the view in a 3d space. You can either rotate the view on
// the horizontal or vertical axis. The default is vertical. This allows
// you to set the actual rotate start and end points in degrees.
// Use negative degrees to go the opposite direction. This animation
// also throws an event in the middle. This is actually two animations
// strung together. This allows you to do something in the middle of 
// the animation (think of a card turning over). The event thrown is
// "AnimationMiddle".
button.AnimationFlipDegrees(0, 90, 270, 360);

// This is only used for the Flip animation. It sets how far back on
// the Z axis the animation is run.
button.AnimationDepth(100);
```

Finally, to animate a visible component,
```
button.Animate(ANIM_BOUNCE);
```



# Frame Animation #

...soon...

# Sprite (Sheet) Animation #

... a little less soon...