package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Component for displaying images and animations.
 *
 */

public final class Image extends AndroidViewComponent {

  private final ImageView view;

  private String picturePath = "";  // Picture property

  /**
   * Creates a new Image component.
   *
   * @param container  container, component will be placed in
   */
  public Image(ComponentContainer container) {
    super(container);
    view = new ImageView(container.$context()) {
      @Override
      public boolean verifyDrawable(Drawable dr) {
        super.verifyDrawable(dr);
        // TODO(user): multi-image animation
        return true;
      }
    };

    // Adds the component to its designated container
    container.$add(this);
    view.setFocusable(true);
  }

  @Override
  public View getView() {
    return view;
  }

  /**
   * Returns the path of the image's picture.
   *
   * @return  the path of the image's picture
   */
  
  public String Picture() {
    return picturePath;
  }

  /**
   * Specifies the path of the image's picture.
   *
   * <p/>See {@link MediaUtil#determineMediaSource} for information about what
   * a path can be.
   *
   * @param path  the path of the image's picture
   */
  
  public void Picture(String path) {
    picturePath = (path == null) ? "" : path;

    Drawable drawable;
    try {
      drawable = MediaUtil.getDrawable(container.$form(), picturePath);
    } catch (IOException ioe) {
      Log.e("Image", "Unable to load " + picturePath);
      drawable = null;
    }

    ViewUtil.setImage(view, drawable);
  }


  /**
   * Animation property setter method.
   *
   * @see AnimationUtil
   *
   * @param animation  animation kind
   */
  
  public void Animation(String animation) {
    AnimationUtil.ApplyAnimation(view, animation);
  }
}
