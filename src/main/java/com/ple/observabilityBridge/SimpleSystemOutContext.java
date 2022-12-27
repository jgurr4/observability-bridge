package com.ple.observabilityBridge;

/**
 * The purpose of this is to ensure it is not meant to do anything. But we have to have context in order for
 * ObservabilityContext map to work. So to save memory, we just have this one, and we don't
 * need more than one created at a time.
 */
public class SimpleSystemOutContext {
    static final SimpleSystemOutContext only = new SimpleSystemOutContext();
    private SimpleSystemOutContext(){};
}
