package com.yiban.kafka.consumer;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @auther WEI.DUAN
 * @date 2018/8/29
 * @website http://blog.csdn.net/dwshmilyss
 */
public class KafkaConsumerDemo1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerDemo1.class);
    private final ConsumerConnector consumer;
    private final String topic;

//    public static final String TOPIC_NAME = "test_10_3";
//    public static final int numPartitions = 10;

    private ExecutorService executor;


    public void shutdown() {
        if (consumer != null) consumer.shutdown();
        if (executor != null) executor.shutdown();
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                System.out.println("Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted during shutdown, exiting uncleanly");
        }
    }


    public void run(int a_numThreads) {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(a_numThreads));
        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());
        Map<String, List<KafkaStream<String, String>>> consumerMap = consumer.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);
        List<KafkaStream<String, String>> streams = consumerMap.get(topic);

        // now launch all the threads
        //
        executor = Executors.newFixedThreadPool(a_numThreads);

        // now create an object to consume the messages
        //
        int threadNumber = 0;
        for (final KafkaStream stream : streams) {
            executor.submit(new ConsumerTest(stream, threadNumber));
            threadNumber++;
        }
    }

    public KafkaConsumerDemo1(String a_zookeeper, String a_groupId, String a_topic) {
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(
                createConsumerConfig(a_zookeeper, a_groupId));
        this.topic = a_topic;
    }


    private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("enable.auto.commit", "false");
        return new ConsumerConfig(props);
    }


    public static void main(String[] args) {

//        Properties props = new Properties();
//        // zookeeper 配置
//        props.put("zookeeper.connect", "10.21.3.74:2181");
//        props.put("group.id","test1");
//        // zk连接超时
//        props.put("zookeeper.session.timeout.ms", "4000");
//        props.put("zookeeper.sync.time.ms", "200");
//        props.put("auto.offset.reset", "smallest");
////            props.put("auto.offset.reset", "largest");
//        props.put("enable.auto.commit", "false");
////        props.put("auto.commit.interval.ms", "100");
//        //指定序列化处理类，默认为kafka.serializer.DefaultEncoder,即byte[]
//        props.put("serializer.class", "kafka.serializer.StringEncoder");
//        ConsumerConfig config = new ConsumerConfig(props);
//
//        consumer = Consumer.createJavaConsumerConnector(config);
//        System.out.println("KafkaConsumer 0.8.2.0 init completed.....");
//
//        test1(TOPIC_NAME,numPartitions);
        String zooKeeper = "10.21.3.74:2181";
        String groupId = "test";
        String topic = "test_10_3";
        int threads = 10;
        KafkaConsumerDemo1 example = new KafkaConsumerDemo1(zooKeeper, groupId, topic);
        example.run(threads);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException ie) {

        }
        example.shutdown();
    }

    /**
     * @param topic
     * @param numConsumers stream的个数 即Consumer的个数
     */
//    private static void test1(String topic, int numConsumers) {
//        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
//        topicCountMap.put(topic, numConsumers);
//
//        //因为createMessageStreams()的返回值类型是List<KafkaStream<byte[], byte[]>>，如果要解析成String，则需要加上解码器StringDecoder
//        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
//        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());
//
//        // 如果不带keyDecoder, valueDecoder 则createMessageStreams()的返回值类型是List<KafkaStream<byte[], byte[]>>
//        Map<String, List<KafkaStream<String, String>>> consumerMap = consumer.createMessageStreams(topicCountMap,
//                keyDecoder, valueDecoder);
//        KafkaStream<String, String> stream = consumerMap.get(topic).get(0);
//        ConsumerIterator<String, String> it = stream.iterator();
//        System.out.println(it.hasNext());
//        while (it.hasNext()) {
//            MessageAndMetadata metadata = it.next();
//            System.out.println("partition = " + metadata.partition() + ",offset = " + metadata.offset() + " ,message = " + metadata.message());
//        }
//    }
}


class ConsumerTest implements Runnable {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerTest.class);
    private KafkaStream m_stream;
    private int m_threadNumber;

    public ConsumerTest(KafkaStream a_stream, int a_threadNumber) {
        m_threadNumber = a_threadNumber;
        m_stream = a_stream;
    }

    public void run() {
        ConsumerIterator<String, String> it = m_stream.iterator();
        while (it.hasNext()) {
//            System.out.println("Thread " + m_threadNumber + ": " + new String(it.next().message()));
            MessageAndMetadata metadata = it.next();
            System.out.println("Thread " + m_threadNumber + ",partition = " + metadata.partition() + ",offset = " + metadata.offset() + " ,message = " + metadata.message());
            LOGGER.info("Thread " + m_threadNumber + ",partition = " + metadata.partition() + ",offset = " + metadata.offset() + " ,message = " + metadata.message());
        }
        System.out.println("Shutting down Thread: " + m_threadNumber);
    }
}
