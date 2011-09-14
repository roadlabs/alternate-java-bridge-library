package com.xiledsystems.AlternateJavaBridgelib.components.common;

public enum PropertyCategory
{
  BEHAVIOR("Behavior"), 
  APPEARANCE("Appearance"), 
  DEPRECATED("Deprecated"), 
  UNSET("Unspecified");

  private String name;

  private PropertyCategory(String categoryName) { this.name = categoryName; }

  public String getName()
  {
    return this.name;
  }
}