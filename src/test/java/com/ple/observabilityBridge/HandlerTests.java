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
  public void promLogTest() {
    final PrometheusHandler promHandler = PrometheusHandler.only;
    final RecordingService rs = RecordingService.make(promHandler);
    rs.log("mysql", "connection", "3", "user", "Mordhau");
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
    final JaegerHandler jHandler = JaegerHandler.only;
    final RecordingService rs = RecordingService.make(jHandler);
    rs.open("root");
    someMethod(rs);
//    rs.log()
    rs.close("root");
  }

  private void someMethod(RecordingService rs) {
    rs.open("someMethod", "verticleName", "blah");
    try {
      Thread.sleep(100);
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
    }
    rs.close("someMethod", "response code", "200");
  }

  @Test
  public void jaegerLogTest() {

  }

}
