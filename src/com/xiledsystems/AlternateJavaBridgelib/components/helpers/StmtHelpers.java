package com.xiledsystems.AlternateJavaBridgelib.components.helpers;

import com.xiledsystems.AlternateJavaBridgelib.components.Arrays;
import com.xiledsystems.AlternateJavaBridgelib.components.variants.ArrayVariant;
import com.xiledsystems.AlternateJavaBridgelib.components.variants.IntegerVariant;
import com.xiledsystems.AlternateJavaBridgelib.components.variants.ObjectVariant;
import com.xiledsystems.AlternateJavaBridgelib.components.variants.Variant;
import com.xiledsystems.AlternateJavaBridgelib.components.collections.Collection;

public final class StmtHelpers
{
  public static int forEachCount(Variant v)
  {
    if ((v instanceof ArrayVariant)) {
      return Arrays.UBound(v, 1);
    }

    return ((Collection)((ObjectVariant)v).getObject()).Count();
  }

  public static Variant forEachItem(Variant v, int index)
  {
    if ((v instanceof ArrayVariant)) {
      return ((ArrayVariant)v).array(new Variant[] { IntegerVariant.getIntegerVariant(index) });
    }

    return ((Collection)((ObjectVariant)v).getObject()).Item(index);
  }
}
