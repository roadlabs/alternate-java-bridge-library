package com.xiledsystems.AlternateJavaBridgelib.components.variants;

import com.xiledsystems.AlternateJavaBridgelib.components.helpers.ConvHelpers;
import com.xiledsystems.AlternateJavaBridgelib.components.helpers.ExprHelpers;

public final class LongVariant extends Variant
{
  private long value;

  public static final LongVariant getLongVariant(long value)
  {
    return new LongVariant(value);
  }

  private LongVariant(long value)
  {
    super(VARIANT_LONG);
    this.value = value;
  }

  public boolean getBoolean()
  {
    return ConvHelpers.long2boolean(this.value);
  }

  public byte getByte()
  {
    return ConvHelpers.long2byte(this.value);
  }

  public short getShort()
  {
    return ConvHelpers.long2short(this.value);
  }

  public int getInteger()
  {
    return (int)this.value;
  }

  public long getLong()
  {
    return this.value;
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
    return Long.toString(this.value);
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
    }
    return getLongVariant(getLong() + rightOp.getLong());
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
      return getLongVariant(getLong() - rightOp.getLong());
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
    case 5:
    }
    return getLongVariant(getLong() * rightOp.getLong());
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
      return getLongVariant(getLong() / rightOp.getLong());
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
    case 5:
      return getLongVariant(getLong() % rightOp.getLong());
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
    return getLongVariant(-this.value);
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
    case 5:
    case 8:
    }
    return getLongVariant(getLong() << (int)rightOp.getLong());
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
    case 5:
    case 8:
    }
    return getLongVariant(getLong() >> (int)rightOp.getLong());
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
    }
    long rvalue = rightOp.getLong();
    if (this.value == rvalue) {
      return 0;
    }
    return this.value > rvalue ? 1 : -1;
  }

  public Variant not()
  {
    return getLongVariant(this.value ^ 0xFFFFFFFF);
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
    case 5:
    case 8:
    }
    return getLongVariant(getLong() & rightOp.getLong());
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
    case 5:
    case 8:
    }
    return getLongVariant(getLong() | rightOp.getLong());
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
    case 5:
    case 8:
    }
    return getLongVariant(getLong() ^ rightOp.getLong());
  }
}
