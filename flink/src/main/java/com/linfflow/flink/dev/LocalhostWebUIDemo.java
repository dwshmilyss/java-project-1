package com.linfflow.flink.dev;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class LocalhostWebUIDemo {
    public static void main(String[] args) throws InterruptedException {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        Thread.sleep(60*1000);
    }
}
