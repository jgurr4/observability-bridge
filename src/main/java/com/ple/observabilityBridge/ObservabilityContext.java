package com.ple.observabilityBridge;

import com.ple.util.IArrayMap;
import com.ple.util.IMap;
import com.ple.util.Immutable;
import io.opentelemetry.context.Context;

/**
 * This is a map of HandlerContexts. It lets us look up the context for the prometheusHandler or others.
 */
@Immutable
public class ObservabilityContext {

  public final static ObservabilityContext empty = new ObservabilityContext();
  public IMap<RecordingHandler, HandlerContext> map = IArrayMap.empty;

  private ObservabilityContext() { }

/*
  public static ObservabilityContext make() {
    return null;
  }
*/

  HandlerContext get(RecordingHandler handler) {
    return map.get(handler);
  }

  ObservabilityContext put(RecordingHandler handler, HandlerContext handlerContext) {
    map = map.put(handler, handlerContext);
    return this;
  }
}
