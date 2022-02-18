package com.ple.observabilityBridge;

import com.ple.util.IMap;
import com.ple.util.Immutable;

@Immutable
public interface RecordingHandler {

  RecordingHandler open(RecordingService recordingService, String context, IMap<String, Object> dimensions);

  RecordingHandler close(RecordingService recordingService, String context, IMap<String, Object> dimensions);

  RecordingHandler log(RecordingService recordingService, int indentOffset, int importance, String base, IMap<String, Object> dimensions);

}
