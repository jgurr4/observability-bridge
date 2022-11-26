package com.ple.observabilityBridge;

import io.opentelemetry.context.Context;

//TODO: Mirror this after ArrayBasedContext except instead of normal maps, make it Immutable Maps.
public class JaegerContext implements HandlerContext {
  public final Context context;

  public JaegerContext(Context context) {
    this.context = context;
  }

  public static HandlerContext make(Context context) {
    return new JaegerContext(context);
  }

  public Context get() {
    return context;
  }

  public Context getRoot() {
    return Context.root();
  }

  public Context getCurrent() {
    return Context.current();
  }
}
