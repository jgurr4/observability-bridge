package com.ple.observabilityBridge;

import com.ple.util.IMap;

/**
 * This is not thread-safe on purpose. But it's state should rarely change anyway so it shouldn't be an issue in practice.
 * Wrap with sync methods if needed.
 */
public class SystemOutLogHandler implements RecordingHandler {

  public static SystemOutLogHandler only = new SystemOutLogHandler();
  public boolean indent = true;

  @Override
  public RecordingHandler open(RecordingService recordingService, String context, IMap<String, Object> dimensions) {
    return log(recordingService, -1, 0, "Opening: " + context, dimensions);
  }

  @Override
  public RecordingHandler close(RecordingService recordingService, String context, IMap<String, Object> dimensions) {
    Long startTime = recordingService.startTimeList.getLast();
    long duration = System.currentTimeMillis() - startTime;
    dimensions = dimensions.put("duration_ms", duration);
    return log(recordingService, -1, 0, "Closing: " + context, dimensions);
  }

  @Override
  public RecordingHandler log(RecordingService recordingService, int indentOffset, int importance, String base, IMap<String, Object> dimensions) {

    int baseLevel = recordingService.contextList.size();
    int computedLevel = baseLevel - importance;

    if (computedLevel < recordingService.verbosity) {

      String message = base + " " + dimensions.toKVString();

      if (indent) {
        message = " ".repeat(baseLevel + indentOffset) + message;
      }

      if (computedLevel < 0) {
        System.err.println(message);
      } else {
        System.out.println(message);
      }

    }

    return this;

  }

}
