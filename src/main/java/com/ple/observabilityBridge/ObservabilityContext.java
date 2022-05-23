package com.ple.observabilityBridge;

import com.ple.util.Immutable;

@Immutable
public class ObservabilityContext {

  public final static ObservabilityContext empty = new ObservabilityContext();

  private ObservabilityContext() { }

  ObservabilityContext get(RecordingHandler handler);

  ObservabilityContext put(RecordingHandler handler, HandlerContext handlerContext);
}
