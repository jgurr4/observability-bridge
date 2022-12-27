package com.ple.observabilityBridge;

import com.ple.util.IMap;
import com.ple.util.Immutable;
import io.prometheus.client.Counter;

/**
 * RecordingHandler is the parent of all custom AdapterHandler classes which are called by RecordingService whenever
 * a method is used to open, log or close monitoring for each adapter in the RecordingService.
 * @param <T>
 */
@Immutable
public interface RecordingHandler<T extends HandlerContext> {

  T open(T context, String group, String name, IMap<String, String> dimensions);

  T close(T context, String group, IMap<String, String> dimensions);

  T log(T context, String group, IMap<String, String> dimensions, int importance);

  Counter get(String metricName);
}
