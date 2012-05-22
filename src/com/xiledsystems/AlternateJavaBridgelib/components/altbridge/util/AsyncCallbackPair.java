package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

/**
 * Interface for callback pair with onSuccess and onFalure
 * The failure callback returns a string
 */

  public interface AsyncCallbackPair<T> {
  /**
   * Create a pair of callbacks,  for success and failure,
   * used tyically in an asynchronous operation.
   */

    /**
     * Called when an asynchronous call fails to complete normally
     *
     * @param message a message to be consumed by the procedure that
     * set up the callback pair
     */
    void onFailure(String message);

    /**
     * Called when an asynchronous call completes successfully.
     *
     * @param result the return value of asynchronous operation
     */
    void onSuccess(T result);
  }
