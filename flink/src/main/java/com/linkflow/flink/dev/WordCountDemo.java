package com.linkflow.flink.dev;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Arrays;
import java.util.List;

public class WordCountDemo {
    public static void main(String[] args) throws Exception {
        System.setProperty("HADOOP_USER_NAME", "hdfs");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //开启checkpoint，5s是两次checkpoint的间隔时间 即下一次checkpoint会在上一次checkpoint结束5s后开始。默认把中间结果保存于JobMananger的内存
        env.enableCheckpointing(5000);
        //重启3次 每次间隔2s
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 2000));
        //程序异常退出，或者人为取消，不删除checkpoint目录数据
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
//        env.setStateBackend(new FsStateBackend("hdfs://hadoop-hdfs-namenode:9000/dww/flink/checkpoints"));
        env.setStateBackend(new FsStateBackend("hdfs://192.168.121.31:8020/dww/flink/checkpoint/wordcount"));
        DataStream<String> lines = env.socketTextStream("localhost", 9999); // nc -l 9999
//        DataStream<String> lines = env.fromElements(WordCountData.WORDS);
        //纯java写法
        DataStream<Tuple2<String,Integer>> flatMap = lines.flatMap(new FlatMapFunction<String, Tuple2<String,Integer>>() {
            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
                String[] split = value.split(" ");
                for (String s : split) {
                    out.collect(new Tuple2(s,1));
                }
            }
        });
        KeyedStream<Tuple2<String, Integer>, Object> keyBy = flatMap.keyBy(new KeySelector<Tuple2<String, Integer>, Object>() {

            @Override
            public Object getKey(Tuple2<String, Integer> value) throws Exception {
                return value.f0;
            }
        });

        SingleOutputStreamOperator<Tuple2<String, Integer>> sum =  keyBy.sum(1);
        sum.print();


        //lambda写法
//        DataStream<Tuple2<String, Integer>> counts = lines.flatMap((String a, Collector<Tuple2<String,Integer>> out) -> Arrays.stream(a.split(" ")).forEach(x -> out.collect(new Tuple2<String,Integer>(x,1))))
//                .returns(Types.TUPLE(Types.STRING, Types.INT))// 如果这里想用函数式接口的lambda表达式的话，需要明确泛型返回的类型
//                .keyBy(value -> value.f0)
//                .sum(1);
//        counts.print();

        //定义一个静态类来实现flatMap
//        DataStream<Tuple2<String, Integer>> counts1 =
//                // split up the lines in pairs (2-tuples) containing: (word,1)
//                lines.flatMap(new Tokenizer())
//                        // group by the tuple field "0" and sum up tuple field "1"
//                        .keyBy(value -> value.f0)
//                        .sum(1);
//        counts1.print();
        env.execute("Flink WordCount with Java");
    }

    public static final class Tokenizer
            implements FlatMapFunction<String, Tuple2<String, Integer>> {

        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            // normalize and split the line
            String[] tokens = value.toLowerCase().split("\\W+");

            // emit the pairs
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
                }
            }
        }
    }
}
