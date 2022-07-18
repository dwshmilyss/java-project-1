package com.yiban.kafka.kafka_0_8.producer;

import kafka.producer.KeyedMessage;
/**
 * 这里一定要导入的是javaapi
 * 不能是java.producer.Producer（这个producer是Scala的）
 *  不然下面send的地方会报错
 *  但是这里的producer用的接口依然是scala的 还有一个java版的 在KafkaProducerDemo2中
 *  注意这里的Producer是 kafka.javaapi.producer.Producer  要和 org.apache.kafka.clients.producer.Producer 区分开来
 */
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Administrator on 2018/8/26 0026.
 */
public class KafkaProducerDemo1 {
    static class KafkaProducer implements Runnable {

        //发送消息的topic
        public static final String TOPIC_NAME = "test_1_1";
//        public static final String TOPIC_NAME = "test_10_3";
        public static final int PARTITION_NUM = 10;

        @Deprecated
        private Producer producer = null;
        private ProducerConfig config = null;


        public KafkaProducer() {
            Properties props = new Properties();
            // 指定序列化处理类，默认为kafka.serializer.DefaultEncoder,即byte[]
            props.put("serializer.class", "kafka.serializer.StringEncoder");
            // 同步还是异步，异步可以提高发送吞吐量，但是也可能导致丢失未发送过去的消息
            props.put("producer.type", "sync");
            // 是否压缩，默认0表示不压缩，1表示用gzip压缩，2表示用snappy压缩。压缩后消息中会有头来指明消息压缩类型，故在消费者端消息解压是透明的无需指定。
            props.put("compression.codec", "1");
            // 指定kafka节点列表，用于获取metadata(元数据)，不必全部指定
            props.put("metadata.broker.list", "master02:9092,slave01:9092,slave02:9092,slave03:9092");

            //request.required.acks
            //0, which means that the producer never waits for an acknowledgement from the broker (the same behavior as 0.7). This option provides the lowest latency but the weakest durability guarantees (some data will be lost when a server fails).
            //1, which means that the producer gets an acknowledgement after the leader replica has received the data. This option provides better durability as the client waits until the server acknowledges the request as successful (only messages that were written to the now-dead leader but not yet replicated will be lost).
            //-1, which means that the producer gets an acknowledgement after all in-sync replicas have received the data. This option provides the best durability, we guarantee that no messages will be lost as long as at least one in sync replica remains.
            props.put("request.required.acks","-1");

            //最小副本同步成功数，如果broker同步副本的数目小于该设定值，则producer抛出异常
            //只有这个参数和上面的参数同时设定，才能保证生产者消息不丢失
            props.put("min.insync.replicas",2);

            config = new ProducerConfig(props);
            producer = new Producer<String, String>(config);
        }


        public void produce(){
            int count = 1000;
            for (int i = 0; i < count; i++) {
                String key = String.valueOf(i);
                String message = "topic - "+i;
                producer.send(new KeyedMessage<String, String>(TOPIC_NAME, key ,message));
            }

            //发送完成后关闭
            producer.close();
        }

        public void run() {
            for (int i = 0; i < PARTITION_NUM; i++) { //往10个分区发数据
                List<KeyedMessage<String, String>> messageList = new ArrayList<KeyedMessage<String, String>>();
                for (int j = 0; j < PARTITION_NUM; j++) { //每个分区10条讯息
                    messageList.add(new KeyedMessage<String, String>
                            //String topic, String key,int partition ,String message
                            (TOPIC_NAME, "key[" + i + "]",i,"message[" + i + "]"));
                }
                producer.send(messageList);
            }

            producer.close();
        }
    }

    public static void main(String[] args) {
//        Thread t = new Thread(new KafkaProducer());
//        t.start();

        new KafkaProducer().produce();
    }
}
