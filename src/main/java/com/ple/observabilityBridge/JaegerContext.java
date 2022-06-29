package com.ple.observabilityBridge;

import io.opentelemetry.context.Context;

//TODO: Mirror this after ArrayBasedContext except instead of normal maps, make it Immutable Maps.
public class JaegerContext implements HandlerContext {
  public final Context context = new JaegerContext();

  public JaegerContext(Context context) {
    this.context = context;
  }

  public static HandlerContext make() {
    return new JaegerContext();
  }

  public Context get() {
    return context;
  }
}
