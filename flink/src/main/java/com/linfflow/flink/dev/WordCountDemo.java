package com.linfflow.flink.dev;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Arrays;
import java.util.List;

public class WordCountDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> lines = env.socketTextStream("localhost", 9999);
        //纯java写法
//        DataStream<Tuple2<String,Integer>> flatMap = lines.flatMap(new FlatMapFunction<String, Tuple2<String,Integer>>() {
//            @Override
//            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
//                String[] split = value.split(" ");
//                for (String s : split) {
//                    out.collect(new Tuple2(s,1));
//                }
//            }
//        });
//        KeyedStream<Tuple2<String, Integer>, Object> keyBy = flatMap.keyBy(new KeySelector<Tuple2<String, Integer>, Object>() {
//
//            @Override
//            public Object getKey(Tuple2<String, Integer> value) throws Exception {
//                return value.f0;
//            }
//        });
//
//        SingleOutputStreamOperator<Tuple2<String, Integer>> sum =  keyBy.sum(1);
//        sum.print();


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
