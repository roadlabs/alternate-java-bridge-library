# ImageGallery #

This component sets up a "gallery" of images, which the user can scroll through, and "select" an image.

```
private ImageGallery gallery;

// This is a string array of the name of the images to load into the
// gallery. You don't need to have the .png, or .jpg, etc, just the
// name of the file without the extension.

String images[] = { "image1", "image2", "image3" } ;


// Here we instantiate the ImageGallery. We pass our string array of
// image names (all the images must reside in the drawable folder). We
// also pass the argument R.styleable.ImageGallery. This ONLY works with
// the altbridge version 1.9 and up. This is always the same no matter
// what.

gallery = new ImageGallery(this, images, R.styleable.ImageGallery);


// And we can set a background image for behind the images
gallery.BackgroundImage("background");

// Or, you can set a background color instead
gallery.BackgroundColor(Color.GRAY);


// You can also manually set the size of the images shown in the gallery
gallery.setPicSize(100, 50);


// For those more advanced, if you want to pass the full path of images 
// (ones that aren't in the drawable folder, let's say on the sdcard)
// Then you have to set this boolean
gallery.useFullPath(true);

```

## Events ##

> This component throws the "Click" event. It also passes the position number. So, it's a good idea to have your pic list stored globally, as the position of the image selected will line up with the array's positions. The following convert's the object to an int, and passes it to the faux method galleryImageSelected.

```
if (component.equals(gallery) && eventName.equals("Click")) {
  galleryImageSelected(Convert.Int(args[0]));
  return true;
}
```