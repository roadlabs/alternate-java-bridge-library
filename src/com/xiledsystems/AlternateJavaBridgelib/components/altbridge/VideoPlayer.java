package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.IOException;

/**
 * TODO: This copies the video from the application's asset directory to a temp
 * file, because the Android VideoView class can't handle assets.
 * Marco plans to include that feature in future releases.
 */

/**
 * TODO: Check that player is prepared (is this necessary?)  See if we need to
 * use isPlaying, onErrorListener, and onPreparedListener.
 */

/**
 * TODO: Set up the touch (and trackball?) Simple event handlers so that they
 * interact well with the videoView implementation, i.e., defining handlers
 * should (probably) override videoView making the Mediacontroller appear.
 */

/**
 * TODO: Remove writes of state debugging info to the log after we're sure
 * things are working solidly.
 */

/**
 * Implementation of VideoPlayer, using {@link android.widget.VideoView}.
 *
 */


public final class VideoPlayer extends AndroidViewComponent
    implements Deleteable, OnCompletionListener, OnErrorListener {

  /*
   * Video clip with player controls (touch it to activate)
   */

  private final VideoView videoView;

  private String sourcePath;     // name of media source

  /**
   * Creates a new VideoPlayer component.
   *
   * @param container
   */
  public VideoPlayer(ComponentContainer container) {
    super(container);
    videoView = new VideoView(container.$context());
    videoView.setMediaController(new MediaController(container.$context()));
    videoView.setOnCompletionListener(this);
    videoView.setOnErrorListener(this);

    // add the component to the designated container
    container.$add(this);
    // set a default size
    container.setChildWidth(this, ComponentConstants.VIDEOPLAYER_PREFERRED_WIDTH);
    container.setChildHeight(this, ComponentConstants.VIDEOPLAYER_PREFERRED_HEIGHT);

    sourcePath = "";
  }

  @Override
  public View getView() {
    return videoView;
  }

  /**
   * Sets the video source.
   *
   * <p/>See {@link MediaUtil#determineMediaSource} for information about what
   * a path can be.
   *
   * @param path  the path to the video source
   */
  
  public void Source(String path) {
    sourcePath = (path == null) ? "" : path;

    // Clear the previous video.
    if (videoView.isPlaying()) {
      videoView.stopPlayback();
    }
    videoView.setVideoURI(null);
    videoView.clearAnimation();

    if (sourcePath.length() > 0) {
      Log.i("VideoPlayer", "Source path is " + sourcePath);

      try {
        MediaUtil.loadVideoView(videoView, container.$form(), sourcePath);
      } catch (IOException e) {
        container.$form().dispatchErrorOccurredEvent(this, "Source",
            ErrorMessages.ERROR_UNABLE_TO_LOAD_MEDIA, sourcePath);
        return;
      }

      Log.i("VideoPlayer", "loading video succeeded");
    }
  }

  /**
   * Plays the media specified by the source.  These won't normally be used in
   * the most elementary applications, because videoView brings up its own
   * player controls when the video is touched.
   */
  
  public void Start() {
    Log.i("VideoPlayer", "Calling Start");
    videoView.start();
  }

  
  public void Pause() {
    Log.i("VideoPlayer", "Calling Pause");
    videoView.pause();
  }

  
  public void SeekTo(int ms) {
    Log.i("VideoPlayer", "Calling SeekTo");
    if (ms < 0) ms = 0;
    // There is no harm if the milliseconds is longer than the duration.
    videoView.seekTo(ms);
  }

  
  public int GetDuration() {
    Log.i("VideoPlayer", "Calling GetDuration");
    return videoView.getDuration();
  }

  // OnCompletionListener implementation

  @Override
  public void onCompletion(MediaPlayer m) {
    Completed();
  }

  /**
   * Indicates that the video has reached the end
   */
  
  public void Completed() {
    EventDispatcher.dispatchEvent(this, "Completed");
  }

  // OnErrorListener implementation

  @Override
  public boolean onError(MediaPlayer m, int what, int extra) {
    Log.e("VideoPlayer", "onError: what is " + what + " 0x" + Integer.toHexString(what) +
        ", extra is " + extra + " 0x" + Integer.toHexString(extra));
    container.$form().dispatchErrorOccurredEvent(this, "Source",
        ErrorMessages.ERROR_UNABLE_TO_LOAD_MEDIA, sourcePath);
    return true;
  }

 
  public void VideoPlayerError(String message) {
  }

  // Deleteable implementation

  @Override
  public void onDelete() {
    videoView.stopPlayback();
  }

  /*
   *  Unfortunately, to prevent the user from setting the width and height
   *  of the component, we have to also prevent them from getting the width
   *  and height of the component.
   */

  /**
   * Returns the component's horizontal width, measured in pixels.
   *
   * @return  width in pixels
   */
  @Override
  
  public int Width() {
    return super.Width();
  }

  /**
   * Specifies the component's horizontal width, measured in pixels.
   *
   * @param  width in pixels
   */
  @Override
 
  public void Width(int width) {
    super.Width(width);
  }

  /**
   * Returns the component's vertical height, measured in pixels.
   *
   * @return  height in pixels
   */
  @Override
  
  public int Height() {
    return super.Height();
  }

  /**
   * Specifies the component's vertical height, measured in pixels.
   *
   * @param  height in pixels
   */
  @Override
 
  public void Height(int height) {
    super.Height(height);
  }
}
