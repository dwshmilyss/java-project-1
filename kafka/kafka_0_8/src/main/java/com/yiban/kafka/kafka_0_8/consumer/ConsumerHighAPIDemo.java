package com.yiban.kafka.kafka_0_8.consumer;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
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
public class ConsumerHighAPIDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerHighAPIDemo.class);
    private final ConsumerConnector consumer;
    private static final ConsumerConnector consumer1 = null;
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


    /**
     * 多线程消费 最好的情况就是一个线程消费一个partition
     * @param a_numThreads
     */
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
            executor.submit(new ConsumerThread(stream, threadNumber));
            threadNumber++;
        }
    }

    public ConsumerHighAPIDemo(String a_zookeeper, String a_groupId, String a_clientId, String a_topic) {
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(
                createConsumerConfig(a_zookeeper, a_groupId,a_clientId));
        this.topic = a_topic;
    }


    private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId , String a_clientId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        // 其实这里不需要hostname 因为默认就是
        System.out.println("a_clientId = " + a_clientId);
        props.put("client.id", a_clientId);
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.commit.enable", "false");
//        props.put("auto.offset.reset","smallest");
        props.put("auto.offset.reset","largest");
        return new ConsumerConfig(props);
    }


    public static void main(String[] args) {

        String zooKeeper = "10.21.3.75:2181";
        String groupId = "test1";
        InetAddress inetAddress = null;
        try {
            inetAddress = java.net.InetAddress.getLocalHost();
        }catch (java.net.UnknownHostException e){
            e.printStackTrace();
        }
        System.out.println("hostname = " + inetAddress.getHostName());
        String clientId = inetAddress != null ? inetAddress.getHostName() : "none";
        System.out.println("clientId = " + clientId);
        String topic = "test_10_3";
        int threads = 10;
        ConsumerHighAPIDemo example = new ConsumerHighAPIDemo(zooKeeper, groupId, clientId, topic);
        example.run(threads);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException ie) {

        }

        while (true){}
//        example.shutdown();
    }

    /**
     * @param topic
     * @param numConsumers stream的个数 即Consumer的个数
     */
    private static void test1(String topic, int numConsumers) {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, numConsumers);

        //因为createMessageStreams()的返回值类型是List<KafkaStream<byte[], byte[]>>，如果要解析成String，则需要加上解码器StringDecoder
        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

        // 如果不带keyDecoder, valueDecoder 则createMessageStreams()的返回值类型是List<KafkaStream<byte[], byte[]>>
        Map<String, List<KafkaStream<String, String>>> consumerMap = consumer1.createMessageStreams(topicCountMap,
                keyDecoder, valueDecoder);
        List<KafkaStream<String, String>> streams = consumerMap.get(topic);
        for (KafkaStream<String, String> stream : streams) {
            ConsumerIterator<String, String> it = stream.iterator();
            System.out.println(it.hasNext());
            while (it.hasNext()) {
                MessageAndMetadata metadata = it.next();
                System.out.println("partition = " + metadata.partition() + ",offset = " + metadata.offset() + " ,message = " + metadata.message());
            }
        }
    }
}


class ConsumerThread implements Runnable {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerThread.class);
    private KafkaStream m_stream;
    private int m_threadNumber;

    public ConsumerThread(KafkaStream a_stream, int a_threadNumber) {
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
