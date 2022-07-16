package com.ple.observabilityBridge;

import com.ple.util.IArrayMap;
import com.ple.util.IMap;
import com.ple.util.Immutable;

@Immutable
public class ObservabilityContext {

  public final static ObservabilityContext empty = new ObservabilityContext();
  public IMap<RecordingHandler, HandlerContext> map;

  private ObservabilityContext() { }

  HandlerContext get(RecordingHandler handler) {
    return map.get(handler);
  }

  ObservabilityContext put(RecordingHandler handler, HandlerContext handlerContext) {
    if (map == null) {
      map = IArrayMap.make(handler, handlerContext);
    } else {
      map = map.put(handler, handlerContext);
    }
    return this;
  }
}
