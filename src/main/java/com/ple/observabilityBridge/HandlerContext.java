package com.ple.observabilityBridge;

/**
 * AdapterContext is the parent for any custom AdapterContext classes, because there are methods that must be consistent
 * between all AdapterContext classes. The following are some examples of custom AdapterContext classes: JaegerContext,
 * PrometheusContext, SplunkContext.
 * Custom HandlerContext classes will wrap an ObservabilityContext object, and performs operations on it based on the needs of the custom
 * adapter itself. I have yet to confirm if things will have to be different between adapters for handling ObservabilityContext. But if so, then we'll keep this class. Otherwise we'll remove it.
 */
public interface HandlerContext {

}
