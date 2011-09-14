package com.xiledsystems.AlternateJavaBridgelib.components.helpers;

public final class ExprHelpers
{
  public static float idiv(float leftOp, float rightOp)
  {
    return Math.round(leftOp / rightOp);
  }

  public static double idiv(double leftOp, double rightOp)
  {
    return Math.round(leftOp / rightOp);
  }

  public static boolean like(String string, String pattern)
  {
    throw new UnsupportedOperationException();
  }

  public static int pow(int leftOp, int rightOp)
  {
    return (int)Math.pow(leftOp, rightOp);
  }

  public static float pow(float leftOp, float rightOp)
  {
    return (float)Math.pow(leftOp, rightOp);
  }
}
