package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.xiledsystems.AlternateJavaBridgelib.components.Component;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.MediaUtil;
import com.xiledsystems.AlternateJavaBridgelib.components.util.ErrorMessages;

/**
 * Multimedia component that plays sounds and optionally vibrates.  A
 * sound is specified via filename.  See also
 * {@link android.media.SoundPool}.
 *
 */

public class Sound extends AndroidNonvisibleComponent
    implements Component, OnResumeListener, OnStopListener, OnInitializeListener, Deleteable , OnDestroySvcListener {

  private static final int MAX_STREAMS = 10;
  private static final float VOLUME_FULL = 1.0f;
  private static final int LOOP_MODE_NO_LOOP = 0;  
  private static final float PLAYBACK_RATE_NORMAL = 1.0f;

  private SoundPool soundPool;
  // soundMap maps sounds (assets, etc) that are loaded into soundPool to their respective
  // soundIds.
  private final Map<String, Integer> soundMap;
  private final ArrayList<String[]> resIdMap;

  private String sourcePath;              // name of source
  private int soundId;                    // id of sound in the soundPool
  private int streamId;                   // stream id returned from last call to SoundPool.play
  
 
  private final Vibrator vibe;
  private boolean initialized = false;
  
  private final boolean isService;


  public Sound(ComponentContainer container) {
    super(container.$form());
    soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
    soundMap = new HashMap<String, Integer>();
    resIdMap = new ArrayList<String[]>();
    vibe = (Vibrator) form.getSystemService(Context.VIBRATOR_SERVICE);
    sourcePath = "";
    form.registerForOnResume(this);
    form.registerForOnStop(this);
    form.registerForOnInitialize(this);
    this.isService = false;

 }
  
  public Sound(ComponentContainer container, int maxstreams) {
	    super(container.$form());
	    soundPool = new SoundPool(maxstreams, AudioManager.STREAM_MUSIC, 0);
	    soundMap = new HashMap<String, Integer>();
	    resIdMap = new ArrayList<String[]>();
	    vibe = (Vibrator) form.getSystemService(Context.VIBRATOR_SERVICE);
	    sourcePath = "";
	    form.registerForOnResume(this);
	    form.registerForOnStop(this);
	    this.isService = false;
	   
	  }
  
  public Sound(SvcComponentContainer container) {
	    super(container.$formService());
	    soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
	    soundMap = new HashMap<String, Integer>();
	    resIdMap = new ArrayList<String[]>();
	    vibe = (Vibrator) formservice.getSystemService(Context.VIBRATOR_SERVICE);
	    sourcePath = "";
	    container.$formService().registerForOnDestroy(this);
	    this.isService = true;

	 }
	  
	  public Sound(SvcComponentContainer container, int maxstreams) {
		    super(container.$formService());
		    soundPool = new SoundPool(maxstreams, AudioManager.STREAM_MUSIC, 0);
		    soundMap = new HashMap<String, Integer>();
		    resIdMap = new ArrayList<String[]>();
		    vibe = (Vibrator) formservice.getSystemService(Context.VIBRATOR_SERVICE);
		    sourcePath = "";
		    container.$formService().registerForOnDestroy(this);
		    this.isService = true;
		   
		  }

  /**
   * Returns the sound's filename.
   */
  
  public String Source() {
    return sourcePath;
  }
  
    
  /**
   * Sets the sound source
   *
   * <p/>See {@link MediaUtil#determineMediaSource} for information about what
   * a path can be.
   *
   * @param tag  the "name" to referece this sound with
   * @param filename the filename of the soundfile
   */
  
  public void addSource(String tag, String filename) {
	  
	  if (filename.contains(".")) {
		  filename = filename.split("\\.")[0];
	  }
	  int temp = form.getResources().getIdentifier(filename, "raw", form.getPackageName());	  
	 
	  if (initialized) {
		  try {
			  int tmp = MediaUtil.loadSoundPool(soundPool, form, filename);
			  soundMap.put(tag, tmp);
			  soundId = soundMap.get(tag);
			  Log.e("Sound", "Sound loaded into SoundMap. SoundID generated: "+soundId);			  
		  } catch (IOException e) {
			  Log.e("Sound", "Sound failed to load: "+filename);
		  }
	  } else {
		  resIdMap.add( new String[] { tag, Integer.toString(temp), filename} );
		  Log.e("Sound", "Sound has been tagged to be loaded in onInitialize.");
	  }
	  
	  /*
    sourcePath = (path == null) ? "" : path;

    // Clear the previous sound.
    if (streamId != 0) {
      soundPool.stop(streamId);
      streamId = 0;
    }
    soundId = 0;

    if (sourcePath.length() != 0) {
      Integer existingSoundId = soundMap.get(sourcePath);
      if (existingSoundId != null) {
        soundId = existingSoundId;

      } else {
        Log.i("Sound", "No existing sound with path " + sourcePath + ".");
        try {
          int newSoundId = MediaUtil.loadSoundPool(soundPool, form, sourcePath);
          if (newSoundId != 0) {
            soundMap.put(sourcePath, newSoundId);
            Log.i("Sound", "Successfully loaded sound: setting soundId to " + newSoundId + ".");
            soundId = newSoundId;
          } else {
            form.dispatchErrorOccurredEvent(this, "Source",
                ErrorMessages.ERROR_UNABLE_TO_LOAD_MEDIA, sourcePath);
          }
        } catch (IOException e) {
          form.dispatchErrorOccurredEvent(this, "Source",
              ErrorMessages.ERROR_UNABLE_TO_LOAD_MEDIA, sourcePath);
        }
      }
    }
    
    */
  }

 
  /**
   * Plays the sound.
   * 
   * @param tag - The tag name of the sound to play
   */
  
  public void Play(String tag) {
	  
    if (soundId != 0 && soundMap.containsKey(tag)) {
      
        streamId = soundPool.play(soundMap.get(tag), VOLUME_FULL, VOLUME_FULL, 0, LOOP_MODE_NO_LOOP,
            PLAYBACK_RATE_NORMAL);
        Log.i("Sound", "SoundPool.play returned stream id " + streamId);
        if (streamId == 0) {
          form.dispatchErrorOccurredEvent(this, "Play",
              ErrorMessages.ERROR_UNABLE_TO_PLAY_MEDIA, sourcePath);
        }
    } else {
      Log.i("Sound", "Unable to play. Did you remember to set the Source property? Tag: "+tag);
    }
  }

  /**
   * Pauses playing the sound if it is being played.
   * 
   * NOTE: This will pause ALL streams. However, when
   * Resume() is called, the active streams which were
   * playing when the Pause() was called will continue
   * playing.
   */
  
  public void Pause() {
    if (streamId != 0) {
      soundPool.autoPause();
    } else {
      Log.i("Sound", "Unable to pause. Did you remember to call the Play function?");
    }
  }

  /**
   * Resumes playing sounds after a pause.
   */
  
  public void Resume() {
    if (streamId != 0) {
      soundPool.autoResume();
    } else {
      Log.i("Sound", "Unable to resume. Did you remember to call the Play function?");
    }
  }

  
  /**
   * Vibrates for the specified number of milliseconds.
   */
  
  public void Vibrate(int millisecs) {
    vibe.vibrate(millisecs);
  }

  
  public void SoundError(String message) {
  }

  // OnStopListener implementation

  @Override
  public void onStop() {
    Log.i("Sound", "Got onStop");
    if (streamId != 0) {
      soundPool.autoPause();
    }
  }

  // OnResumeListener implementation

  @Override
  public void onResume() {
    Log.i("Sound", "Got onResume");
    if (streamId != 0) {
      soundPool.autoResume();
    }
  }

  // Deleteable implementation

  @Override
  public void onDelete() {
    if (streamId != 0) {
            
      ArrayList<Integer> temp = (ArrayList<Integer>) soundMap.values();
      for (int i = 0; i < temp.size(); i++) {
    	  soundPool.stop(temp.get(i));
    	  soundPool.unload(temp.get(i));
      }
    }
    soundPool.release();
    vibe.cancel();
    // The documentation for SoundPool suggests setting the reference to null;
    soundPool = null;
  }

  @Override
  public void onInitialize() {
	
	  if (!initialized && resIdMap.size() > 0) {
		  initialized = true;
		  for (int i = 0; i < resIdMap.size(); i++) {
			  try {
				  int temp = MediaUtil.loadSoundPool(soundPool, form, resIdMap.get(i)[2]);
				  soundMap.put(resIdMap.get(i)[0], temp);
				  if (i == 0) {
					  soundId = temp;
					  Log.e("Sound", "Sound loaded into SoundMap. SoundID generated: "+soundId);
				  }
			  } catch (IOException e) {
				  Log.e("Sound", "Sound failed to load: "+resIdMap.get(i)[0]);
			  }
		  }		  
	  }	
  }

@Override
public void onDestroy() {
	
	if (streamId != 0) {        
	      ArrayList<Integer> temp = (ArrayList<Integer>) soundMap.values();
	      for (int i = 0; i < temp.size(); i++) {
	    	  soundPool.stop(temp.get(i));
	    	  soundPool.unload(temp.get(i));
	      }
	    }
	    soundPool.release();
	    vibe.cancel();
	    // The documentation for SoundPool suggests setting the reference to null;
	    soundPool = null;	
	}
}
