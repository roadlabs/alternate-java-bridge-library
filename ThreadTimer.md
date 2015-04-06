# ThreadTimer #

> This is useful if you want to process a lot of data in the background as efficiently as possible (the clock component runs in the application's main UI thread, which can slow things down depending on what you are doing).

```
private ThreadTimer timer;

timer = new ThreadTimer(this);


// This sets the interval in ms of the timer. (Whatever code you want 
// executed, this is how long to pause before running it again). In 
// this case, it's 3000ms

timer.Interval(3000);


// This enables the timer (starts it going). Use false to stop the 
// timer.

timer.Enabled(true);


// This set's the auto toggle of the timer. If set to true, the timer
// will automatically stop when your application loses focus, and 
// resume when the app gets focus back.

timer.AutoToggle(true);


```

## Events ##

The ThreadTimer throws the "Timer" event, just like the Clock. So, whatever code you want the thread to run, catch that event, and place it there.

```
if (component.equals(timer) && eventName.equals("Timer")) {
    processStuff();
    return true;
 }
```

# Important Info #

  * A seperate thread cannot touch the UI in any way.
  * It is up to you to make sure that threads don't try to access the same data at the same time. The SimpleSQL component is thread safe, but this doesn't mean you can't run into issues (the app will just freeze, rather than crash).