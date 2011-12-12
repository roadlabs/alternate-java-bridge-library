package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import android.provider.MediaStore;
import android.util.Log;

/**
 * Component enabling a user to select an image from the phone's gallery.
 *
 */

public class ImagePicker extends Picker implements ActivityResultListener {

  private String imagePath;

  /**
   * Create a new ImagePicker component.
   *
   * @param container the parent container.
   */
  public ImagePicker(ComponentContainer container) {
    super(container);
  }
  
  public ImagePicker(ComponentContainer container, int resourceId) {
	    super(container, resourceId);
	  }

  /**
   * Path for the image that was selected.
   */
  
  public String ImagePath() {
    return imagePath;
  }

  @Override
  protected Intent getIntent() {
    return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
  }

  /**
   * Callback method to get the result returned by the image picker activity
   *
   * @param requestCode a code identifying the request.
   * @param resultCode a code specifying success or failure of the activity
   * @param data the returned data, in this case an Intent whose data field
   *        contains the image's content URI.
   */
  public void resultReturned(int requestCode, int resultCode, Intent data) {
    if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK) {
      Uri selectedImage = data.getData();
      imagePath = selectedImage.toString();
      Log.i("ImagePicker", "Image imagePath = " + imagePath);
      AfterPicking();
    }
  }
}
