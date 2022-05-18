/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.ple.observabilityBridge;

import com.ple.util.IArrayMap;
import com.ple.util.IMap;

import java.util.*;

/**
 * Should only be used by a single thread. If it is needed in another thread, pass a cloned instance.
 */
public class RecordingService {

  public static final RecordingService empty = make();

  public static RecordingService make(RecordingHandler... handlers) {
    return new RecordingService(List.of(handlers), new HashMap<>(), new ArrayDeque<>(), 0);
  }

  public final List<RecordingHandler> handlers;
  public final Map<RecordingHandler, Object> contextList;
  public final Deque<Long> startTimeList;
  public int verbosity = 0;

  private RecordingService(List<RecordingHandler> handlers,
                           Map<RecordingHandler, Object> contextList,
                           Deque<Long> startTimeList, int verbosity) {
    this.handlers = handlers;
    this.contextList = contextList;
    this.startTimeList = startTimeList;
    this.verbosity = verbosity;
  }

  public RecordingService open(String context, IMap<String, String> dimensions) {
    startTimeList.add(System.currentTimeMillis());
    // The context can become a span for jaeger, it can be a normal string for LogOutHandler, and it can be a
    for (RecordingHandler handler : handlers) {
      contextList.put(context, )
      handler.open(this, context, dimensions);
    }

    return this;
  }

  public RecordingService open(String context, Object... dimensions) {
    return open(context, IArrayMap.make(dimensions));
  }

  public RecordingService open(String context) {
    return open(context, IArrayMap.empty);
  }

  public RecordingService close(String context, IMap<String, String> dimensions) {

    for (RecordingHandler handler : handlers) {
      handler.close(this, context, dimensions);
    }

    if (!Objects.equals(contextList.getLast(), context)) {
      System.err.println("Warning: logging close mismatch, expected [" + contextList.getLast() + "] but got [" + context + "]");
    }

    startTimeList.removeLast();
    contextList.removeLast();

    return this;

  }

  public RecordingService close(String context, Object... dimensions) {
    return close(context, IArrayMap.make(dimensions));
  }

  public RecordingService close(String context) {
    return close(context, IArrayMap.empty);
  }

  public RecordingService clone() {
    return new RecordingService(new ArrayList(handlers), new ArrayDeque<>(contextList), new ArrayDeque<>(startTimeList), verbosity);
  }

  public RecordingService log(int importance, String base, IMap<String, String> dimensions) {
    for (RecordingHandler handler : handlers) {
      handler.log(this, 0, importance, base, dimensions);
    }
    return this;
  }

  public RecordingService log(int importance, String base, String... dimensions) {
    return log(importance, base, IArrayMap.make(dimensions));
  }

  public RecordingService log(String base, String... dimensions) {
    return log(0, base, IArrayMap.make(dimensions));
  }

}
