package com.xiledsystems.AlternateJavaBridgelib.components.common;

import java.util.HashMap;
import java.util.Map;

public enum ComponentCategory
{
  BASIC("Basic"), 
  MEDIA("Media"), 
  ANIMATION("Animation"), 
  SOCIAL("Social"), 
  SENSORS("Sensors"), 
  ARRANGEMENTS("Screen Arrangement"), 
  LEGOMINDSTORMS("LEGO® MINDSTORMS®"), 
  MISC("Other stuff"), 
  EXPERIMENTAL("Not ready for prime time"), 
  OBSOLETE("Old stuff"), 
  INTERNAL("For internal use only"), 

  UNINITIALIZED("Uninitialized");

  private static final Map<String, String> DOC_MAP;
  private String name;

  private ComponentCategory(String categoryName)
  {
    this.name = categoryName;
  }

  public String getName() {
    return this.name;
  }

  public String getDocName() {
    return (String)DOC_MAP.get(this.name);
  }

  static
  {
    DOC_MAP = new HashMap();

    DOC_MAP.put("Basic", "basic");
    DOC_MAP.put("Media", "media");
    DOC_MAP.put("Animation", "animation");
    DOC_MAP.put("Social", "social");
    DOC_MAP.put("Sensors", "sensors");
    DOC_MAP.put("Screen Arrangement", "screenarrangement");
    DOC_MAP.put("LEGO® MINDSTORMS®", "legomindstorms");
    DOC_MAP.put("Other stuff", "other");
    DOC_MAP.put("Not ready for prime time", "notready");
  }
}
