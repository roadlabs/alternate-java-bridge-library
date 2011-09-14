package com.xiledsystems.AlternateJavaBridgelib.components.variants;

import java.lang.reflect.Array;

public final class ArrayVariant extends Variant
{
  private Object value;

  public static final ArrayVariant getArrayVariant(Object value)
  {
    return new ArrayVariant(value);
  }

  private ArrayVariant(Object value)
  {
    super(VARIANT_ARRAY);
    this.value = value;
  }

  public Object getArray()
  {
    return this.value;
  }

  public boolean identical(Variant rightOp)
  {
    if (rightOp.getKind() != 10)
    {
      return super.identical(rightOp);
    }

    return this.value == rightOp.getArray();
  }

  private Object getArrayOfLastDimension(Variant[] indices)
  {
    int lastDimension = indices.length - 1;
    Object array = this.value;
    for (int i = 0; i < lastDimension; i++) {
      array = Array.get(array, indices[i].getInteger());
    }
    return array;
  }

  public Variant array(Variant[] indices)
  {
    Object array = getArrayOfLastDimension(indices);
    Class elementType = array.getClass().getComponentType();
    int lastIndex = indices[(indices.length - 1)].getInteger();

    if (elementType == Boolean.TYPE)
      return BooleanVariant.getBooleanVariant(Array.getBoolean(array, lastIndex));
    if (elementType == Byte.TYPE)
      return ByteVariant.getByteVariant(Array.getByte(array, lastIndex));
    if (elementType == Short.TYPE)
      return ShortVariant.getShortVariant(Array.getShort(array, lastIndex));
    if (elementType == Integer.TYPE)
      return IntegerVariant.getIntegerVariant(Array.getInt(array, lastIndex));
    if (elementType == Long.TYPE)
      return LongVariant.getLongVariant(Array.getLong(array, lastIndex));
    if (elementType == Float.TYPE)
      return SingleVariant.getSingleVariant(Array.getFloat(array, lastIndex));
    if (elementType == Double.TYPE)
      return DoubleVariant.getDoubleVariant(Array.getDouble(array, lastIndex));
    if (elementType == String.class) {
      return StringVariant.getStringVariant((String)Array.get(array, lastIndex));
    }
    return ObjectVariant.getObjectVariant(Array.get(array, lastIndex));
  }

  public void array(Variant[] indices, Variant variant)
  {
    Object array = getArrayOfLastDimension(indices);
    Class elementType = array.getClass().getComponentType();
    int lastIndex = indices[(indices.length - 1)].getInteger();

    if (elementType == Boolean.TYPE)
      Array.set(array, lastIndex, Boolean.valueOf(variant.getBoolean()));
    else if (elementType == Byte.TYPE)
      Array.set(array, lastIndex, Byte.valueOf(variant.getByte()));
    else if (elementType == Short.TYPE)
      Array.set(array, lastIndex, Short.valueOf(variant.getShort()));
    else if (elementType == Integer.TYPE)
      Array.set(array, lastIndex, Integer.valueOf(variant.getInteger()));
    else if (elementType == Long.TYPE)
      Array.set(array, lastIndex, Long.valueOf(variant.getLong()));
    else if (elementType == Float.TYPE)
      Array.set(array, lastIndex, Float.valueOf(variant.getSingle()));
    else if (elementType == Double.TYPE)
      Array.set(array, lastIndex, Double.valueOf(variant.getDouble()));
    else if (elementType == String.class)
      Array.set(array, lastIndex, variant.getString());
    else
      Array.set(array, lastIndex, variant.getObject());
  }
}
