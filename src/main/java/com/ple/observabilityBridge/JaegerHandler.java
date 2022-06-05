package com.ple.observabilityBridge;

import com.ple.util.IMap;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.*;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.*;
import io.prometheus.client.Counter;

public class JaegerHandler implements RecordingHandler<JaegerContext> {
  public static JaegerHandler only = new JaegerHandler("http://localhost:4137");
  public final Tracer tracer;

  private JaegerHandler(String jaegerEndpoint) {
//    Resource serviceNameResource = Resource.create(Attributes.of(AttributeKey.stringKey("hello"), "otel-jaeger-example"));
//    Resource resource = Resource.builder().put("hello", "otel-jaeger-example").build();
    SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
        .addSpanProcessor(BatchSpanProcessor.builder(OtlpGrpcSpanExporter.builder()
                .setEndpoint(jaegerEndpoint)
            .build()).build())
        .build();

    OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
        .setTracerProvider(sdkTracerProvider)
        .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
        .buildAndRegisterGlobal();
    tracer = openTelemetry.getTracer("instrumentation-library-name", "1.0.0");
  }

  public static JaegerHandler make(String jaegerEndpoint) {
    return new JaegerHandler(jaegerEndpoint);
  }

  @Override
  public JaegerContext open(JaegerContext context, String group, IMap<String, String> dimensions) {
    Span child = tracer.spanBuilder("child span").setParent(context.get()).startSpan();
      // put the span into the current Context
    try {
      // do something...
    } catch (Throwable t){
      child.setStatus(StatusCode.ERROR, "Something bad happened!");
      child.recordException(t);
    } finally {
      child.end();
    }
    return null;
  }

  @Override
  public JaegerContext close(JaegerContext context, String group, IMap<String, String> dimensions) {
    return null;
  }

  //Jaeger will record events to a jaeger server, since it is push based. Prometheus is pull based so it simply records events in memory.
  @Override
  public JaegerContext log(JaegerContext context, String group, IMap<String, String> dimensions,
                              int importance) {
    return null;
  }

  @Override
  public Counter get(String metricName) {
    return null;
  }
}
