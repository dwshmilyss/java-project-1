package com.linfflow.flink.dev;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;
import java.util.regex.Pattern;


public class KafkaDemo {

    //192.168.26.11:31090,192.168.26.11:31091,192.168.26.11:31092
    public static void main(String[] args) throws Exception {
        testKafka(args[0],args[1],args[2]);
    }

    public static void testPattern() {
        Pattern pattern = Pattern.compile("^[a-zA-Z]*_362$");
        System.out.println(pattern.matcher("event_362").matches());
    }

    public static void testKafka(String tenant_id,String brokerList,String groupId) throws Exception {
        // 1.获取flink流计算的运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //Kafka props
        Properties properties = new Properties();
        //指定Kafka的Broker地址 "localhost:9092"
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
        //指定组ID
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        //如果没有记录偏移量，第一次从最开始消费
        //                "kafka.key.deserializer",
        //                "kafka.value.deserializer",
        //                "kafka.key.serializer",
        //                "kafka.value.serializer",
        //                "kafka.request.timeout.ms",
        //                "kafka.max.poll.interval.ms",
        //                "kafka.session.timeout.ms",
        //                "kafka.heartbeat.interval.ms"
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "1800000");
//        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "false");
//        properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "false");
        properties.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, "240000");
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "180000");
        properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "120000");
        properties.setProperty("flink.partition-discovery.interval-millis", String.valueOf(10 * 1000));

        // 2.从kafka读取数据
//        FlinkKafkaConsumer<String> kafkaSource = new FlinkKafkaConsumer<>(java.util.regex.Pattern.compile("t[0-9]"), new SimpleStringSchema(), properties);
        FlinkKafkaConsumer<String> kafkaSource = new FlinkKafkaConsumer<>(Pattern.compile("^[a-zA-Z]*_"+tenant_id+"$"), new SimpleStringSchema(), properties);
        DataStreamSource<String> stringDataStreamSource = env.addSource(kafkaSource);
        //3.调用Sink
        stringDataStreamSource.print();
        //4.启动流计算
        env.execute("KafkaSouceReview");
    }


}
