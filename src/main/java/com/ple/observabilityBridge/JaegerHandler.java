package com.ple.observabilityBridge;

import com.ple.util.IMap;
import io.prometheus.client.Counter;

public class JaegerHandler implements RecordingHandler {
  public static JaegerHandler only = new JaegerHandler();
  @Override
  public RecordingHandler open(RecordingService recordingService, String context, IMap<String, String> dimensions) {
    return null;
  }

  @Override
  public RecordingHandler close(RecordingService recordingService, String context, IMap<String, String> dimensions) {
    return null;
  }

  //Jaeger will record events to a jaeger server, since it is push based. Prometheus is pull based so it simply records events in memory.
  @Override
  public RecordingHandler log(RecordingService recordingService, int indentOffset, int importance, String base,
                              IMap<String, String> dimensions) {
    return null;
  }

  @Override
  public Counter get(String metricName) {
    return null;
  }
}
