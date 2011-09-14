package com.xiledsystems.AlternateJavaBridgelib.components.variants;

import java.util.Calendar;

import com.xiledsystems.AlternateJavaBridgelib.components.errors.ConversionError;

public abstract class Variant
{
  protected static final byte VARIANT_BOOLEAN = 1;
  protected static final byte VARIANT_BYTE = 2;
  protected static final byte VARIANT_SHORT = 3;
  protected static final byte VARIANT_INTEGER = 4;
  protected static final byte VARIANT_LONG = 5;
  protected static final byte VARIANT_SINGLE = 6;
  protected static final byte VARIANT_DOUBLE = 7;
  protected static final byte VARIANT_STRING = 8;
  protected static final byte VARIANT_OBJECT = 9;
  protected static final byte VARIANT_ARRAY = 10;
  protected static final byte VARIANT_DATE = 11;
  private final byte kind;

  protected Variant(byte kind)
  {
    this.kind = kind;
  }

  protected final byte getKind()
  {
    return this.kind;
  }

  public boolean getBoolean()
  {
    throw new ConversionError();
  }

  public byte getByte()
  {
    throw new ConversionError();
  }

  public short getShort()
  {
    throw new ConversionError();
  }

  public int getInteger()
  {
    throw new ConversionError();
  }

  public long getLong()
  {
    throw new ConversionError();
  }

  public float getSingle()
  {
    throw new ConversionError();
  }

  public double getDouble()
  {
    throw new ConversionError();
  }

  public String getString()
  {
    throw new ConversionError();
  }

  public Object getObject()
  {
    throw new ConversionError();
  }

  public Object getArray()
  {
    throw new ConversionError();
  }

  public Calendar getDate()
  {
    throw new ConversionError();
  }

  public Variant add(Variant rightOp)
  {
    throw new ConversionError();
  }

  public Variant sub(Variant rightOp)
  {
    throw new ConversionError();
  }

  public Variant mul(Variant rightOp)
  {
    throw new ConversionError();
  }

  public Variant div(Variant rightOp)
  {
    throw new ConversionError();
  }

  public Variant idiv(Variant rightOp)
  {
    throw new ConversionError();
  }

  public Variant mod(Variant rightOp)
  {
    throw new ConversionError();
  }

  public Variant pow(Variant rightOp)
  {
    throw new ConversionError();
  }

  public Variant neg()
  {
    throw new ConversionError();
  }

  public Variant shl(Variant rightOp)
  {
    throw new ConversionError();
  }

  public Variant shr(Variant rightOp)
  {
    throw new ConversionError();
  }

  public int cmp(Variant rightOp)
  {
    throw new ConversionError();
  }

  public boolean identical(Variant rightOp)
  {
    throw new ConversionError();
  }

  public boolean like(Variant rightOp)
  {
    throw new ConversionError();
  }

  public boolean typeof(Class<?> type)
  {
    throw new ConversionError();
  }

  public Variant not()
  {
    throw new ConversionError();
  }

  public Variant and(Variant rightOp)
  {
    throw new ConversionError();
  }

  public Variant or(Variant rightOp)
  {
    throw new ConversionError();
  }

  public Variant xor(Variant rightOp)
  {
    throw new ConversionError();
  }

  public Variant function(String name, Variant[] args)
  {
    throw new ConversionError();
  }

  public Variant dataMember(String name)
  {
    throw new ConversionError();
  }

  public void dataMember(String name, Variant variant)
  {
    throw new ConversionError();
  }

  public Variant array(Variant[] indices)
  {
    throw new ConversionError();
  }

  public void array(Variant[] indices, Variant variant)
  {
    throw new ConversionError();
  }
}
