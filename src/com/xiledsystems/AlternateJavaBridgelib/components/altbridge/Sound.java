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
  private Map<String, Integer> playIds = new HashMap<String, Integer>();
  private final ArrayList<Map<String, Integer>> streamIds;

  private String sourcePath;              // name of source
  private int soundId;                    // id of sound in the soundPool
  private int streamId;                   // stream id returned from last call to SoundPool.play  
 
  private final Vibrator vibe;
  private boolean initialized = false;  
  private final boolean isService;


  public Sound(ComponentContainer container) {
    super(container);
    soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
    soundMap = new HashMap<String, Integer>();
    resIdMap = new ArrayList<String[]>();
    streamIds = new ArrayList<Map<String,Integer>>();
    vibe = (Vibrator) container.$form().getSystemService(Context.VIBRATOR_SERVICE);
    sourcePath = "";
    container.$form().registerForOnResume(this);
    container.$form().registerForOnStop(this);
    container.$form().registerForOnInitialize(this);
    this.isService = false;

 }
  
  public Sound(ComponentContainer container, int maxstreams) {
	    super(container);
	    soundPool = new SoundPool(maxstreams, AudioManager.STREAM_MUSIC, 0);
	    soundMap = new HashMap<String, Integer>();
	    resIdMap = new ArrayList<String[]>();
	    streamIds = new ArrayList<Map<String,Integer>>();
	    vibe = (Vibrator) container.$form().getSystemService(Context.VIBRATOR_SERVICE);
	    sourcePath = "";
	    container.$form().registerForOnResume(this);
	    container.$form().registerForOnStop(this);
	    this.isService = false;
	   
	  }
  
  public Sound(SvcComponentContainer container) {
	    super(container);
	    soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
	    soundMap = new HashMap<String, Integer>();
	    resIdMap = new ArrayList<String[]>();
	    streamIds = new ArrayList<Map<String,Integer>>();
	    vibe = (Vibrator) container.$formService().getSystemService(Context.VIBRATOR_SERVICE);
	    sourcePath = "";
	    container.$formService().registerForOnDestroy(this);
	    this.isService = true;

	 }
	  
	  public Sound(SvcComponentContainer container, int maxstreams) {
		    super(container);
		    soundPool = new SoundPool(maxstreams, AudioManager.STREAM_MUSIC, 0);
		    soundMap = new HashMap<String, Integer>();
		    resIdMap = new ArrayList<String[]>();
		    streamIds = new ArrayList<Map<String,Integer>>();
		    vibe = (Vibrator) container.$formService().getSystemService(Context.VIBRATOR_SERVICE);
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
	  int temp;
	  if (isService) {
		  temp = sContainer.$formService().getResources().getIdentifier(filename, "raw", sContainer.$formService().getPackageName()); 	  
	  } else {
		  temp = container.$form().getResources().getIdentifier(filename, "raw", container.$form().getPackageName());
	  }
	 
	  if (initialized) {
		  try {
			  int tmp;
			  if (isService) {
				  tmp = MediaUtil.loadSoundPool(soundPool, sContainer.$formService(), filename);
			  } else {
				  tmp = MediaUtil.loadSoundPool(soundPool, container.$form(), filename);
			  }
			  
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
        playIds.clear();
        playIds.put(tag, streamId);
        streamIds.add(playIds);
        Log.i("Sound", "SoundPool.play returned stream id " + streamId);
        if (streamId == 0) {
        	if (isService) {
        		sContainer.$formService().dispatchErrorOccurredEvent(this, "Play",
        				ErrorMessages.ERROR_UNABLE_TO_PLAY_MEDIA, sourcePath);
        	} else {
        		container.$form().dispatchErrorOccurredEvent(this, "Play",
        				ErrorMessages.ERROR_UNABLE_TO_PLAY_MEDIA, sourcePath);
        	}
        }
    } else {
      Log.i("Sound", "Unable to play. Did you remember to set the Source property? Tag: "+tag);
    }
  }
  
  public void Stop(String tag) {
	  
	  if (streamId != 0) {
		  for (int i = 0; i < streamIds.size(); i++) {
			  playIds.clear();
			  playIds = streamIds.get(i);
			  if (playIds.containsKey(tag)) {
				  soundPool.stop(playIds.get(tag));
				  streamIds.remove(i);
				  return;
			  }
		  }		  
	  }	  
  }
  
  public void AllStop() {
	  
	  if (streamIds.size()>0) {
		  for (int i = 0; i < streamIds.size(); i++) {
			  playIds.clear();
			  playIds = streamIds.get(i);
			  soundPool.stop(Integer.parseInt(playIds.values().toArray()[0].toString()));
			  streamIds.remove(i);
		  }
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
				  int temp;
				  if (isService) {
					  temp = MediaUtil.loadSoundPool(soundPool, sContainer.$formService(), resIdMap.get(i)[2]);
				  } else {
					  temp = MediaUtil.loadSoundPool(soundPool, container.$form(), resIdMap.get(i)[2]);
				  }
				  
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
