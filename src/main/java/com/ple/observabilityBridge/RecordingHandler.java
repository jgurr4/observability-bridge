package com.ple.observabilityBridge;

import com.ple.util.IMap;
import com.ple.util.Immutable;
import io.prometheus.client.Counter;

@Immutable
public interface RecordingHandler {

  RecordingHandler open(RecordingService recordingService, String context, IMap<String, String> dimensions);

  RecordingHandler close(RecordingService recordingService, String context, IMap<String, String> dimensions);

  RecordingHandler log(RecordingService recordingService, int indentOffset, int importance, String base, IMap<String, String> dimensions);

  Counter get(String metricName);
}
