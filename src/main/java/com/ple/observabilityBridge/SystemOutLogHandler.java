package com.ple.observabilityBridge;

import com.ple.util.IMap;
import io.prometheus.client.Counter;

/**
 * This is not thread-safe on purpose. But it's state should rarely change anyway so it shouldn't be an issue in practice.
 * Wrap with sync methods if needed.
 */
public class SystemOutLogHandler implements RecordingHandler {

  public static SystemOutLogHandler only = new SystemOutLogHandler();
  public boolean indent = true;

  @Override
  public HandlerContext open(ObservabilityContext context, String group, IMap<String, String> dimensions) {
    return log(context, -1, dimensions, 0);
  }

  @Override
  public HandlerContext close(ObservabilityContext context, String group, IMap<String, String> dimensions) {
    Long startTime = context.startTimeList.getLast();
    long duration = System.currentTimeMillis() - startTime;
    dimensions = dimensions.put("duration_ms", Long.toString(duration));
    return log(context, -1, dimensions, 0);
  }

  @Override
  public RecordingHandler log(ObservabilityContext context, String group, IMap<String, String> dimensions,
                              int importance) {

    int baseLevel = context.contextMap.size();
    int computedLevel = baseLevel - importance;

    if (computedLevel < context.verbosity) {

      String message = base + " " + dimensions.toKVString();

      if (indent) {
        message = " ".repeat(baseLevel + group) + message;
      }

      if (computedLevel < 0) {
        System.err.println(message);
      } else {
        System.out.println(message);
      }

    }

    return this;

  }

  @Override
  public Counter get(String metricName) {
    return null;
  }

}
