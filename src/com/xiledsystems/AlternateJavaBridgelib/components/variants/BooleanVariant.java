package com.xiledsystems.AlternateJavaBridgelib.components.variants;

import com.xiledsystems.AlternateJavaBridgelib.components.helpers.ConvHelpers;

public final class BooleanVariant extends Variant
{
  private boolean value;

  public static final BooleanVariant getBooleanVariant(boolean value)
  {
    return new BooleanVariant(value);
  }

  private BooleanVariant(boolean value)
  {
    super(VARIANT_BOOLEAN);
    this.value = value;
  }

  public boolean getBoolean()
  {
    return this.value;
  }

  public byte getByte()
  {
    return (byte)ConvHelpers.boolean2integer(this.value);
  }

  public short getShort()
  {
    return (short)ConvHelpers.boolean2integer(this.value);
  }

  public int getInteger()
  {
    return ConvHelpers.boolean2integer(this.value);
  }

  public long getLong()
  {
    return ConvHelpers.boolean2long(this.value);
  }

  public float getSingle()
  {
    return ConvHelpers.boolean2single(this.value);
  }

  public double getDouble()
  {
    return ConvHelpers.boolean2double(this.value);
  }

  public String getString()
  {
    return ConvHelpers.boolean2string(this.value);
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
    return IntegerVariant.getIntegerVariant(getInteger() + rightOp.getInteger());
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
      return IntegerVariant.getIntegerVariant(getInteger() - rightOp.getInteger());
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
    return IntegerVariant.getIntegerVariant(getInteger() * rightOp.getInteger());
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
      return IntegerVariant.getIntegerVariant(getInteger() / rightOp.getInteger());
    case 5:
      return LongVariant.getLongVariant(getLong() / rightOp.getLong());
    case 6:
      return SingleVariant.getSingleVariant(getSingle()).idiv(rightOp);
    case 7:
    case 8:
    }
    return DoubleVariant.getDoubleVariant(getDouble()).idiv(rightOp);
  }

  public Variant mod(Variant rightOp)
  {
    switch (rightOp.getKind())
    {
    default:
      return super.sub(rightOp);
    case 1:
    case 2:
    case 3:
    case 4:
      return IntegerVariant.getIntegerVariant(getInteger() % rightOp.getInteger());
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
    return IntegerVariant.getIntegerVariant(-getInteger());
  }

  public Variant shl(Variant rightOp)
  {
    return this;
  }

  public Variant shr(Variant rightOp)
  {
    return this;
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
    return getInteger() - rightOp.getInteger();
  }

  public Variant not()
  {
    return getBooleanVariant(!this.value);
  }

  public Variant and(Variant rightOp)
  {
    switch (rightOp.getKind()) { case 6:
    case 7:
    default:
      return super.and(rightOp);
    case 1:
      return getBooleanVariant(this.value & rightOp.getBoolean());
    case 2:
    case 3:
    case 4:
      return IntegerVariant.getIntegerVariant(getInteger() & rightOp.getInteger());
    case 5:
    case 8:
    }
    return LongVariant.getLongVariant(getInteger() & rightOp.getLong());
  }

  public Variant or(Variant rightOp)
  {
    switch (rightOp.getKind()) { case 6:
    case 7:
    default:
      return super.or(rightOp);
    case 1:
      return getBooleanVariant(this.value | rightOp.getBoolean());
    case 2:
    case 3:
    case 4:
      return IntegerVariant.getIntegerVariant(getInteger() | rightOp.getInteger());
    case 5:
    case 8:
    }
    return LongVariant.getLongVariant(getInteger() | rightOp.getLong());
  }

  public Variant xor(Variant rightOp)
  {
    switch (rightOp.getKind()) { case 6:
    case 7:
    default:
      return super.xor(rightOp);
    case 1:
      return getBooleanVariant(this.value ^ rightOp.getBoolean());
    case 2:
    case 3:
    case 4:
      return IntegerVariant.getIntegerVariant(getInteger() ^ rightOp.getInteger());
    case 5:
    case 8:
    }
    return LongVariant.getLongVariant(getInteger() ^ rightOp.getLong());
  }
}
