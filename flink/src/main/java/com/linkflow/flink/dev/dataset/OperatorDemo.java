package com.linkflow.flink.dev.dataset;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class OperatorDemo {
    public static void main(String[] args) throws Exception {
//        Configuration config = new Configuration();
//        config.setString("fs.s3a.endpoint", "xxx:9000");
//        config.setString("fs.s3a.access.key", "xxx");
//        config.setString("fs.s3a.secret.key", "xxx");
//        config.setString("fs.s3a.path.style.access", "true");
//        config.setString("fs.s3a.connection.ssl.enabled", "false");
//        ExecutionEnvironment executionEnvironment = ExecutionEnvironment.createLocalEnvironment(config);
//        DataSet<String> ds = executionEnvironment.readTextFile("s3a://oss/contact_ids.csv");


        ExecutionEnvironment executionEnvironment = ExecutionEnvironment.getExecutionEnvironment();
//        DataSet<Tuple2<String,Integer>> data = executionEnvironment.fromElements(Tuple2.of("张三",1),Tuple2.of("李四",2),Tuple2.of("王五",3),Tuple2.of("张三",4));
//        data.groupBy(0).sum(1).print();

        //DSet版的wordcount
        String path = OperatorDemo.class.getClassLoader().getResource("words.txt").getPath();
        System.out.println("path = " + path);
        DataSet<String> ds = executionEnvironment.readTextFile(path);
        ds.flatMap(new FlatMapFunction<String, Tuple2<String,Integer>>() {
            @Override
            public void flatMap(String value, Collector<Tuple2<String,Integer>> out) throws Exception {
                String[] split = value.split(" ");
                for (String s : split) {
                    out.collect(new Tuple2(s,1));
                }
//                out.collect(Tuple2.of(split[0],1));
            }
        }).groupBy(0).sum(1).print();
    }
}
