package com.yiban.javaBase.dev.kafka;

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
    private static KafkaConsumer kafkaConsumer;

    private static final String GROUP = "MsgConsumer";
    private static final List TOPICS = Arrays.asList("test");

    static {
        Properties props = new Properties();
//        props.put("bootstrap.servers", "192.168.128.129:9092,192.168.128.129:9093,192.168.128.129:9094");
        props.put("bootstrap.servers", "10.21.3.129:9092");
        props.put("zookeeper.connect", "10.21.3.129:2181");
        props.put("group.id", "g3");
        //每次最小拉取的消息大小（byte）。Consumer会等待消息积累到一定尺寸后进行批量拉取。默认为1，代表有一条就拉一条
//        props.put("fetch.min.bytes", "1");
//        //每次从单个分区中拉取的消息最大尺寸（byte），默认为1M
//        props.put("max.partition.fetch.bytes", "");
        //是否自动提交已拉取消息的offset。提交offset即视为该消息已经成功被消费，该组下的Consumer无法再拉取到该消息（除非手动修改offset）。默认为true
        props.put("enable.auto.commit", "false");
        //和commit=false 配合使用并没有丢失消息
        props.put("auto.offset.reset", "earliest");
//        props.put("auto.offset.reset", "latest");
//        props.put("enable.auto.commit", "true");
        //自动提交offset的间隔毫秒数，默认5000
//        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");




        kafkaConsumer = new KafkaConsumer<String, String>(props);

        // 修改kafka日志输出级别(只针对当前的console)
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        Logger.getLogger("kafka").setLevel(Level.WARN);
        System.out.println("KafkaConsumer init completed.....");
    }


    /**
     * 自动提交offset
     */
    public void autoCommit() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());//key反序列化方式
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getCanonicalName());//value反系列化方式
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,true);//自动提交
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.59.130:9092,192.168.59.131:9092,192.168.59.132:9092");//指定broker地址，来找到group的coordinator
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,this.GROUP);//指定用户组

        KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(TOPICS);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);//100ms 拉取一次数据
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("topic: "+record.topic() + " key: " + record.key() + " value: " + record.value() + " partition: "+ record.partition());
            }
        }
    }


    /**
     * 手动提交offset
     */
    public void consumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());//key反序列化方式
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getCanonicalName());//value反系列化方式
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);//手动提交
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.59.130:9092,192.168.59.131:9092,192.168.59.132:9092");//指定broker地址，来找到group的coordinator
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,this.GROUP);//指定用户组

        KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(TOPICS);//指定topic消费

        long i = 0;
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);//100ms 拉取一次数据
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("topic: "+record.topic() + " key: " + record.key() + " value: " + record.value() + " partition: "+ record.partition());
                i ++;
            }

            if (i >= 100) {
                consumer.commitAsync();//手动commit
                i = 0;
            }
        }
    }


    public static void main(String[] args) {
        String topic = "test";
//        kafkaConsumer.subscribe(Arrays.asList(topic));
        TopicPartition partition0 = new TopicPartition(topic, 0);
        kafkaConsumer.assign(Arrays.asList(partition0));
        //获取当前topic指定partition的offset(已提交的最后一条)
        System.out.println(kafkaConsumer.position(partition0));

//        long time  = 1421849887000l;
//        Map<TopicPartition, Long> timestampsToSearch = new HashMap();
//        timestampsToSearch.put(partition0,time);
//
//        //大于指定time的最早的一条
//        Map<TopicPartition, OffsetAndTimestamp> res = kafkaConsumer.offsetsForTimes(timestampsToSearch);
//
//        for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry: res.entrySet()
//             ) {
//            System.out.println("=======================");
//            TopicPartition key = entry.getKey();
//            OffsetAndTimestamp value = entry.getValue();
//            System.out.println(key.topic()+","+key.partition()+","+value.timestamp()+","+value.offset());
//        }

        //跳到指定offset位置
//        kafkaConsumer.seek(partition0,122);
        //从指定partition的未提交的offset的开始位置开始消费 等价于auto.offset.reset => "earliest"
        kafkaConsumer.seekToBeginning(Arrays.asList(partition0));
        //跳到指定partition的结束位置 但是不改变kafkaConsumer.position(partition0)，不等价于auto.offset.reset => "latest"
//        kafkaConsumer.seekToEnd(Arrays.asList(partition0));

        /**
         * 可以指定分区，但是这样kafka就不会负载均衡了
         */
//        TopicPartition partition0 = new TopicPartition(topic, 0);
//        TopicPartition partition1 = new TopicPartition(topic, 1);
//        kafkaConsumer.assign(Arrays.asList(partition0, partition1));


        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(2000);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("fetched from partition " + record.partition() + ", offset: " + record.offset() + ", message: " + record.value() + ",time = " + record.timestamp() + ",key = " + record.key());
            }
            //手动提交offset(manual offset)
//            kafkaConsumer.commitSync();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
