package com.ple.observabilityBridge;

import com.ple.util.IMap;
import com.ple.util.Immutable;
import io.prometheus.client.Counter;

@Immutable
public class SimpleSystemOutHandler implements RecordingHandler<SimpleSystemOutContext> {
    public static SimpleSystemOutHandler only = new SimpleSystemOutHandler();

    @Override
    public SimpleSystemOutContext open(SimpleSystemOutContext context, String group, String name, IMap<String, String> dimensions) {
        return null;
    }

    @Override
    public SimpleSystemOutContext close(SimpleSystemOutContext context, String group, IMap<String, String> dimensions) {
        return null;
    }

    @Override
    public SimpleSystemOutContext log(SimpleSystemOutContext context, String group, IMap<String, String> dimensions, int importance) {
        System.out.println(group + " " + dimensions.toKVString());
        return null;
    }

    @Override
    public Counter get(String metricName) {
        return null;
    }
}
