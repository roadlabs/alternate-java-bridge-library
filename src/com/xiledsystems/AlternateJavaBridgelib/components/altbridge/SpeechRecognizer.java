package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;

import java.util.ArrayList;

/**
 * Component for using the built in VoiceRecognizer to convert speech to text.
 * For more details, please see:
 * http://developer.android.com/reference/android/speech/RecognizerIntent.html
 *
 */

public class SpeechRecognizer extends AndroidNonvisibleComponent
    implements Component, ActivityResultListener {

  private final ComponentContainer container;
  private String result;

  /* Used to identify the call to startActivityForResult. Will be passed back
     into the resultReturned() callback method. */
  private int requestCode;

  /**
   * Creates a SpeechRecognizer component.
   *
   * @param container container, component will be placed in
   */
  public SpeechRecognizer(ComponentContainer container) {
    super(container.$form());
    this.container = container;
    result = "";
  }

  /**
   * Result property getter method.
   */
  
  public String Result() {
    return result;
  }

  
  public void GetText() {
    BeforeGettingText();
    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
    if (requestCode == 0) {
      requestCode = form.registerForActivityResult(this);
    }
    container.$context().startActivityForResult(intent, requestCode);
  }

  @Override
  public void resultReturned(int requestCode, int resultCode, Intent data) {
    Log.i("VoiceRecognizer",
        "Returning result. Request code = " + requestCode + ", result code = " + resultCode);
    if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK) {
      if (data.hasExtra(RecognizerIntent.EXTRA_RESULTS)) {
        ArrayList<String> results;
        results = data.getExtras().getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
        result = results.get(0);
      } else {
        result = "";
      }
      AfterGettingText(result);
    }
  }

  /**
   * Simple event to raise when VoiceReco is invoked but before the VoiceReco
   * activity is started.
   */
  
  public void BeforeGettingText() {
    EventDispatcher.dispatchEvent(this, "BeforeGettingText");
  }

  /**
   * Simple event to raise after the VoiceReco activity has returned
   */
  
  public void AfterGettingText(String result) {
    EventDispatcher.dispatchEvent(this, "AfterGettingText", result);
  }

}
