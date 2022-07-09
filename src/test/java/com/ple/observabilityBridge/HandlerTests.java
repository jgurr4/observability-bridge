package com.ple.observabilityBridge;

import com.ple.observabilityBridge.JaegerHandler;
import com.ple.observabilityBridge.PrometheusHandler;
import com.ple.observabilityBridge.RecordingService;
import com.ple.observabilityBridge.SystemOutLogHandler;
import io.prometheus.client.Counter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Assert.*;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.7.0'
//        testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.7.0'

public class HandlerTests {

  @Test
  public void sysOutTest() {
    SystemOutLogHandler sysOutHandler = new SystemOutLogHandler();
    RecordingService rs = RecordingService.make(sysOutHandler);
    ObservabilityContext context = rs.open(ObservabilityContext.empty, "sysOutTest");
    context = rs.log(context, "sysOutTest", "Some message");
    rs.close(context, "sysOutTest");
    Assert.assertEquals("Some message", context.get(sysOutHandler).handler.group);
  }

  @Test
  public void promLogTest() {
    final PrometheusHandler promHandler = PrometheusHandler.only;
    final RecordingService rs = RecordingService.make(promHandler);
    rs.log(ObservabilityContext.empty, "mysql", "connection", "3", "user", "Mordhau");
//    rs.log("mysql", "connection", "4", "user", "Benny");
    Assert.assertThrows(IllegalArgumentException.class,
        () -> Counter.build().name("mysql_connection_user_count").labelNames("connection", "user").help("1")
            .register());
    final Counter mysql_connection_user_count = rs.handlers.get(0).get("mysql_connection_user_count");
//    Assert.assertEquals(1, mysql_connection_user_count.get(), 0);   //FIXME: .get() fails because noLabelsChild is null.
    System.out.println(mysql_connection_user_count.collect().get(0).toString());
//    System.out.println(rs.handlers.get(0).get("mysql_connection_user_count"));   // For some reason prometheus client library is missing a toString() method.
  }

  @Test
  public void promScrapeTest() {

  }

  @Test
  public void jaegerOpenCloseTest() {
    final JaegerHandler jHandler = JaegerHandler.make("http://localhost:14520");
    final RecordingService rs = RecordingService.make(jHandler);
    ObservabilityContext context = rs.open(ObservabilityContext.empty, "root", jHandler);
    try {
      Thread.sleep(10);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    rs.log(context, "something happened");
    rs.close(context, "root");
    // TODO: Make a Http request here to jaeger docker instance.
    // https://jaeger-query:16686/api/traces/{trace-id-hex-string}
  }

  @Test
  public void jaegerOpenCloseTest2() {
    // This method actually tests running a nested open and close inside another open and close, simulating real tracing.
    final JaegerHandler jHandler = JaegerHandler.only;
    final RecordingService rs = RecordingService.make(jHandler);
    ObservabilityContext context = rs.open(ObservabilityContext.empty, "root");
    context = rs.open(context, "mysqlMethod", "mysql", "test");
    try {
      Thread.sleep(10);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    context = rs.log(context, "mysqlMethod", "mysql", "test");
    context = rs.close(context, "mysqlMethod");
    rs.close(context, "root");
  }

  @Test
  public void jaegerLogTest() {

  }

}
