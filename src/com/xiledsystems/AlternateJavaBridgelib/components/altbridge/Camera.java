package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.Date;

/**
 * Camera provides access to the phone's camera
 *
 *
 */

public class Camera extends AndroidNonvisibleComponent
    implements ActivityResultListener, Component {

  private static final String CAMERA_INTENT = "android.media.action.IMAGE_CAPTURE";
  private static final String CAMERA_OUTPUT = "output";
  private final ComponentContainer container;
  private Uri imageFile;

  /* Used to identify the call to startActivityForResult. Will be passed back
  into the resultReturned() callback method. */
  private int requestCode;

    /**
   * Creates a Camera component.
   *
   * @param container container, component will be placed in
   */
  public Camera(ComponentContainer container) {
    super(container.$form());
    this.container = container;
  }

  
  public void TakePicture() {
    Date date = new Date();
    String state = Environment.getExternalStorageState();

    if (Environment.MEDIA_MOUNTED.equals(state)) {
      Log.i("CameraComponent", "External storage is available and writable");

      imageFile = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
        "/Pictures/app_inventor_" + date.getTime()
        + ".jpg"));

      ContentValues values = new ContentValues();
      values.put(MediaStore.Images.Media.DATA, imageFile.getPath());
      values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
      values.put(MediaStore.Images.Media.TITLE, imageFile.getLastPathSegment());

      if (requestCode == 0) {
        requestCode = form.registerForActivityResult(this);
      }

      Uri imageUri = container.$context().getContentResolver().insert(
        MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
      Intent intent = new Intent(CAMERA_INTENT);
      intent.putExtra(CAMERA_OUTPUT, imageUri);
      container.$context().startActivityForResult(intent, requestCode);
    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
      form.dispatchErrorOccurredEvent(this, "TakePicture",
          ErrorMessages.ERROR_MEDIA_EXTERNAL_STORAGE_READONLY);
    } else {
      form.dispatchErrorOccurredEvent(this, "TakePicture",
          ErrorMessages.ERROR_MEDIA_EXTERNAL_STORAGE_NOT_AVAILABLE);
    }
  }

  @Override
  public void resultReturned(int requestCode, int resultCode, Intent data) {
    Log.i("CameraComponent",
      "Returning result. Request code = " + requestCode + ", result code = " + resultCode);
    if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK) {
      File image = new File(imageFile.getPath());
      if (image.length() != 0) {
        AfterPicture(imageFile.toString());
      } else {
        deleteFile(imageFile);  // delete empty file
        // see if something useful got returned in the data
        if (data != null && data.getData() != null) {
          Uri tryImageUri = data.getData();
          Log.i("CameraComponent", "Calling Camera.AfterPicture with image path "
              + tryImageUri.toString());
          AfterPicture(tryImageUri.toString());
        } else {
          Log.i("CameraComponent", "Couldn't find an image file from the Camera result");
          form.dispatchErrorOccurredEvent(this, "TakePicture",
              ErrorMessages.ERROR_CAMERA_NO_IMAGE_RETURNED);
        }
      }
    } else {
      // delete empty file
      deleteFile(imageFile);
    }
  }

  private void deleteFile(Uri fileUri) {
    File fileToDelete = new File(fileUri.getPath());
    try {
      if (fileToDelete.delete()) {
        Log.i("CameraComponent", "Deleted file " + fileUri.toString());
      } else {
        Log.i("CameraComponent", "Could not delete file " + fileUri.toString());
      }
    } catch (SecurityException e) {
      Log.i("CameraComponent", "Got security exception trying to delete file "
          + fileUri.toString());
    }
  }

  
  public void AfterPicture(String image) {
    EventDispatcher.dispatchEvent(this, "AfterPicture", image);
  }
}
