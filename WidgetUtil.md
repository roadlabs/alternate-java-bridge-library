# WidgetUtil #


## Usage ##

The WidgetUtil class is designed to be instantiated once per widget. If you need to modify another widget, instantiate another instance of WidgetUtil for it.

```
private WidgetUtil wUtil;

// Instantiate the util passing the class name of the widget to control
// and also the layout resource of the widget
wUtil = new WidgetUtil(TestWidget.class, R.layout.testwidget_layout);

// Set the component you'd like to manipulate:
wUtil.Component(R.id.imageView1);

// This method sets the imageview's image to the testimg file in the
// drawable folder. This method is used the same exact way when used
// in a Form, or FormService. 
wUtil.Image(this, R.drawable.testimg);

// Set the text of a textview in the widget. Like the Image method,
// this is used the same way in a Form, and FormService.
wUtil.Component(R.id.textView1);
wUtil.Text(this, "Blah blah blah");

// Set the color of the text of a textview in the widget.
wUtil.TextColor(this, ABColors.RED);

// Set the visibility of a view in the widget
wUtil.Visible(this, false); 
 
```

For the Image method to work, you have to place an ImageView into the widget's layout file (which will be in the /res/layout folder with your
widget's name suffixed by `_`layout. Likewise, for the Text method to work, you have to place a TextView into the widget's layout.

Note that you can also use the WidgetUtil without instantiating it. The methods are just a bit longer.

```
// Change the image of an ImageView
WidgetUtil.setImage(this, R.id.imageView1, R.drawable.testimg, R.layout.testwidget_layout, TestWidget.class);

// Set the text of a textview
WidgetUtil.setText(this, R.id.textView1, "Blah blah blah", R.layout.testwidget_layout, TestWidget.class);

// Set the text color
WidgetUtil.setText(this, R.id.textView1, ABColors.BLACK, R.layout.testwidget_layout, TestWidget.class);

// Set the visibility of a view in the widget
WidgetUtil.setVisibility(this, R.id.textView1, false, R.layout.testwidget_layout, TestWidget.class);
```