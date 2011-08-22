package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;


/**
 * Interface for components that need to do something when they are dynamically deleted (most
 * likely by the REPL)
 *
 */
public interface Deleteable {
  void onDelete();
}
