package com.ple.observabilityBridge;

import com.ple.util.IMap;
import com.ple.util.Immutable;

@Immutable
public interface RecordingHandler {

  void open(RecordingService recordingService, String context, IMap<String, String> lowCard, IMap<String, String> highCard);

}
