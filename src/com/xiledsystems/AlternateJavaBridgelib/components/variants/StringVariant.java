package com.xiledsystems.AlternateJavaBridgelib.components.variants;

import com.xiledsystems.AlternateJavaBridgelib.components.helpers.ConvHelpers;
import com.xiledsystems.AlternateJavaBridgelib.components.helpers.ExprHelpers;

public final class StringVariant extends Variant
{
  private String value;

  public static final StringVariant getStringVariant(String value)
  {
    return new StringVariant(value);
  }

  private StringVariant(String value)
  {
    super(VARIANT_STRING);
    this.value = value;
  }

  public boolean getBoolean()
  {
    return ConvHelpers.string2boolean(this.value);
  }

  public byte getByte()
  {
    return ConvHelpers.string2byte(this.value);
  }

  public short getShort()
  {
    return ConvHelpers.string2short(this.value);
  }

  public int getInteger()
  {
    return ConvHelpers.string2integer(this.value);
  }

  public long getLong()
  {
    return ConvHelpers.string2long(this.value);
  }

  public float getSingle()
  {
    return ConvHelpers.string2single(this.value);
  }

  public double getDouble()
  {
    return ConvHelpers.string2double(this.value);
  }

  public String getString()
  {
    return this.value;
  }

  public Variant add(Variant rightOp)
  {
    return DoubleVariant.getDoubleVariant(getDouble()).add(rightOp);
  }

  public Variant sub(Variant rightOp)
  {
    return DoubleVariant.getDoubleVariant(getDouble()).sub(rightOp);
  }

  public Variant mul(Variant rightOp)
  {
    return DoubleVariant.getDoubleVariant(getDouble()).mul(rightOp);
  }

  public Variant div(Variant rightOp)
  {
    return DoubleVariant.getDoubleVariant(getDouble()).div(rightOp);
  }

  public Variant idiv(Variant rightOp)
  {
    return DoubleVariant.getDoubleVariant(getDouble()).idiv(rightOp);
  }

  public Variant mod(Variant rightOp)
  {
    return DoubleVariant.getDoubleVariant(getDouble()).mod(rightOp);
  }

  public Variant pow(Variant rightOp)
  {
    return DoubleVariant.getDoubleVariant(getDouble()).pow(rightOp);
  }

  public Variant neg()
  {
    return DoubleVariant.getDoubleVariant(getDouble()).neg();
  }

  public Variant shl(Variant rightOp)
  {
    return LongVariant.getLongVariant(getLong()).shl(rightOp);
  }

  public Variant shr(Variant rightOp)
  {
    return LongVariant.getLongVariant(getLong()).shr(rightOp);
  }

  public int cmp(Variant rightOp)
  {
    if ((getKind() == 8) && (rightOp.getKind() == 8)) {
      return this.value.compareTo(rightOp.getString());
    }

    return DoubleVariant.getDoubleVariant(getDouble()).cmp(rightOp);
  }

  public boolean like(Variant rightOp)
  {
    return ExprHelpers.like(this.value, rightOp.getString());
  }

  public Variant not()
  {
    return LongVariant.getLongVariant(getLong()).not();
  }

  public Variant and(Variant rightOp)
  {
    return LongVariant.getLongVariant(getLong()).and(rightOp);
  }

  public Variant or(Variant rightOp)
  {
    return LongVariant.getLongVariant(getLong()).or(rightOp);
  }

  public Variant xor(Variant rightOp)
  {
    return LongVariant.getLongVariant(getLong()).xor(rightOp);
  }
}
