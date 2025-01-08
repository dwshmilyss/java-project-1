package com.linkflow.flink.dev;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.configuration.Configuration;


public class FlinkStorageDemo {
    public static void main(String[] args) throws Exception {
        Configuration config = new Configuration();

        config.setString("fs.s3a.endpoint", "172.17.0.5:9000");
        config.setString("fs.s3a.access.key", "linkflow");
        config.setString("fs.s3a.secret.key", "Sjtu403c@#%");
        config.setString("fs.s3a.path.style.access", "true");
        config.setString("fs.s3a.connection.ssl.enabled", "false");
        config.setString("fs.s3a.buffer.dir", "file:///Users/edz/tmp/s3a");

//        config.set("fs.s3a.endpoint", "172.17.0.5:9000");
//        config.set("fs.s3a.access.key", "linkflow");
//        config.set("fs.s3a.secret.key", "Sjtu403c@#%");
//        config.set("fs.s3a.path.style.access", "true");
//        config.set("fs.s3a.connection.ssl.enabled", "false");
//        config.set("fs.s3a.buffer.dir", "file:///Users/edz/tmp/s3a");

//        config.setString("s3.endpoint", "172.17.0.5:9000");
//        config.setString("s3.access-key", "linkflow");
//        config.setString("s3.secret-key", "Sjtu403c@#%");
//        config.setString("s3.path.style.access", "true");
//        config.setString("s3.connection.ssl.enabled", "false");

//        ExecutionEnvironment executionEnvironment = ExecutionEnvironment.createLocalEnvironment(config);
//            ExecutionEnvironment executionEnvironment = ExecutionEnvironment.createRemoteEnvironment("172.17.0.5", 6123, "/Users/edz/sourceCode/flink_demo/target/flink_demo-1.0-SNAPSHOT.jar");
        ExecutionEnvironment executionEnvironment = ExecutionEnvironment.getExecutionEnvironment();
//        System.out.println(executionEnvironment.getConfiguration().containsKey("fs.s3a.endpoint"));
//        DataSet<Integer> words = executionEnvironment.fromElements(1, 2, 3);
//        DataSet<Integer> res = words.map(new MapFunction<Integer, Integer>() {
//            @Override
//            public Integer map(Integer value) throws Exception {
//                return value*2;
//            }
//        });
//        final FileSink<String> sink = FileSink.forRowFormat(new Path(""), new SimpleStringEncoder<String>("UTF-8")).build();
        org.apache.hadoop.conf.Configuration hadoopconf = new org.apache.hadoop.conf.Configuration();
        System.out.println(hadoopconf.get("fs.s3a.access.key"));
        System.out.println(hadoopconf.get("fs.s3a.secret.key"));
        System.out.println(hadoopconf.get("fs.s3a.endpoint"));
        System.out.println(hadoopconf.get("fs.s3a.path.style.access"));
        System.out.println(hadoopconf.get("fs.s3a.connection.ssl.enabled"));
        System.out.println(hadoopconf.get("fs.s3a.impl"));
//        DataSet<String> ds = executionEnvironment.readTextFile("s3a://oss/contact_ids.csv").withParameters(config);
//        DataSet<String> ds = executionEnvironment.readTextFile("s3a://oss/contact_ids.csv");
        DataSet<String> ds = executionEnvironment.readTextFile("s3a://image-leadswarp/1/dict/contact/521.csv");
//        DataSet<String> ds = executionEnvironment.readTextFile("s3a://oss/899.csv");
//        DataSet<String> ds = executionEnvironment.readTextFile("s3a://oss/1/dict/contact/161300293.csv");

//        DataSet<String> ds = executionEnvironment.readTextFile("hdfs://hdp1-test.leadswarp.com:8020/test.csv");

//        FileInputFormat fileInputFormat = new TextInputFormat(new Path("hdfs://hdp1-test.leadswarp.com:8020/test.csv"));
//        fileInputFormat.setNestedFileEnumeration(true);
//        DataSet<String> ds = executionEnvironment.readFile(fileInputFormat,"hdfs://hdp1-test.leadswarp.com:8020/test.csv");
        ds.print();
//        executionEnvironment.execute();
    }
}
