# GLE #

This is mainly to show how the GUI was set up in the simple example from the download section of this site.

1. From the Theme drop down box, select "Theme.NoTitleBar.Fullscreen"

2. Double click the AndroidManifest.xml of the project. Select the Application tab. Click the Browse button next to the theme input box. Select system resources, then select Theme.NoTitleBar.Fullscreen. (**Note as of ADT v16.0.1, there's a bug here. After selecting system resources, you will only be able to select OK or Cancel. Hit OK, then hit the browse button again, and you will be able to select the Theme).**

3. Back in the GLE, delete the Hello World textview

4. Under Custom & Library Views (all the AltBridge visible components will show up in this section), drop an AB\_Button into the UI.

5. Click the toggle fill width button right above the UI canvas.

6. Right click the button (ab\_button), select Layout Height, then Other, set it to 50dp

7. Right click the button and select Other properties > All By Name > Background...

8. Select DarkGrey under Color:

![http://dl.dropbox.com/u/8949112/altbridge/colorchooser.png](http://dl.dropbox.com/u/8949112/altbridge/colorchooser.png)

Note that it gives a preview of the color on the right.


9. Right click the button, select edit Text, then hit the Clear button (we're setting the text in code, but you could always just do it here)

The rest were pretty much the same as the above. Next was a label, with a textsize of 24sp, I believe. Then the AB\_Canvas, height of 300dp, and the AB\_VerticalArrangement on the bottom, set to match parent.