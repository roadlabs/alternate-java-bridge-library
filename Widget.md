# Creating a new Widget #

Right click on your project in the project explorer. Go to New, then Other.

In the AltBridge category, select AltBridge New Widget, and hit Next.


## Define Widget Meta Data ##

![http://dl.dropbox.com/u/8949112/altbridge/newwidget.png](http://dl.dropbox.com/u/8949112/altbridge/newwidget.png)

  * Name - The name of the widget.
  * Minimum Widht - The minimum width of the widget. 40 is the lowest allowed.
  * Minimum Height - The minimum height of the widget. 40 is the lowest allowed.
  * UpdatePeriod - This sets how often the widget updates itself. Most of the time, this should be left alone to conserve battery power.
If you update the widget using WidgetUtil, it will update it's display (you don't have to wait for the updateperiod to expire).
  * BaseLayout - This can be either LinearLayout, RelativeLayout, or FrameLayout

If you want to change these settings later, you can edit the xml file located
in /res/xml.

Click Finish.

## Define Widget Layout ##

The widget automatically creates a blank layout for the widget for you.
It resides in the /res/layout folder. It will take the widget name, and append
`_`layout to the end. Double click that file to open the GLE. Place an ImageView.
Select the image you want (or just use the ic\_launcher for now).
Take note of the ID, or edit it yourself (default should be imageView1).

## Define Widget Components ##

The wizard automatically creates the widget's java file for you. It will
also automatically set the layout for you. The only thing you have to do
is to tell it about click events (if you need to).

Open the widget java file (located in the same location as your application's java files).

Under the Layout() method, set the click method of the imageview.

```
// This sets the widget's layout. There should be no need to change this
// as the wizard automatically generates a layout file for you.
Layout(R.layout.newwidget_layout);

// Set the click of our imageview to open the YourFormNameHere Form.
addFormClick(R.id.ImageView1, YourFormNameHere.class);
```

Save everything, and you now have a widget that can be placed on the home screen, which will open a Form when clicked.