# Installation #

### Notes ###

  * Requires version 3.7.2 of Eclipse (Indigo)
  * Platform independent (Windows, Linux, Mac)
  * No longer requires any specific version of ADT.

### Procedure ###

  1. In Eclipse, within the Help pulldown menu, select Install New Software
![http://dl.dropbox.com/u/8949112/altbridge/step1.png](http://dl.dropbox.com/u/8949112/altbridge/step1.png)
  1. Click the Add button

![http://dl.dropbox.com/u/8949112/altbridge/step2.png](http://dl.dropbox.com/u/8949112/altbridge/step2.png)
  1. Enter a name for the site (ex AltBridge UpdateSite), and enter this URL into the location field: http://altbridge-deploy.appspot.com
  1. Hit OK
  1. Check the AltBridge plugin
![http://dl.dropbox.com/u/8949112/altbridge/step3.png](http://dl.dropbox.com/u/8949112/altbridge/step3.png)
  1. Click next, accept the license, and the plugin will install (eclipse will want to restart after installation is finished)

UPDATE (2.3.02) - The new project wizard is no longer in the AltBridge plugin. Use the ADT wizard instead. See here: http://xiledsystems.com/wp/2012/12/newest-altbridge-changes/

You can watch a video tutorial on creating a simple project here:
http://www.youtube.com/watch?v=Tdpdq4sc5i0 (This is a bit out of date now)


> Note: If upgrading, follow the same procedure, eclipse will automatically perform an upgrade (it will let you know this as well). Also, see the UpdatingProjects Wiki for more information.





The below is deprecated, but if you have an older version, this is how you install it.

# Installation (All versions BELOW 1.9) #


  1. Download the zip file, and unzip.
  1. Create your project in eclipse.
  1. Navigate to your project's folder (you should see /src, /res, /assets, etc)
  1. Create a folder called libs here.
  1. Copy the AlternateJavaBridgelib.jar file you unzipped earlier into the /libs directory.
  1. In Eclipse, click on your project's directory in the package explorer window, and hit F5 to refresh the directory
  1. Open the libs folder, then right click on the AltBridge jar file, and select Build Path > Add To Build Path. You're now ready to start working on your project using the library.