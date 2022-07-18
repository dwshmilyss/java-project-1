package com.yiban.kafka.kafka_0_10.api.consumer;


import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * kafka consumer
 *
 * @auther WEI.DUAN
 * @create 2017/5/14
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class KafkaConsumerDemo1 {
    //0.10.1.0
    private static KafkaConsumer kafkaConsumer;


    private static final String GROUP = "test_1_1";
    private static final List TOPICS = Arrays.asList("test_1_1");
    public static final String BROKERS = "10.21.3.74:9092,10.21.3.75:9092,10.21.3.76:9092,10.21.3.77:9092";

    static {
        // 修改kafka日志输出级别(只针对当前的console)
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        Logger.getLogger("kafka").setLevel(Level.WARN);
    }

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(KafkaConsumerDemo1.class);


    /**
     * 自动提交offset
     */
    public static void autoCommit() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());//key反序列化方式
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());//value反系列化方式
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);//自动提交
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKERS);//指定broker地址，来找到group的coordinator
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP);//指定用户组

        KafkaConsumer<String, String> consumer = new KafkaConsumer(properties);
        consumer.subscribe(TOPICS);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);//100ms 拉取一次数据
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("topic: " + record.topic() + " key: " + record.key() + " value: " + record.value() + " partition: " + record.partition());
            }
        }
    }


    /**
     * 手动提交offset
     */
    public static void manualCommit() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());//key反序列化方式
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());//value反系列化方式
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);//手动提交
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKERS);//指定broker地址，来找到group的coordinator
//        properties.put("zookeeper.connect","10.21.3.76:2181");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP);//指定用户组

        KafkaConsumer<String, String> consumer = new KafkaConsumer(properties);
        consumer.subscribe(TOPICS);//指定topic消费

        long i = 0;
        while (true) {
            //每100条提交一次
            ConsumerRecords<String, String> records = consumer.poll(100);//100ms 拉取一次数据
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("topic: " + record.topic() + " key: " + record.key() + " value: " + record.value() + " partition: " + record.partition());
                i++;
            }

            if (i >= 100) {
                consumer.commitAsync();//手动commit
                i = 0;
            }

            //获取该topic下所有partition的所有记录，每消费完一个partition提交一次
            for (TopicPartition partition : records.partitions()) {
                List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
                for (ConsumerRecord<String, String> record : partitionRecords) {
                    System.out.println("now consumer the message it's offset is :" + record.offset() + " and the value is :" + record.value());
                }
                long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                System.out.println("now commit the partition[ " + partition.partition() + "] offset");
                consumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(lastOffset + 1)));
            }
        }
    }


    public static void main(String[] args) {
//        String topic = "test_10_3";
//        createConsumer(topic);

        manualCommit();
    }


    private static void createConsumer(String topic) {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());//key反序列化方式
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());//value反系列化方式
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);//手动提交

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKERS);//指定broker地址，来找到group的coordinator
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP);//指定用户组
        //每次最小拉取的消息大小（byte）。Consumer会等待消息积累到一定尺寸后进行批量拉取。默认为1，代表有一条就拉一条
//        props.put("fetch.min.bytes", "1");
//        //每次从单个分区中拉取的消息最大尺寸（byte），默认为1M
//        props.put("max.partition.fetch.bytes", "");
        //是否自动提交已拉取消息的offset。提交offset即视为该消息已经成功被消费，该组下的Consumer无法再拉取到该消息（除非手动修改offset）。默认为true
        //和commit=false 配合使用并没有丢失消息
        /**
         * 当无法获取当前offset的时候该怎么处理，这个配置项的默认值是“latest”，而当我们新建一个group对主题订阅的时候，第一次应该是符合这个要求的（无法知道当前的offset值），
         * 这个时候就触发了“latest”这个配置值对应的操作，也就是说把当前topic里面最新的偏移作为offset，那显然，该消费者是读不到主题中的历史信息的，于是把配置的值改为“earliest”，发现正常了，
         需要注意的是，这个配置只在group第一次订阅主题的时候触发，一旦这个offset值被确定下来了，你再把这个配置改成“earliest”就没效果了，因为他已经不符合这个条件了（这个group在这个主题下已经能拿到offset值了）
         */
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//            props.put("auto.offset.reset", "latest");
        //自动提交offset的间隔毫秒数，默认5000
        properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000");
        properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");

        kafkaConsumer = new KafkaConsumer<String, String>(properties);
        System.out.println("KafkaConsumer 0.10.2.0 init completed.....");
        test_0_10_2_0(topic);
    }


    private static void test_0_10_2_0(String topic) {
        TopicPartition test_partition_0 = new TopicPartition(topic, 0);
        TopicPartition test_partition_1 = new TopicPartition(topic, 1);
        TopicPartition test_partition_2 = new TopicPartition(topic, 2);
        TopicPartition test_partition_3 = new TopicPartition(topic, 3);
        TopicPartition test_partition_4 = new TopicPartition(topic, 4);
        TopicPartition test_partition_5 = new TopicPartition(topic, 5);
        TopicPartition test_partition_6 = new TopicPartition(topic, 6);
        TopicPartition test_partition_7 = new TopicPartition(topic, 7);

        //订阅某个topic 多个topic也可以 (相当于以前的高阶API)
//        kafkaConsumer.subscribe(Arrays.asList(topic));

        //指定partition消费 (相当于低阶API)
        /**
         * 可以指定分区，但是这样kafka就不会负载均衡了
         */
//        kafkaConsumer.assign(Arrays.asList(test_partition_0, test_partition_1, test_partition_2, test_partition_3, test_partition_4, test_partition_5, test_partition_6, test_partition_7, test_partition_8, test_partition_9));

//        kafkaConsumer.pause(Arrays.asList(partition0));

        /**
         *         获取当前topic指定partition的offset
         */
//        System.out.println("topic = " + topic + "，partition = 0 ，offset = " + kafkaConsumer.position(test_partition_0));
//        System.out.println("topic = " + topic + "，partition = 1 ，offset = " + kafkaConsumer.position(test_partition_1));
//        System.out.println("topic = " + topic + "，partition = 2 ，offset = " + kafkaConsumer.position(test_partition_2));
//        System.out.println("topic = " + topic + "，partition = 3 ，offset = " + kafkaConsumer.position(test_partition_3));
//        System.out.println("topic = " + topic + "，partition = 4 ，offset = " + kafkaConsumer.position(test_partition_4));
//        System.out.println("topic = " + topic + "，partition = 5 ，offset = " + kafkaConsumer.position(test_partition_5));
//        System.out.println("topic = " + topic + "，partition = 6 ，offset = " + kafkaConsumer.position(test_partition_6));
//        System.out.println("topic = " + topic + "，partition = 7 ，offset = " + kafkaConsumer.position(test_partition_7));
//        System.out.println("topic = " + topic + "，partition = 8 ，offset = " + kafkaConsumer.position(test_partition_8));
//        System.out.println("topic = " + topic + "，partition = 9 ，offset = " + kafkaConsumer.position(test_partition_9));

        /**
         * 设定一个消费开始时间 2018-09-04 18:40:00
         */
        long startTime = Long.valueOf(1536057600*1000L);
        Map<TopicPartition, Long> startTopicPartitionMap = new HashMap();
        startTopicPartitionMap.put(test_partition_0, startTime);
        startTopicPartitionMap.put(test_partition_1, startTime);
        startTopicPartitionMap.put(test_partition_2, startTime);
        startTopicPartitionMap.put(test_partition_3, startTime);
        startTopicPartitionMap.put(test_partition_4, startTime);
        startTopicPartitionMap.put(test_partition_5, startTime);
        startTopicPartitionMap.put(test_partition_6, startTime);

        startTopicPartitionMap.put(test_partition_7, startTime);
        //大于指定time的最早的一条
        Map<TopicPartition, OffsetAndTimestamp> startOffsetMap = kafkaConsumer.offsetsForTimes(startTopicPartitionMap);

        System.out.println("=======================");
        print_offset_timestamp_info(startOffsetMap);

        /**
         * 设定一个消费结束时间 2018-09-05 00:00:00
         */
        long endTime = Long.valueOf(1536076800*1000L);
        Map<TopicPartition, Long> endTopicPartitionMap = new HashMap();
        endTopicPartitionMap.put(test_partition_0, endTime);
        endTopicPartitionMap.put(test_partition_1, endTime);
        endTopicPartitionMap.put(test_partition_2, endTime);
        endTopicPartitionMap.put(test_partition_3, endTime);
        endTopicPartitionMap.put(test_partition_4, endTime);
        endTopicPartitionMap.put(test_partition_5, endTime);
        endTopicPartitionMap.put(test_partition_6, endTime);
        endTopicPartitionMap.put(test_partition_7, endTime);

        Map<TopicPartition, OffsetAndTimestamp> endOffsetMap = kafkaConsumer.offsetsForTimes(endTopicPartitionMap);
        System.out.println("=======================");
        print_offset_timestamp_info(endOffsetMap);


        /**
         * 获取partition开始的offset
         */
        Map<TopicPartition, Long> beginningOffsets = kafkaConsumer.beginningOffsets(startTopicPartitionMap.keySet());
        System.out.println("=======================");
        printInfo(beginningOffsets);


        /**
         * 获取partition结束的offset
         */
        Map<TopicPartition, Long> endOffsets = kafkaConsumer.endOffsets(startTopicPartitionMap.keySet());
        System.out.println("=======================");
        printInfo(endOffsets);


        /**
         * 跳到指定offset位置
         */
//        kafkaConsumer.seek(test_partition_0, 0);
//        kafkaConsumer.seek(test_partition_1, 0);
//        kafkaConsumer.seek(test_partition_2, 0);
//        kafkaConsumer.seek(test_partition_3, 0);
//        kafkaConsumer.seek(test_partition_4, 0);
//        kafkaConsumer.seek(test_partition_5, 0);
//        kafkaConsumer.seek(test_partition_6, 0);
//        kafkaConsumer.seek(test_partition_7, 0);
//        kafkaConsumer.seek(test_partition_8, 0);
//        kafkaConsumer.seek(test_partition_9, 0);
//
//
//        //从指定partition的未提交的offset的开始位置开始消费 等价于auto.offset.reset => "earliest"
////        kafkaConsumer.seekToBeginning(Arrays.asList(partition0));
//        //跳到指定partition的结束位置 但是不改变kafkaConsumer.position(partition0)，不等价于auto.offset.reset => "latest"
        kafkaConsumer.seekToEnd(Arrays.asList(test_partition_0));
//
//
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                LOGGER.info(record.toString());
            }
//            手动提交offset(manual offset)
//            kafkaConsumer.commitSync();
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    private static void print_offset_timestamp_info(Map<TopicPartition, OffsetAndTimestamp> offsetMap) {
        for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : offsetMap.entrySet()
                ) {
            System.out.println("----------------------------");
            TopicPartition key = entry.getKey();
            OffsetAndTimestamp value = entry.getValue();
            System.out.println("topic = " + key.topic() + ",partition = " + key.partition() + ",timestamp = " + value.timestamp() + ",offset = " + value.offset());
        }
    }

    private static void printInfo(Map<TopicPartition, Long> info) {
        for (Map.Entry<TopicPartition, Long> entry : info.entrySet()
                ) {
            System.out.println("-----------------------------");
            TopicPartition key = entry.getKey();
            long value = entry.getValue();
            System.out.println("topic = " + key.topic() + ",partition = " + key.partition() + ",offset = " + value);
        }
    }
}
