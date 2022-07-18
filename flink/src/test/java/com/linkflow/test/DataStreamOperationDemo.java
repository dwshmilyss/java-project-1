package com.linkflow.test;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.table.data.TimestampData;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;


public class DataStreamOperationDemo {
    @Test
    public void testUtils() {
        System.out.println(TimestampData.fromEpochMillis(1));
    }

    @Test
    public void testPattern() {
        Pattern pattern = java.util.regex.Pattern.compile("^[a-zA-Z]*_362$");
        System.out.println(pattern.matcher("event_362").matches());
    }

    @Test
    public void testKafka() throws Exception {
        // 1.获取flink流计算的运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //Kafka props
        Properties properties = new Properties();
        //指定Kafka的Broker地址
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //指定组ID
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "kafka_test_group1");
        //如果没有记录偏移量，第一次从最开始消费
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.setProperty("flink.partition-discovery.interval-millis", String.valueOf(10 * 1000));

        // 2.从kafka读取数据
//        FlinkKafkaConsumer<String> kafkaSource = new FlinkKafkaConsumer<>(java.util.regex.Pattern.compile("t[0-9]"), new SimpleStringSchema(), properties);
        FlinkKafkaConsumer<String> kafkaSource = new FlinkKafkaConsumer<>(java.util.regex.Pattern.compile("^[a-zA-Z]*_362$"), new SimpleStringSchema(), properties);
        DataStreamSource<String> stringDataStreamSource = env.addSource(kafkaSource);
        //3.调用Sink
        stringDataStreamSource.print();
        //4.启动流计算
        env.execute("KafkaSouceReview");
    }

    @Test
    public void testReadData() throws Exception {
        //1.获取执行环境配置信息
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        DataStream<String> textStream = env.fromElements(WordCountData.WORDS);
        DataStream<String> textStream = env.fromCollection(Arrays.asList(WordCountData.WORDS.clone()));
        DataStream<Tuple2<String, Integer>> counts =
                // split up the lines in pairs (2-tuples) containing: (word,1)
                textStream.flatMap(new Tokenizer())
                        // group by the tuple field "0" and sum up tuple field "1"
                        .keyBy(value -> value.f0)
                        .sum(1);
        counts.print().setParallelism(1);
        //5.开始执行
        env.execute("socket with flink");
    }

    @Test
    public void testKeyBy() throws Exception {
        //1.获取执行环境配置信息
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        //2.定义加载或创建数据源（source）,监听9000端口的socket消息
        DataStream<String> textStream = env.socketTextStream("localhost", 9999);
        //3.
        DataStream<Tuple2<String, Integer>> result = textStream
                //map是将每一行单词变为一个tuple2
                .map(line -> Tuple2.of(line.trim(), 1))
                //如果要用Lambda表示是，Tuple2是泛型，那就得用returns指定类型。
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                //keyBy进行分区，按照第一列，也就是按照单词进行分区
                .keyBy(0);
                //指定窗口，每10秒个计算一次
//                .timeWindow(Time.of(5, TimeUnit.SECONDS),Time.of(5, TimeUnit.SECONDS))
//                .timeWindow(Time.of(5, TimeUnit.SECONDS))
                //计算个数，计算第1列
//                .sum(1);
        //4.打印输出sink
        result.print().setParallelism(1);
        //5.开始执行
        env.execute("socket with flink");
    }

    /**
     * 主要为了存储单词以及单词出现的次数
     */
    public static class WordWithCount{
        public String word;
        public long count;
        public WordWithCount(){}
        public WordWithCount(String word, long count) {
            this.word = word;
            this.count = count;
        }

        @Override
        public String toString() {
            return "WordWithCount{" +
                    "word='" + word + '\'' +
                    ", count=" + count +
                    '}';
        }
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
