package com.ple.observabilityBridge;

import com.ple.util.IEntry;
import com.ple.util.IList;
import com.ple.util.IMap;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.SimpleCollector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PrometheusHandler implements RecordingHandler {
  public static PrometheusHandler only = new PrometheusHandler();
  private static Map<String, Counter> counters = new HashMap<>();
  private static Map<String, Gauge> gauges = new HashMap<>();
  private static Map<String, Histogram> histograms = new HashMap<>();

  // Open and close are used for histograms to record latency for buckets. They may also be used to generate generic category for the metrics inside.
  @Override
  public RecordingHandler open(RecordingService recordingService, String context, IMap<String, String> dimensions) {
    return null;
  }

  // Eventually you'll want to record timing for how long things take using these for metrics. But that is not necessary at first.
  // It seems like histograms can make use of Open/Close because they are used to observe latency issues.
  @Override
  public RecordingHandler close(RecordingService recordingService, String context, IMap<String, String> dimensions) {
    return null;
  }

  //Log doesn't just mean log. It means recording an event. In prometheus it will write the metric to a in-memory counter
  // or gauge or histograms. Which prometheus will scrape periodically. In this case I'll need a prometheus client library.
  // low cardinality dimensions can exceed a certain threshold like 30 and it becaomes a high cardinality dimension.
  // High cardinality dimensions you don't want to make a label for.  Instead you aggregate it, and don't log it unless you aggregate them and log 10 or 20 of them.
  //TODO: figure out difference between .create() and .register().
  // Histograms are more complicated, so focus on just getting counters for now.
  // Gauges are simply determined like so: Imagine you have mysql_pool metric, it can go up and down. But the way it's logged is like this:
  // A user defines a metric called "pool_connection_requests" and another metric called "pool_connection_returns" you subtract the returns from the requests, and that is your gauge.
  // You'll also have to automatically check base and dimensions to make sure any spaces " " get replaced with "_" and all should be lower cased.
  // Buckets can be defined by the user when they create the handler. But if it is not defined it should be automatically figured out inside here.
  // based on the types of data flowing through here. Also labels would be nice to auto-define as well.
  // The philosophy of observability bridge is require the least amount of work from the users as possible. This means
  // using algorithms to determine what the user wants whenever the user doesn't define something. Not everything can be done this way. But some things can and should like labels, metric names,
  // buckets, and gauges. A user should be able to use log() somewhere and not have to explain what it is whether counter, gauges or histogram,
  // but still have the option to define things if they want more control over it. When a user creates the handler they can choose to define buckets.
  // dimension keys go in labelNames() for counter creation. Dimension values go in labels() for incrementing. base is used to define the counter key and the metric name(). help() is always empty
  // Don't bother trying to use exporting or service discovery for prometheus yet. Only worry about that once everything is finished and you are ready to create prometheus service in docker.
  // recordingservice
  // We need labels from (dimensions) as well as generic tag from (Open) and (close) tag, and finally we have a metric name which is determined by the labels.
  // If labels are the same it is the same metric.
  @Override
  public RecordingHandler log(RecordingService recordingService, int indentOffset, int importance, String base,
                              IMap<String, String> dimensions) {
/*
    SimpleCollector<?> metricType = determineMetricType(base, dimensions, importance);
    String metricName = makeMetricFromBase(base, metricType);
    if (metricType instanceof Counter) {
      final Counter counter;
      if (!counters.containsKey(metricName)) {
        counter = Counter.build().name(metricName).labelNames(dimensions.keys().toArray()).register();
        counters.put(metricName, counter);
      } else {
        counter = counters.get(metricName);
      }
      counter.labels(dimensions.values().toArray()).inc();
    } else if (metricType instanceof Gauge) {
      final Gauge gauge;
      if (!gauges.containsKey(metricName)) {
        gauge = Gauge.build().name(base).labelNames(dimensions.keys().toArray()).register();
        gauges.put(metricName, gauge);
      } else {
        gauge = gauges.get(metricName);
      }
      updateGauge(gauge, dimensions.values().toArray(), metricName);
    } else if (metricType instanceof Histogram){
      histograms.put(metricName, Histogram.build().name(metricName).labelNames(dimensions.keys().toArray()).register());
    }
*/
    String metricName = makeMetricFromBase(base, dimensions);
    final Counter counter;
    if (!counters.containsKey(metricName)) {
      counter = Counter.build().name(metricName).labelNames(dimensions.keys().toArray(new String[0])).help("1").register();
      counters.put(metricName, counter);
    } else {
      counter = counters.get(metricName);
    }
    counter.labels(dimensions.values().toArray(new String[0])).inc();
    exposeMetric();
    return this;
  }

  public void exposeMetric() {
    try {
      HTTPServer server = new HTTPServer.Builder()
          .withPort(8080)
          .build();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Counter get(String metricName) {
    return counters.get(metricName);
  }

  private String makeMetricFromBase(String base, IMap<String, String> dimensions) {
    // Here I will design an algorithm which will remove all spaces from the base metric name, and replace them with underscores (_).
    // This will also change the name of the metric so it fits into the rules of our names above. For counters it must either end with
    // _request_count  _return_count
    // For gauges it must end in _total
    // And look up more name rules to use here as well. https://prometheus.io/docs/practices/naming/
    // For now I won't do anything except replace " " with "_"
    String metricName = base;
    for (IEntry<String, String> dimension : dimensions) {
      metricName += "_" + dimension.key;
    }
/*
    base = base.replace(" ", "_").toLowerCase(Locale.ROOT);
    if (metricType instanceof Counter) {
      metricName = base + "_count";   //FIXME: I don't know how to make sure _request or _return are determined correctly here without requiring users to specify it explicitely.
    } else if (metricType instanceof Gauge) {
      metricName = base + "_total";
    } else if (metricType instanceof Histogram) {
      metricName = base + "";  //FIXME: I don't know what to put here yet.
    }
     return metricName;
*/
    return metricName.replace(" ", "_").toLowerCase(Locale.ROOT) + "_count";
  }

  private void updateGauge(Gauge gauge, String[] dimensionValues, String metricName) {
    // Here I will design an algorithm which looks into the counters list and finds any that closely match the metric name but oppose it.
    // For example: if mysql_pool_connections_request_count is the metricName, then we search for one called mysql_pool_connections_return_count.
    // We subtract return_count from request_count and that gets us the gauge called mysql_pool_connections_total
    final double counter1;
    final double counter2;
    if (metricName.contains("_request_count")) {
      counter1 = counters.get(metricName).get();
    } else {
      counter1 = 0;
    }
    if (metricName.contains("_return_count")) {
      counter2 = counters.get(metricName).get();
    } else {
      counter2 = 0;
    }
    double total = counter1 - counter2 > 0 ? counter1 - counter2 : 0;
    gauge.labels(dimensionValues).set(total);
  }

  private SimpleCollector<?> determineMetricType(String base, IMap<String, String> dimensions, int importance) {
    //Here I will design my algorithm for determining if the event relates to a counter, gauge or histogram.
    return null;
  }

}
