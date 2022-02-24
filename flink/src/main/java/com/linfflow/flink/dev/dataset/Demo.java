package com.linfflow.flink.dev.dataset;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.ConfigurationUtils;

public class Demo {
    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();
        config.setString("fs.s3a.endpoint", "xxx:9000");
        config.setString("fs.s3a.access.key", "xxx");
        config.setString("fs.s3a.secret.key", "xxx");
        config.setString("fs.s3a.path.style.access", "true");
        config.setString("fs.s3a.connection.ssl.enabled", "false");
        ExecutionEnvironment executionEnvironment = ExecutionEnvironment.createLocalEnvironment(config);
//        ExecutionEnvironment executionEnvironment = ExecutionEnvironment.getExecutionEnvironment();
//        DataSet<Integer> words = executionEnvironment.fromElements(1, 2, 3);
//        DataSet<Integer> res = words.map(new MapFunction<Integer, Integer>() {
//            @Override
//            public Integer map(Integer value) throws Exception {
//                return value*2;
//            }
//        });
//        final FileSink<String> sink = FileSink.forRowFormat(new Path(""), new SimpleStringEncoder<String>("UTF-8")).build();

        DataSet<String> ds = executionEnvironment.readTextFile("s3a://oss/contact_ids.csv");
        ds.print();
        executionEnvironment.execute();
    }
}
