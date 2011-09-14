package com.xiledsystems.AlternateJavaBridgelib.components.variants;

import com.xiledsystems.AlternateJavaBridgelib.components.helpers.ConvHelpers;
import com.xiledsystems.AlternateJavaBridgelib.components.helpers.ExprHelpers;

public final class DoubleVariant extends Variant
{
  private double value;

  public static final DoubleVariant getDoubleVariant(double value)
  {
    return new DoubleVariant(value);
  }

  private DoubleVariant(double value)
  {
    super(VARIANT_DOUBLE);
    this.value = value;
  }

  public boolean getBoolean()
  {
    return ConvHelpers.double2boolean(this.value);
  }

  public byte getByte()
  {
    return ConvHelpers.double2byte(this.value);
  }

  public short getShort()
  {
    return ConvHelpers.double2short(this.value);
  }

  public int getInteger()
  {
    return (int)this.value;
  }

  public long getLong()
  {
    return (long)this.value;
  }

  public float getSingle()
  {
    return (float)this.value;
  }

  public double getDouble()
  {
    return this.value;
  }

  public String getString()
  {
    return Double.toString(this.value);
  }

  public Variant add(Variant rightOp)
  {
    return getDoubleVariant(this.value + rightOp.getDouble());
  }

  public Variant sub(Variant rightOp)
  {
    return getDoubleVariant(this.value - rightOp.getDouble());
  }

  public Variant mul(Variant rightOp)
  {
    return getDoubleVariant(this.value * rightOp.getDouble());
  }

  public Variant div(Variant rightOp)
  {
    return getDoubleVariant(this.value / rightOp.getDouble());
  }

  public Variant idiv(Variant rightOp)
  {
    return getDoubleVariant(ExprHelpers.idiv(this.value, rightOp.getDouble()));
  }

  public Variant mod(Variant rightOp)
  {
    return getDoubleVariant(this.value % rightOp.getDouble());
  }

  public Variant pow(Variant rightOp)
  {
    return getDoubleVariant(Math.pow(this.value, rightOp.getDouble()));
  }

  public Variant neg()
  {
    return getDoubleVariant(-this.value);
  }

  public int cmp(Variant rightOp)
  {
    double rightValue = rightOp.getDouble();
    if (this.value == rightValue) {
      return 0;
    }
    return this.value > rightValue ? 1 : -1;
  }
}
