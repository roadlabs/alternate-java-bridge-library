package com.xiledsystems.AlternateJavaBridgelib.components;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

import com.xiledsystems.AlternateJavaBridgelib.components.variants.Variant;


public final class Arrays
{
  
  public static String[] Filter(String[] array, String str, boolean include)
  {
    if (str == null) {
      throw new NullPointerException();
    }

    Set result = new HashSet();
    for (String a : array) {
      if (a.contains(str) == include) {
        result.add(a);
      }
    }
    return (String[])result.toArray(new String[result.size()]);
  }

  
  public static String Join(String[] array, String separator)
  {
    if (separator == null) {
      throw new NullPointerException();
    }

    StringBuilder sb = new StringBuilder();
    String sep = "";
    for (String a : array) {
      sb.append(sep).append(a);
      sep = separator;
    }
    return sb.toString();
  }

  
  public static String[] Split(String str, String separator, int count)
  {
    if (separator == null) {
      throw new NullPointerException();
    }

    if (count <= 0) {
      throw new IllegalArgumentException("Count for Split() out of range. Should greater than 0.");
    }

    return str.split(new StringBuilder().append("\\Q").append(separator).append("\\E").toString(), count);
  }

  
  public static int UBound(Variant array, int dim)
  {
    if (dim <= 0) {
      throw new IllegalArgumentException("Dimension for UBound() out of range. Should be greater than 0.");
    }

    Object arr = array.getArray();
    while (true) { dim--; if (dim <= 0) break;
      arr = Array.get(arr, 0);
    }

    return Array.getLength(arr);
  }
}
