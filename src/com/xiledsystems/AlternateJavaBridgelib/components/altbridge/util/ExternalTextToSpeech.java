package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;

/**
 */
public class ExternalTextToSpeech implements ITextToSpeech, ActivityResultListener {

  private static final String TTS_INTENT = "com.google.tts.makeBagel";

  /* Used to identify the call to startActivityForResult. Will be passed back
     into the resultReturned() callback method. */
  private int requestCode;

  private final ComponentContainer container;

  private final TextToSpeechCallback callback;

  public ExternalTextToSpeech(ComponentContainer container,
                              TextToSpeechCallback callback) {
    this.container = container;
    this.callback = callback;
  }

  @Override
  public void speak(String message, Locale loc) {
    Intent intent = new Intent(TTS_INTENT);
    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    intent.putExtra("message", message);
    intent.putExtra("language", loc.getISO3Language());
    intent.putExtra("country", loc.getISO3Country());
    if (requestCode == 0) {
      requestCode = container.$form().registerForActivityResult(this);
    }
    container.$context().startActivityForResult(intent, requestCode);
  }

  @Override
  public void onStop() {
    // nothing to do
  }

  @Override
  public void onResume() {
    // nothing to do
  }

  @Override
  public void resultReturned(int requestCode, int resultCode, Intent data) {
    boolean isSuccess = (requestCode == this.requestCode) && (resultCode == Activity.RESULT_OK);
    if (isSuccess) {
      callback.onSuccess();
    } else {
      callback.onFailure();
    }
  }

}
