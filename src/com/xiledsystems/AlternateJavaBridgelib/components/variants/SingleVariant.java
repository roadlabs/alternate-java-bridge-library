package com.xiledsystems.AlternateJavaBridgelib.components.variants;

import com.xiledsystems.AlternateJavaBridgelib.components.helpers.ConvHelpers;
import com.xiledsystems.AlternateJavaBridgelib.components.helpers.ExprHelpers;

public final class SingleVariant extends Variant
{
  private float value;

  public static final SingleVariant getSingleVariant(float value)
  {
    return new SingleVariant(value);
  }

  private SingleVariant(float value)
  {
    super(VARIANT_SINGLE);
    this.value = value;
  }

  public boolean getBoolean()
  {
    return ConvHelpers.single2boolean(this.value);
  }

  public byte getByte()
  {
    return ConvHelpers.single2byte(this.value);
  }

  public short getShort()
  {
    return ConvHelpers.single2short(this.value);
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
    return this.value;
  }

  public double getDouble()
  {
    return this.value;
  }

  public String getString()
  {
    return Float.toString(this.value);
  }

  public Variant add(Variant rightOp)
  {
    switch (rightOp.getKind()) {
    default:
      return rightOp.add(this);
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
    }
    return getSingleVariant(getSingle() + rightOp.getSingle());
  }

  public Variant sub(Variant rightOp)
  {
    switch (rightOp.getKind())
    {
    default:
      return super.sub(rightOp);
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
      return getSingleVariant(getSingle() - rightOp.getSingle());
    case 7:
    case 8:
    }
    return DoubleVariant.getDoubleVariant(getDouble() - rightOp.getDouble());
  }

  public Variant mul(Variant rightOp)
  {
    switch (rightOp.getKind()) {
    default:
      return rightOp.mul(this);
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
    }
    return getSingleVariant(getSingle() * rightOp.getSingle());
  }

  public Variant div(Variant rightOp)
  {
    return DoubleVariant.getDoubleVariant(getDouble()).div(rightOp);
  }

  public Variant idiv(Variant rightOp)
  {
    switch (rightOp.getKind())
    {
    default:
      return super.idiv(rightOp);
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
      return getSingleVariant(ExprHelpers.idiv(getSingle(), rightOp.getSingle()));
    case 7:
    case 8:
    }
    return DoubleVariant.getDoubleVariant(ExprHelpers.idiv(getDouble(), rightOp.getDouble()));
  }

  public Variant mod(Variant rightOp)
  {
    switch (rightOp.getKind())
    {
    default:
      return super.mod(rightOp);
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
      return getSingleVariant(getSingle() % rightOp.getSingle());
    case 7:
    case 8:
    }
    return DoubleVariant.getDoubleVariant(getDouble() % rightOp.getDouble());
  }

  public Variant pow(Variant rightOp)
  {
    return DoubleVariant.getDoubleVariant(Math.pow(getDouble(), rightOp.getDouble()));
  }

  public Variant neg()
  {
    return getSingleVariant(-this.value);
  }

  public int cmp(Variant rightOp)
  {
    switch (rightOp.getKind()) {
    default:
      return -rightOp.cmp(this);
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
    }
    float rvalue = rightOp.getSingle();
    if (this.value == rvalue) {
      return 0;
    }
    return this.value > rvalue ? 1 : -1;
  }
}
