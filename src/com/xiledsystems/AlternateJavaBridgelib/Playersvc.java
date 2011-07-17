package com.xiledsystems.AlternateJavaBridgelib;

import java.io.IOException;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import com.google.devtools.simple.runtime.components.Component;
import com.google.devtools.simple.runtime.components.android.Deleteable;
import com.google.devtools.simple.runtime.components.util.ErrorMessages;
import com.google.devtools.simple.runtime.events.EventDispatcher;

/*
 * Playersvc class to use a player in a service (good for music). This has been mostly copied from
 * Java Bridge's Player class. It has been modified to run in a service.
 * 
 * This service registers an OnStartCommand Event. This allows you to add more to this component. When the service
 * first starts, it calls the define() method, then OnStartCommand. Once the sevice is running, if it gets another
 * intent to start it, it only runs OnStartCommand.
 * 
 * Ryan Bis - www.xiledsystems.com
 * 
 */

public class Playersvc extends AndroidServiceComponent implements Component, Deleteable, OnStartCommandListener,
 OnDestroyListener {

    private MediaPlayer mp;
      private final Vibrator vibe;

      private int playerState;
      private String sourcePath;

      /*
       * playerState encodes a simplified version of the full MediaPlayer state space, that should be
       * adequate, given this API:
       * 0: player initial state
       * 1: player prepared but not started
       * 2: player started or paused
       * The allowable transitions are:
       * Start: must be called in state 1 or 2, results in state 2
       * Pause: must be called in state 2, results in state 2
       * Stop: must be called in state 1 or 2, results in state 1
       * We can simplify this to remove state 0 and use a simple boolean after we're
       * more confident that there are no start-up problems.
       */

      /**
       * Creates a new Player component.
       *
       * @param container
       */
      public Playersvc(SvcComponentContainer container) {
        super(container.$formService());
        sourcePath = "";
        vibe = (Vibrator) formService.getSystemService(Context.VIBRATOR_SERVICE);
        formService.registerForOnStartCommand(this);
        formService.registerForOnDestroy(this);
      }

      /**
       * Returns the path to the audio or video source
       */
      
      public String Source() {
        return sourcePath;
      }

      /**
       * Sets the audio or video source.
       *
       * <p/>See {@link MediaUtil#determineMediaSource} for information about what
       * a path can be.
       *
       * @param path  the path to the audio or video source
       */
      
      public void Source(String path) {
        sourcePath = (path == null) ? "" : path;

        // Clear the previous MediaPlayer.
        if (playerState == 1 || playerState == 2) {
          mp.stop();
        }
        playerState = 0;
        if (mp != null) {
          mp.release();
          mp = null;
        }

        if (sourcePath.length() > 0) {
          Log.i("Player", "Source path is " + sourcePath);
          mp = new MediaPlayer();

          try {
            MediaUtilsvc.loadMediaPlayer(mp, formService, sourcePath);

          } catch (IOException e) {
            mp.release();
            mp = null;
            formService.dispatchErrorOccurredEvent(this, "Source",
                ErrorMessages.ERROR_UNABLE_TO_LOAD_MEDIA, sourcePath);
            return;
          }

          mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
          Log.i("Player", "Successfully loaded source path " + sourcePath);

          // The Simple API is set up so that the user never has
          // to call prepare.
          prepare();
          // Player should now be in state 1. (If prepare failed, we are in state 0.)
        }
      }

      /**
       * Plays the media.  If it was previously paused, the playing is resumed.
       */
      
      public void Start() {
        Log.i("Player", "Calling Start -- State=" + playerState);
        if (playerState == 1 || playerState == 2) {
          mp.start();
          playerState = 2;
          // Player should now be in state 2
        }
      }

      /**
       * Suspends playing the media if it is playing.
       */
      
      public void Pause() {
        Log.i("Player", "Calling Pause -- State=" + playerState);
        if (playerState == 2) {
          mp.pause();
          playerState = 2;
          // Player should now be in state 2.
        }
      }

      /**
       * Stops playing the media
       */
      
      public void Stop() {
        Log.i("Player", "Calling Stop -- State=" + playerState);
        if (playerState == 1 || playerState == 2) {
          mp.stop();
          prepare();
          // Player should now be in state 1. (If prepare failed, we are in state 0.)
        }
      }

      //  TODO: Reconsider whether vibrate should be here or in a separate component.
      /**
       * Vibrates for specified number of milliseconds.
       */
      
      public void Vibrate(long milliseconds) {
        vibe.vibrate(milliseconds);
      }

     
      public void PlayerError(String message) {
      }

      private void prepare() {
        // This should be called only after mp.stop() or directly after
        // initialization
        try {
          mp.prepare();
          playerState = 1;
          Log.i("Player", "Successfully prepared");

        } catch (IOException ioe) {
          mp.release();
          mp = null;
          playerState = 0;
          formService.dispatchErrorOccurredEvent(this, "Source",
              ErrorMessages.ERROR_UNABLE_TO_PREPARE_MEDIA, sourcePath);
        }
      }

      // Deleteable implementation

      @Override
      public void onDelete() {
        mp.stop();
        mp.release();
        vibe.cancel();
      }

    @Override
    public void onDestroy() {
        mp.stop();
        mp.release();
        vibe.cancel();
        
    }

    @Override
    public void onStartCommand() {
        EventDispatcher.dispatchEvent(this, "OnStartCommand");        
    }
    }
