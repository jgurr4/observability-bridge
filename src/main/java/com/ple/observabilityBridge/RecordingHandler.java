package com.ple.observabilityBridge;

import com.ple.util.IMap;
import com.ple.util.Immutable;
import io.prometheus.client.Counter;

@Immutable
public interface RecordingHandler extends HandlerContext {

  HandlerContext open(ObservabilityContext context, String group, IMap<String, String> dimensions);

  HandlerContext close(ObservabilityContext context, String group, IMap<String, String> dimensions);

  RecordingHandler log(ObservabilityContext context, String group, IMap<String, String> dimensions, int importance);

  Counter get(String metricName);
}
