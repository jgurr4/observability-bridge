package com.ple.observabilityBridge;

import com.ple.util.IMap;

public class SystemOutLogHandler implements RecordingHandler {

  public static SystemOutLogHandler only = new SystemOutLogHandler();

  @Override
  public void open(RecordingService recordingService, String context, IMap<String, String> lowCard, IMap<String, String> highCard) {
  }

}
