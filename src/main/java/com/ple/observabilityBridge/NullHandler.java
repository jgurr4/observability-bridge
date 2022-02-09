package com.ple.observabilityBridge;

public class NullHandler implements RecordingHandler {

  public static RecordingHandler only = new NullHandler();

}
