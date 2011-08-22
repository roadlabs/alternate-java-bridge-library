package com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util;

import gnu.mapping.Environment;
import gnu.mapping.InPort;
import gnu.mapping.OutPort;
import gnu.mapping.Procedure;
import gnu.mapping.RunnableClosure;

/**
 * A version of the {@link gnu.mapping.Future} class that can run with a larger stack size
 */
public class BiggerFuture extends Thread {
  public BiggerFuture(Procedure action, Environment penvironment,
                      InPort in, OutPort out, OutPort err, String threadName, long stackSize) {
    super(new ThreadGroup("biggerthreads"),
          new RunnableClosure (action, penvironment, in, out, err),
          threadName, stackSize);
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append ("#<future ");
    buf.append(getName());
    buf.append(">");
    return buf.toString();
  }
}
