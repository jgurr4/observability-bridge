package com.ple.observabilityBridge;

import io.opentelemetry.context.Context;

//TODO: Mirror this after ArrayBasedContext except instead of normal maps, make it Immutable Maps.
public class JaegerContext implements HandlerContext {
  public final Context context;

  public JaegerContext(Context context) {
    this.context = context;
  }

  public Context get() {
    return context;
  }
}
