package com.linfflow.flink.dev.datastream;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.List;

public class WordCountDemo {
    public static void main(String[] args) throws Exception {
//        testRichMapFunction();
//        testKeyByMutli();
//        wordcount();
//        testReduce();
//        testMinOrMax();
        testWindow();
    }

    /**
     * input: "dw 20200101 2"
     *
     * @throws Exception
     */
    public static void testKeyByMutli() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStream<String> lines = env.socketTextStream("localhost", 9999);
        DataStream<Tuple3<String, String, Integer>> map = lines.map(new MapFunction<String, Tuple3<String, String, Integer>>() {
            @Override
            public Tuple3<String, String, Integer> map(String value) throws Exception {
                String[] words = value.split(" ");
                String userId = words[0];
                String monthId = words[1];
                Integer cn = Integer.parseInt(words[2]);
                return Tuple3.of(userId, monthId, cn);
            }
        });
        //Deprecated api
//        KeyedStream<Tuple3<String, String, Integer>, Tuple> key = map.keyBy(0, 1);
        //new api
        KeyedStream<Tuple3<String, String, Integer>, String> key = map.keyBy(t -> t.f0 + t.f1);
        // another new api
//        key.print();
        key.min(2).print();
//        SingleOutputStreamOperator<Tuple3<String, String, Integer>> summed = key.sum(2);
//        summed.print();
        env.execute();
    }

    public static void testRichMapFunction() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStream<String> lines = env.socketTextStream("localhost", 9999);
        DataStream<String> map = lines.map(new RichMapFunction<String, String>() {
            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                System.out.println("建立连接");
            }

            @Override
            public void close() throws Exception {
                super.close();
                System.out.println("关闭连接");
            }

            @Override
            public String map(String value) throws Exception {
                return value;
            }
        });
        map.print();
        env.execute();
    }


    public static void wordcount() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> lines = env.socketTextStream("localhost", 9999);
        DataStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out) throws Exception {
                String[] split = value.split(" ");
                for (String s : split) {
                    out.collect(s);
                }
            }
        });
        DataStream<Tuple2<String, Long>> wordWithOne = words.map(new MapFunction<String, Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> map(String value) throws Exception {
                return new Tuple2(value, 1L);
            }
        });
        KeyedStream<Tuple2<String, Long>, String> keyedData = wordWithOne.keyBy(new KeySelector<Tuple2<String, Long>, String>() {
            @Override
            public String getKey(Tuple2<String, Long> value) throws Exception {
                return value.f0;
            }
        });
//        keyedData.print();
        WindowedStream<Tuple2<String, Long>, String, TimeWindow> windowedStream = keyedData.window(TumblingProcessingTimeWindows.of(Time.seconds(5)));
//        SingleOutputStreamOperator<Tuple2<String, Long>> sumData = keyedData.sum(1);
        SingleOutputStreamOperator<Tuple2<String, Long>> sumData = windowedStream.sum(1).setParallelism(2);
        sumData.print();
        env.execute("Flink WordCount");
    }


    public static void testWindow() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> lines = env.socketTextStream("localhost", 9999);
        DataStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out) throws Exception {
                String[] split = value.split(" ");
                for (String s : split) {
                    out.collect(s);
                }
            }
        });
        DataStream<Tuple2<String, Long>> wordWithOne = words.map(new MapFunction<String, Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> map(String value) throws Exception {
                return new Tuple2(value, 1L);
            }
        });

        AllWindowedStream<Tuple2<String, Long>, GlobalWindow> allWindowedStream = wordWithOne.countWindowAll(5);
        SingleOutputStreamOperator<Tuple2<String, Long>> reduce = allWindowedStream.reduce(new ReduceFunction<Tuple2<String, Long>>() {
            @Override
            public Tuple2<String, Long> reduce(Tuple2<String, Long> value1, Tuple2<String, Long> value2) throws Exception {
                return Tuple2.of(value1.f0, value1.f1 + value2.f1);
            }
        });
        reduce.print("reduce = ");

//        KeyedStream<Tuple2<String, Long>, String> keyedData = wordWithOne.keyBy(new KeySelector<Tuple2<String, Long>, String>() {
//            @Override
//            public String getKey(Tuple2<String, Long> value) throws Exception {
//                return value.f0;
//            }
//        });

//        WindowedStream<Tuple2<String, Long>, String, TimeWindow> windowedStream = keyedData.window(TumblingProcessingTimeWindows.of(Time.seconds(5)));
//        SingleOutputStreamOperator<Tuple2<String, Long>> sumData = windowedStream.sum(1).setParallelism(2);
//        sumData.print();
        env.execute("Flink window");
    }


    public static void testReduce() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> lines = env.socketTextStream("localhost", 9999);
        DataStream<Tuple2<String, Integer>> words = lines.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
                String[] split = value.split(" ");
                for (String s : split) {
                    out.collect(Tuple2.of(s, 1));
                }
            }
        });
        SingleOutputStreamOperator<Tuple2<String, Integer>> reduce = words.keyBy(t -> t.f0).reduce(new ReduceFunction<Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> reduce(Tuple2<String, Integer> value1, Tuple2<String, Integer> value2) throws Exception {
                return Tuple2.of(value1.f0, value1.f1 + value2.f1);
            }
        });
        reduce.print();
        env.execute("Flink WordCount");
    }


    public static void testMinOrMax() throws Exception {
        //获取运行环境的上下文
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //获取数据源
        List data = new ArrayList<Tuple3<Integer, Integer, Integer>>();
        data.add(new Tuple3<>(0, 1, 0));
        data.add(new Tuple3<>(0, 1, 1));
        data.add(new Tuple3<>(0, 2, 2));
        data.add(new Tuple3<>(0, 1, 3));
        data.add(new Tuple3<>(1, 2, 5));
        data.add(new Tuple3<>(1, 2, 9));
        data.add(new Tuple3<>(1, 2, 11));
        data.add(new Tuple3<>(1, 2, 13));
        data.add(new Tuple3<>(1, 2, 16));

        DataStreamSource<Tuple3<Integer, Integer, Integer>> items = env.fromCollection(data);
        items.keyBy(0).min(2).printToErr("min = ");
        items.keyBy(0).minBy(2).printToErr("minBy = ");

        //一定要触发执行，不然没结果输出
        env.execute();
    }

    static class WordCount {
        public String word;
        public Integer count;

        public WordCount(String word, Integer count) {
            this.word = word;
            this.count = count;
        }

        public WordCount() {
        }

        public static WordCount of(String word, Integer count) {
            return new WordCount(word, count);
        }

        @Override
        public String toString() {
            return "WordCount{" +
                    "word='" + word + '\'' +
                    ", count=" + count +
                    '}';
        }

    }
}
