package com.yiban.javaBase.dev.kafka;

import com.alibaba.fastjson.serializer.ObjectSerializer;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import scala.None;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * kafka consumer
 *
 * @auther WEI.DUAN
 * @create 2017/5/14
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class KafkaConsumerDemo1 {
    private static KafkaConsumer kafkaConsumer;

    private static kafka.javaapi.consumer.ConsumerConnector consumer;

    private static final String GROUP = "MsgConsumer";
    private static final List TOPICS = Arrays.asList("test");

    static {
        // 修改kafka日志输出级别(只针对当前的console)
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        Logger.getLogger("kafka").setLevel(Level.WARN);
    }


    /**
     * 自动提交offset
     */
    public void autoCommit() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());//key反序列化方式
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());//value反系列化方式
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);//自动提交
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.59.130:9092,192.168.59.131:9092,192.168.59.132:9092");//指定broker地址，来找到group的coordinator
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, this.GROUP);//指定用户组

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
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
    public void consumer() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());//key反序列化方式
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());//value反系列化方式
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);//手动提交
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.59.130:9092,192.168.59.131:9092,192.168.59.132:9092");//指定broker地址，来找到group的coordinator
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, this.GROUP);//指定用户组

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        consumer.subscribe(TOPICS);//指定topic消费

        long i = 0;
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);//100ms 拉取一次数据
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("topic: " + record.topic() + " key: " + record.key() + " value: " + record.value() + " partition: " + record.partition());
                i++;
            }

            if (i >= 100) {
                consumer.commitAsync();//手动commit
                i = 0;
            }
        }
    }


    public static void main(String[] args) {
        String topic = "test1";
        int kafkaVersion = 10;
        createConsumer(kafkaVersion, topic);
    }


    private static void createConsumer(int version, String topic) {
        Properties props = new Properties();
        props.put("group.id", "g7");
        if (version == 10) {
//        props.put("bootstrap.servers", "192.168.128.129:9092,192.168.128.129:9093,192.168.128.129:9094");
            props.put("bootstrap.servers", "10.21.3.129:9092");
            //每次最小拉取的消息大小（byte）。Consumer会等待消息积累到一定尺寸后进行批量拉取。默认为1，代表有一条就拉一条
//        props.put("fetch.min.bytes", "1");
//        //每次从单个分区中拉取的消息最大尺寸（byte），默认为1M
//        props.put("max.partition.fetch.bytes", "");
            //是否自动提交已拉取消息的offset。提交offset即视为该消息已经成功被消费，该组下的Consumer无法再拉取到该消息（除非手动修改offset）。默认为true
            //和commit=false 配合使用并没有丢失消息
            /**
             * 当无法获取当前offset的时候该怎么处理，这个配置项的默认值是“latest”，而当我们新建一个group对主题订阅的时候，第一次应该是符合这个要求的（无法知道当前的offset值），这个时候就触发了“latest”这个配置值对应的操作，也就是说把当前topic里面最新的偏移作为offset，那显然，该消费者是读不到主题中的历史信息的，于是把配置的值改为“earliest”，发现正常了，
             需要注意的是，这个配置只在group第一次订阅主题的时候触发，一旦这个offset值被确定下来了，你再把这个配置改成“earliest”就没效果了，因为他已经不符合这个条件了（这个group在这个主题下已经能拿到offset值了）
             */
            props.put("auto.offset.reset", "earliest");
//            props.put("auto.offset.reset", "latest");
//            props.put("enable.auto.commit", "true");
            props.put("enable.auto.commit", "false");
            //自动提交offset的间隔毫秒数，默认5000
//            props.put("auto.commit.interval.ms", "1000");
            props.put("session.timeout.ms", "30000");
            props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");


//            Properties props1 = new Properties();
//            props.put("bootstrap.servers", {allthreeservers});
//            props.put("group.id", groupId);
//            props.put("key.deserializer", StringDeserializer.class.getName());
//            props.put("value.deserializer", ObjectSerializer.class.getName());
//            props.put("auto.offset.reset", erlierst);
//            props.put("enable.auto.commit", false);
//            props.put("session.timeout.ms", 30000);
//            props.put("heartbeat.interval.ms", 10000);
//            props.put("request.timeout.ms", 31000);
//            props.put("kafka.consumer.topic.name", topic);
//            props.put("max.partition.fetch.bytes", 1000);

            kafkaConsumer = new KafkaConsumer<String, String>(props);
            System.out.println("KafkaConsumer 0.10.2.0 init completed.....");
            test_0_10_2_0(topic);
        } else if (version == 8) {
            // zookeeper 配置
            props.put("zookeeper.connect", "10.21.3.129:2181");
            // zk连接超时
            props.put("zookeeper.session.timeout.ms", "4000");
            props.put("zookeeper.sync.time.ms", "200");
            props.put("auto.offset.reset", "smallest");
//            props.put("auto.offset.reset", "largest");
            props.put("enable.auto.commit", "true");
            props.put("auto.commit.interval.ms", "100");
            // 序列化类
            props.put("serializer.class", "kafka.serializer.StringEncoder");
            kafka.consumer.ConsumerConfig config = new kafka.consumer.ConsumerConfig(props);

            consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config);
            System.out.println("KafkaConsumer 0.8.2.0 init completed.....");
            test_0_8_2_1(topic);
        }

    }


    private static void test_0_8_2_1(String topic) {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(1));

        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

        Map<String, List<KafkaStream<String, String>>> consumerMap = consumer.createMessageStreams(topicCountMap,
                keyDecoder, valueDecoder);
        KafkaStream<String, String> stream = consumerMap.get(topic).get(0);
        ConsumerIterator<String, String> it = stream.iterator();
        System.out.println(it.hasNext());
        while (it.hasNext()) {
            MessageAndMetadata metadata = it.next();
            System.out.println("offset = " + metadata.offset() + ",partition = " + metadata.partition() + ",message = " + metadata.message());
        }
    }

    private static void test_0_10_2_0(String topic) {
        TopicPartition partition0 = new TopicPartition(topic, 0);
        kafkaConsumer.subscribe(Arrays.asList(topic));
//        kafkaConsumer.assign(Arrays.asList(partition0));
        //获取当前topic指定partition的offset
//        System.out.println(kafkaConsumer.position(partition0));

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
//        kafkaConsumer.seekToBeginning(Arrays.asList(partition0));
        //跳到指定partition的结束位置 但是不改变kafkaConsumer.position(partition0)，不等价于auto.offset.reset => "latest"
//        kafkaConsumer.seekToEnd(Arrays.asList(partition0));

        /**
         * 可以指定分区，但是这样kafka就不会负载均衡了
         */
//        TopicPartition partition0 = new TopicPartition(topic, 0);
//        TopicPartition partition1 = new TopicPartition(topic, 1);
//        kafkaConsumer.assign(Arrays.asList(partition0, partition1));

        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(Thread.currentThread().getName()+",fetched from partition " + record.partition() + ", offset: " + record.offset() + ", message: " + record.value() + ",time = " + record.timestamp() + ",key = " + record.key());
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
