package com.ple.observabilityBridge;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;

public class JaegerContext implements HandlerContext {
  public Context get() {
    return null;
  }
}
