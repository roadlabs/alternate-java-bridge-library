package com.xiledsystems.AlternateJavaBridgelib.components.variants;

import com.xiledsystems.AlternateJavaBridgelib.components.helpers.ConvHelpers;
import com.xiledsystems.AlternateJavaBridgelib.components.helpers.ExprHelpers;

public final class IntegerVariant extends Variant
{
  private int value;

  public static final IntegerVariant getIntegerVariant(int value)
  {
    return new IntegerVariant(value);
  }

  private IntegerVariant(int value)
  {
    super(VARIANT_INTEGER);

    this.value = value;
  }

  public boolean getBoolean()
  {
    return ConvHelpers.integer2boolean(this.value);
  }

  public byte getByte()
  {
    return ConvHelpers.integer2byte(this.value);
  }

  public short getShort()
  {
    return ConvHelpers.integer2short(this.value);
  }

  public int getInteger()
  {
    return this.value;
  }

  public long getLong()
  {
    return this.value;
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
    return Integer.toString(this.value);
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
    }
    return getIntegerVariant(getInteger() + rightOp.getInteger());
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
      return getIntegerVariant(getInteger() - rightOp.getInteger());
    case 5:
      return LongVariant.getLongVariant(getLong() - rightOp.getLong());
    case 6:
      return SingleVariant.getSingleVariant(getSingle() - rightOp.getSingle());
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
    }
    return getIntegerVariant(getInteger() * rightOp.getInteger());
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
      return getIntegerVariant(getInteger() / rightOp.getInteger());
    case 5:
      return LongVariant.getLongVariant(getLong() / rightOp.getLong());
    case 6:
      return SingleVariant.getSingleVariant(ExprHelpers.idiv(getSingle(), rightOp.getSingle()));
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
      return getIntegerVariant(getInteger() % rightOp.getInteger());
    case 5:
      return LongVariant.getLongVariant(getLong() % rightOp.getLong());
    case 6:
      return SingleVariant.getSingleVariant(getSingle() % rightOp.getSingle());
    case 7:
    case 8:
    }
    return DoubleVariant.getDoubleVariant(getDouble() % rightOp.getDouble());
  }

  public Variant pow(Variant rightOp)
  {
    return DoubleVariant.getDoubleVariant(getDouble()).pow(rightOp);
  }

  public Variant neg()
  {
    return getIntegerVariant(-this.value);
  }

  public Variant shl(Variant rightOp)
  {
    switch (rightOp.getKind()) { case 6:
    case 7:
    default:
      return super.and(rightOp);
    case 1:
    case 2:
    case 3:
    case 4:
      return getIntegerVariant(getInteger() << rightOp.getInteger());
    case 5:
    case 8:
    }
    return LongVariant.getLongVariant(getLong() << (int)rightOp.getLong());
  }

  public Variant shr(Variant rightOp)
  {
    switch (rightOp.getKind()) { case 6:
    case 7:
    default:
      return super.and(rightOp);
    case 1:
    case 2:
    case 3:
    case 4:
      return getIntegerVariant(getInteger() >> rightOp.getInteger());
    case 5:
    case 8:
    }
    return LongVariant.getLongVariant(getLong() >> (int)rightOp.getLong());
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
    }
    return this.value - rightOp.getInteger();
  }

  public Variant not()
  {
    return getIntegerVariant(this.value ^ 0xFFFFFFFF);
  }

  public Variant and(Variant rightOp)
  {
    switch (rightOp.getKind()) { case 6:
    case 7:
    default:
      return super.and(rightOp);
    case 1:
    case 2:
    case 3:
    case 4:
      return getIntegerVariant(getInteger() & rightOp.getInteger());
    case 5:
    case 8:
    }
    return LongVariant.getLongVariant(getLong() & rightOp.getLong());
  }

  public Variant or(Variant rightOp)
  {
    switch (rightOp.getKind()) { case 6:
    case 7:
    default:
      return super.or(rightOp);
    case 1:
    case 2:
    case 3:
    case 4:
      return getIntegerVariant(getInteger() | rightOp.getInteger());
    case 5:
    case 8:
    }
    return LongVariant.getLongVariant(getLong() | rightOp.getLong());
  }

  public Variant xor(Variant rightOp)
  {
    switch (rightOp.getKind()) { case 6:
    case 7:
    default:
      return super.xor(rightOp);
    case 1:
    case 2:
    case 3:
    case 4:
      return getIntegerVariant(getInteger() ^ rightOp.getInteger());
    case 5:
    case 8:
    }
    return LongVariant.getLongVariant(getLong() ^ rightOp.getLong());
  }
}
