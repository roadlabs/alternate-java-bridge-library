package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;

/**
 * Persistently store YAIL values on the phone using tags to store and retrieve.
 *
 */

public class TinyDB extends AndroidNonvisibleComponent implements Component, Deleteable {

  private SharedPreferences sharedPreferences;

  /**
   * Creates a new TinyDB component.
   *
   * @param container the Form that this component is contained in.
   */
  public TinyDB(ComponentContainer container) {
    super(container.$form());
    final Context context = (Context) container.$context();
    sharedPreferences = context.getSharedPreferences("TinyDB", Context.MODE_PRIVATE);
  }

  /**
   * Store the given value under the given tag.  The storage persists on the
   * phone when the app is restarted.
   *
   * @param tag The tag to use
   * @param valueToStore The value to store. Can be any type of value (e.g.
   * number, text, boolean or list).
   */
  
  public void StoreValue(final String tag, final Object valueToStore) {
    final SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();
    try {
      sharedPrefsEditor.putString(tag, JsonUtil.getJsonRepresentation(valueToStore));
      sharedPrefsEditor.commit();
    } catch (JSONException e) {
      throw new YailRuntimeError("Value failed to convert to JSON.", "JSON Creation Error.");
    }
  }

  /**
   * Retrieve the value stored under the given tag.
   *
   * @param tag The tag to use
   * @return The value stored under the tag. Can be any type of value (e.g.
   * number, text, boolean or list).
   */
  
  public Object GetValue(final String tag) {
    try {
      String value = sharedPreferences.getString(tag, "");
      // If there's no entry with tag as a key then return the empty string.
      return (value.length() == 0) ? "" : JsonUtil.getObjectFromJson(value);
    } catch (JSONException e) {
      throw new YailRuntimeError("Value failed to convert from JSON.", "JSON Creation Error.");
    }
  }

  @Override
  public void onDelete() {
    final SharedPreferences.Editor sharedPrefsEditor = sharedPreferences.edit();
    sharedPrefsEditor.clear();
    sharedPrefsEditor.commit();
  }
}
