# Sprite Sheets #

As of version 2.2.32, you can now use sprite sheets created with Texture Packer. (You can get it here: http://www.texturepacker.com/ )

Texture Packer is a great tool for creating sprite sheets quickly. The developer asks a reasonable price for the full version, but it can be used for free without using any options. If you like the program, make sure you support the dev, and buy a license.

There are mainly two situations where you'd want to use a spritesheet:

  * Sprite Animations (each frame of the animation is placed in the sheet)
  * Assigning images to many components.

The most expensive operations an app can do is to perform network operations, or reading/writing to the filesystem. A spritesheet will boost performance, as it only has to load the image from the filesystem once. Note that while it's called a sprite sheet, it doesn't mean the images within HAVE to be used for a sprite. They can be applied to any visible component.

## Creating the Sprite Sheet ##

Ok, so you've got Texture Packer downloaded and installed, now how do you create the sheet? It's pretty simple, but there are a couple of options to be aware of.

But first, open Texture Packer, and drag the images you want in the sheet into the box on the right hand side.


![http://dl.dropbox.com/u/8949112/altbridge/wiki/newsheet.png](http://dl.dropbox.com/u/8949112/altbridge/wiki/newsheet.png)


Now you have to adjust the output settings:
  * The Data Format should be set to JSON.
  * Set the data file (this is where it saves the data file, and what to name it).
  * Then set the Texture File location (this is the outputted spritesheet file).

If you're using the free version for now (I believe it's called Essentials in the program), you'll have to change some other things so that your spritesheet doesn't get a watermark over half of it.
Scroll down to the layout section on the left hand side.

![http://dl.dropbox.com/u/8949112/altbridge/wiki/layoutsection.png](http://dl.dropbox.com/u/8949112/altbridge/wiki/layoutsection.png)

  * Change the Algorithm to Basic.
  * Uncheck Allow rotation.
  * Uncheck Trim.

Now, go to File > Export Image.
Then, File > Export Data.

You can also hit Publish I believe. Now the spritesheet, and the resulting data file are ready to be put into your projects folders. The spritesheet should go into the drawable-nodpi folder (create it if it doesnt exist). Then the data file should go into the assets folder.

The data file contains the filenames of the images in the spritesheet. So, it's a good idea to remember these, because you use it to reference which image you want when assigning to visible components.

## Using the Sprite Sheet ##

Ok, so using the spritesheet from the example archive, it's a small sprite sheet with 4 pngs in it. The four files used in the sheet are:

  * blueshade.png
  * blueshade\_down.png
  * redshade.png
  * redshade\_down.png

First you must create a SpriteSheetHelper, then you can assign the images within it. The helper can return a drawable to use for your components.

```
// Instantiate the helper, and tell it the resource of the sprite sheet
SpriteSheetHelper helper = new SpriteSheetHelper(this, R.drawable.map);

// Tell the helper the name of the data file of the sprite sheet
helper.loadSheetData("map.json");

// This will set the button's image to the blueshade image from the
// sprite sheet.
btn1.Drawable(helper.getDrawable("blueshade.png"));

// We'll use a couple of button states here from the sprite sheet
ButtonStateHelper help = new ButtonStateHelper(this);
help.setPressedDrawable(helper.getDrawable("blueshade_down.png"));
help.setEnabledDrawable(helper.getDrawable("blueshade.png")); 	
btn1.ImageDrawables(help);

```

> ## Sprite Sheets with ImageSprites ##

With an ImageSprite, the frames get loaded in the order they show up in the JSON data file.

```

// This will return a string arraylist of the filenames in the sprite sheet.
// The index of each item in the list corresponds to the frame number.
// This allows you to change which sprite is in which frame. Reorder
// the list how you want, then use setFrameOrder with the reordered list.
ArrayList<String> files = helper.frameOrder();
// Reorder the list how you want
helper.setFrameOrder(files);

// This tells the ImageSprite to use the sprite sheet helper for it's
// images.
sprite.SetSpriteSheetHelper(helper);

```