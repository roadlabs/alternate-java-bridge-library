# Multiple Screens #

UPDATE: You can check out this tutorial which shows a simple project with two screens: http://www.youtube.com/watch?v=Tdpdq4sc5i0

Working with multiple screens is actually pretty easy. Each "screen" is simply a Form. So, if you want another screen, create a new Form.

**Note: In order for this new form to run, you must declare it in your Android Manifest. If you don't, you'll get a Force Close when you try to start it.**

**Note Pt2: The above note only applies if you did NOT use the right click option to create a New Form. This is handled automatically when you use that option.**

## How Do I start the next screen? ##

Let's say you have Splash, and MainMenu Forms created. Within the Splash file, you use an intent to start the mainmenu like so:

```
Intent intent = new Intent(this, MainMenu.class);
startActivity(intent);
```

That's it. If you don't want the splash screen to stay in memory, add finish(); after the startactivity line.

You can also run methods after running startActivity that will change the animation between Forms (fade in/out, zoom in/out, rotate in/out - see the Animation Wiki for more info).

## How do I pass data to another Form upon loading? ##

Ok, so like the above, you use intents.

```
Intent intent = new Intent(this, MainMenu.class);
intent.putExtra("NAME_OF_DATA", data);
startActivity(intent);
finish();
```

You use the method putExtra to attach your data to the intent. Then, to retreive this data, in the MainMenu file within the $define() method, you grab the intent like so:

```
Intent intent = getIntent();
if (intent.hasExtra("NAME_OF_DATA")) {
   data = intent.getExtra("NAME_OF_DATA");
  }
```

Now, that last line will look a little different depending on the type of data you are passing. Examples:
```
// String extra
data = intent.getStringExtra("NAME_OF_DATA");
// Integer
data = intent.getIntExtra("NAME_OF_DATA", 0);
```

The 0 in the Intextra line is the default (if it doesnt find the value, it will give you the default value).