# File Placement #

In all versions of the AltBridge prior to 1.3, all media files had to be placed into the /assets folder (images, videos, sounds). In version 1.3 and up, media files will be split between two or more folders.

  * Images must be in /res/drawable-mdpi
  * Sounds must be in /res/raw

Note: You will have to create the raw directory. Also, you can use the drawable-ldpi, and drawable-hdpi folders as well if you plan to have 3 versions of each image in your app (the difference with these images is the density, or dpi - dots per inch). Most times, this is unnecessary as most images will resize and still look good.


The reason for this change is to take advantage of android's built in image handling. It takes care of compression, and deals with memory much better. In order to take advantage of this, the files have to be moved into the drawable folders. Since the images had to be moved, it made sense to move the sound files to the appropriate android SDK location.