package com.ple.observabilityBridge;

import com.ple.util.IMap;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.prometheus.client.Counter;

public class JaegerHandler implements RecordingHandler {
  public static JaegerHandler only = new JaegerHandler();
  public final Tracer tracer;

  public JaegerHandler() {
    SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
        .addSpanProcessor(BatchSpanProcessor.builder(OtlpGrpcSpanExporter.builder().build()).build())
        .build();

    OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
        .setTracerProvider(sdkTracerProvider)
        .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
        .buildAndRegisterGlobal();
    tracer = openTelemetry.getTracer("instrumentation-library-name", "1.0.0");
  }

  @Override
  public HandlerContext open(ObservabilityContext context, String group, IMap<String, String> dimensions) {
    Span parentSpan = tracer.spanBuilder("").startSpan();
    Span child = tracer.spanBuilder("child span").setParent(Context.current().with(parentSpan)).startSpan();
// put the span into the current Context
    try {
      // do something...
    } finally {
      child.end();
    }
    return null;
  }

  @Override
  public HandlerContext close(ObservabilityContext context, String group, IMap<String, String> dimensions) {
    return null;
  }

  //Jaeger will record events to a jaeger server, since it is push based. Prometheus is pull based so it simply records events in memory.
  @Override
  public RecordingHandler log(ObservabilityContext context, String group, IMap<String, String> dimensions,
                              int importance) {
    return this;
  }

  @Override
  public Counter get(String metricName) {
    return null;
  }
}
