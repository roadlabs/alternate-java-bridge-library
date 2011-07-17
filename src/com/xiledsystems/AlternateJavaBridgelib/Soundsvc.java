package com.xiledsystems.AlternateJavaBridgelib;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.Log;

import com.google.devtools.simple.runtime.components.android.Deleteable;
import com.google.devtools.simple.runtime.components.android.util.MediaUtil;
import com.google.devtools.simple.runtime.components.util.ErrorMessages;
import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.events.EventDispatcher;

/*
 * Soundsvc class to use a player in a service (good for music). This has been mostly copied from
 * Java Bridge's Sound class. It has been modified to run in a service.
 * 
 * This service registers an OnStartCommand Event. This allows you to add more to this component. When the service
 * first starts, it calls the define() method, then OnStartCommand. Once the sevice is running, if it gets another
 * intent to start it, it only runs OnStartCommand.
 * 
 * Ryan Bis - www.xiledsystems.com
 * 
 */

public class Soundsvc extends AndroidServiceComponent implements Component, OnStartCommandListener,
OnDestroyListener, Deleteable {
	
	 private static final int MAX_STREAMS = 10;
	  private static final float VOLUME_FULL = 1.0f;
	  private static final int LOOP_MODE_NO_LOOP = 0;
	  private static final float PLAYBACK_RATE_NORMAL = 1.0f;

	  private SoundPool soundPool;
	  // soundMap maps sounds (assets, etc) that are loaded into soundPool to their respective
	  // soundIds.
	  private final Map<String, Integer> soundMap;

	  private String sourcePath;              // name of source
	  private int soundId;                    // id of sound in the soundPool
	  private int streamId;                   // stream id returned from last call to SoundPool.play
	  private int minimumInterval;            // minimum interval between Play() calls
	  private long timeLastPlayed;            // the system time when Play() was last called
	  private final Vibrator vibe;


	  public Soundsvc(SvcComponentContainer container) {
	    super(container.$formService());
	    soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
	    soundMap = new HashMap<String, Integer>();
	    vibe = (Vibrator) formService.getSystemService(Context.VIBRATOR_SERVICE);
	    sourcePath = "";
	    formService.registerForOnStartCommand(this);
	    formService.registerForOnDestroy(this);

	    // Default property values
	    MinimumInterval(500);
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
	   * @param path  the path to the sound source
	   */
	  
	  public void Source(String path) {
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
	          int newSoundId = MediaUtilsvc.loadSoundPool(soundPool, formService, sourcePath);
	          if (newSoundId != 0) {
	            soundMap.put(sourcePath, newSoundId);
	            Log.i("Sound", "Successfully loaded sound: setting soundId to " + newSoundId + ".");
	            soundId = newSoundId;
	          } else {
	            formService.dispatchErrorOccurredEvent(this, "Source",
	                ErrorMessages.ERROR_UNABLE_TO_LOAD_MEDIA, sourcePath);
	          }
	        } catch (IOException e) {
	          formService.dispatchErrorOccurredEvent(this, "Source",
	              ErrorMessages.ERROR_UNABLE_TO_LOAD_MEDIA, sourcePath);
	        }
	      }
	    }
	  }

	  /**
	   * Returns the minimum interval required between calls to Play(), in
	   * milliseconds.
	   * Once the sound starts playing, all further Play() calls will be ignored
	   * until the interval has elapsed.
	   * @return  minimum interval in ms
	   */
	  
	  public int MinimumInterval() {
	    return minimumInterval;
	  }

	  /**
	   * Specify the minimum interval required between calls to Play(), in
	   * milliseconds.
	   * Once the sound starts playing, all further Play() calls will be ignored
	   * until the interval has elapsed.
	   * @param interval  minimum interval in ms
	   */
	 
	  public void MinimumInterval(int interval) {
	    minimumInterval = interval;
	  }

	  /**
	   * Plays the sound.
	   */
	  
	  public void Play() {
	    if (soundId != 0) {
	      long currentTime = System.currentTimeMillis();
	      if (timeLastPlayed == 0 || currentTime >= timeLastPlayed + minimumInterval) {
	        timeLastPlayed = currentTime;
	        streamId = soundPool.play(soundId, VOLUME_FULL, VOLUME_FULL, 0, LOOP_MODE_NO_LOOP,
	            PLAYBACK_RATE_NORMAL);
	        Log.i("Sound", "SoundPool.play returned stream id " + streamId);
	        if (streamId == 0) {
	          formService.dispatchErrorOccurredEvent(this, "Play",
	              ErrorMessages.ERROR_UNABLE_TO_PLAY_MEDIA, sourcePath);
	        }
	      } else {
	        Log.i("Sound", "Unable to play because MinimumInterval has not elapsed since last play.");
	      }
	    } else {
	      Log.i("Sound", "Unable to play. Did you remember to set the Source property?");
	    }
	  }

	  /**
	   * Pauses playing the sound if it is being played.
	   */
	  
	  public void Pause() {
	    if (streamId != 0) {
	      soundPool.pause(streamId);
	    } else {
	      Log.i("Sound", "Unable to pause. Did you remember to call the Play function?");
	    }
	  }

	  /**
	   * Resumes playing the sound after a pause.
	   */
	  
	  public void Resume() {
	    if (streamId != 0) {
	      soundPool.resume(streamId);
	    } else {
	      Log.i("Sound", "Unable to resume. Did you remember to call the Play function?");
	    }
	  }

	  /**
	   * Stops playing the sound if it is being played.
	   */
	  
	  public void Stop() {
	    if (streamId != 0) {
	      soundPool.stop(streamId);
	      streamId = 0;
	    } else {
	      Log.i("Sound", "Unable to stop. Did you remember to call the Play function?");
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

	 	 
	  // Deleteable implementation

	  @Override
	  public void onDelete() {
	    if (streamId != 0) {
	      soundPool.stop(streamId);
	      soundPool.unload(streamId);
	    }
	    soundPool.release();
	    vibe.cancel();
	    // The documentation for SoundPool suggests setting the reference to null;
	    soundPool = null;
	  }

	  // OnDestroyListener implemenation
	  
	@Override
	public void onDestroy() {
		if (streamId != 0) {
		      soundPool.stop(streamId);
		      soundPool.unload(streamId);
		    }
		    soundPool.release();
		    vibe.cancel();
		    // The documentation for SoundPool suggests setting the reference to null;
		    soundPool = null;
		  		
	}
	
	// OnStartCommandListener implementation

	@Override
	public void onStartCommand() {

		EventDispatcher.dispatchEvent(this, "OnStartCommand");		
	}
	}
