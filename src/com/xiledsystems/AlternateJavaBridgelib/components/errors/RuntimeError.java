package com.xiledsystems.AlternateJavaBridgelib.components.errors;

public abstract class RuntimeError extends RuntimeException
{
  protected RuntimeError()
  {
  }

  protected RuntimeError(String message)
  {
    super(message);
  }

  public static RuntimeError convertToRuntimeError(Throwable throwable)
  {
    if ((throwable instanceof RuntimeError)) {
      return (RuntimeError)throwable;
    }

    if ((throwable instanceof ArrayIndexOutOfBoundsException)) {
      return new ArrayIndexOutOfBoundsError();
    }
    if ((throwable instanceof IllegalArgumentException)) {
      return new IllegalArgumentError();
    }
    if ((throwable instanceof NullPointerException)) {
      return new UninitializedInstanceError();
    }

    throw new UnsupportedOperationException(throwable);
  }
}
