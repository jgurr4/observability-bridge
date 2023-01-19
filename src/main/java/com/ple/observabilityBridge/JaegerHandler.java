package com.ple.observabilityBridge;

import com.ple.util.IMap;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.prometheus.client.Counter;

public class JaegerHandler implements RecordingHandler<JaegerContext> {
  public static JaegerHandler only = make("http://localhost:4137");
  public final Tracer tracer;
//  public final Tracer globalTracer;

  /**
   *
   * @param jaegerHttpEndpoint
   */
  private JaegerHandler(String jaegerHttpEndpoint, Tracer tracer) {
//    this.globalTracer = globalTracer;
    this.tracer = tracer;
  }

  public static JaegerHandler make(String jaegerHttpEndpoint) {
//    Resource serviceNameResource = Resource.create(Attributes.of(AttributeKey.stringKey("hello"), "otel-jaeger-example"));
//    Resource resource = Resource.builder().put("hello", "otel-jaeger-example").build();
    SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(OtlpGrpcSpanExporter.builder()
                    .setEndpoint(jaegerHttpEndpoint)
                    .build()).build())
            .build();

    // Jaeger cannot handle multi-plexing (reactive programming is a type of multi-plexing) multiplexing is just single
    // thread doing multiple tasks at the same time. We need it to handle multi-plexing for it to work with vertx. Because
    // vertx will do lots with one single thread and also do multiple threads at the same time.
    OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
            .setTracerProvider(sdkTracerProvider)
//        .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
            .build();

    //TODO: Figure out what is the use of global.
/*
    OpenTelemetry openTelemetryGlobal = null;
    if (globalTracer == null) {
      openTelemetryGlobal = OpenTelemetrySdk.builder()
              .setTracerProvider(sdkTracerProvider)
//        .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
              .buildAndRegisterGlobal();
    }
*/

    final Tracer tracer = openTelemetry.getTracer("instrumentation-library-name", "1.0.0");
//    final Tracer globalTracer = openTelemetryGlobal.getTracer("instrumentation-library-name", "1.0.0");
    return new JaegerHandler(jaegerHttpEndpoint, tracer);
  }

  @Override
  public JaegerContext open(JaegerContext context, String group, String name, IMap<String, String> dimensions) {
    Span span = this.tracer.spanBuilder("Start my wonderful use case").setParent(context.get())
        .startSpan();
    span.addEvent("Event 0");
    span.addEvent("Event 1");
    span.end();
/*
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
*/
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
