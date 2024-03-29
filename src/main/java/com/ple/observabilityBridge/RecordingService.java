/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.ple.observabilityBridge;

import com.ple.util.*;
import io.opentelemetry.context.Context;

import java.util.Arrays;

/**
 * Should only be used by a single thread. If it is needed in another thread, pass a cloned instance.
 */
@Immutable
public class RecordingService {

  public static final RecordingService empty = make(IHashMap.empty);
  public final IMap<String, RecordingHandler> handlers;

  private RecordingService(IMap<String, RecordingHandler> handlers) {
    this.handlers = handlers;
  }

  public static RecordingService make(IMap<String, RecordingHandler> handlers) {
    return new RecordingService(handlers);
  }

  public static RecordingService make(String name, RecordingHandler handler) {
    return new RecordingService(IHashMap.make(name, handler));
  }

  public ObservabilityContext open(ObservabilityContext context, String group, IMap<String, String> dimensions) {
    for (RecordingHandler handler : handlers.values()) {
      final HandlerContext handlerContext = handler.open(context.get(handler), group, dimensions);
      context = context.put(handler, handlerContext);
    }
    return context;
  }

  public ObservabilityContext open(ObservabilityContext context, String group, String... dimensions) {
    return open(context, group, IArrayMap.make(Arrays.asList(dimensions)));
  }

  public ObservabilityContext open(ObservabilityContext context, String group, String name, IMap<String, String> dimensions) {
    for (RecordingHandler handler : handlers.values()) {
      handler.open(context, group, name, dimensions);
    }
    return open(context, group, IArrayMap.empty);
  }

  public ObservabilityContext close(ObservabilityContext context, String group, IMap<String, String> dimensions) {
    for (RecordingHandler handler : handlers.values()) {
      final HandlerContext handlerContext = handler.close(context.get(handler), group, dimensions);
      context = context.put(handler, handlerContext);
    }
    return context;
  }

  public ObservabilityContext close(ObservabilityContext context, String group, Object... dimensions) {
    return close(context, group, IArrayMap.make(dimensions));
  }

  public ObservabilityContext close(ObservabilityContext context, String group) {
    return close(context, group, IArrayMap.empty);
  }

  public ObservabilityContext log(ObservabilityContext context, String group, int importance,
                                  IMap<String, String> dimensions) {
    for (RecordingHandler handler : handlers.values()) {
      final HandlerContext handlerContext = handler.log(context.get(handler), group, dimensions, importance);
      context = context.put(handler, handlerContext);
    }
    return context;
  }

  public ObservabilityContext log(ObservabilityContext context, String group, int importance, String... dimensions) {
    return log(context, group, importance, IArrayMap.make(Arrays.asList(dimensions)));
  }

  public ObservabilityContext log(ObservabilityContext context, String group, String... dimensions) {
    return log(context, group, 0, IArrayMap.make(Arrays.asList(dimensions)));
  }

  public RecordingService clone() {
    return new RecordingService(handlers);
  }

}
