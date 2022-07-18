package com.yiban.kafka.kafka_0_8.producer;


import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 但是这里的producer用的接口是java的
 * 是 org.apache.kafka.clients.producer.Producer，这个类的send()可以接收返回值
 * 还有一个scala版的 在KafkaProducerDemo1中
 * 是 kafka.javaapi.producer.Producer
 */

/**
 * Created by Administrator on 2018/8/26 0026.
 */
public class KafkaProducerDemo2 {
    static class KafkaProducer implements Runnable {

        //发送消息的topic
//        public static final String TOPIC_NAME = "test_1_1";
        public static final String TOPIC_NAME = "test_10_3";
        public static final int PARTITION_NUM = 10;

        @Deprecated
        private Producer producer = null;


        public KafkaProducer() {
            Properties props = new Properties();
            // 指定序列化处理类，默认为kafka.serializer.DefaultEncoder,即byte[]
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            // 同步还是异步，异步可以提高发送吞吐量，但是也可能导致丢失未发送过去的消息
            props.put("producer.type", "sync");
            // 是否压缩，默认0表示不压缩，1表示用gzip压缩，2表示用snappy压缩。压缩后消息中会有头来指明消息压缩类型，故在消费者端消息解压是透明的无需指定。
            props.put("compression.codec", "1");
            // 指定kafka节点列表，用于获取metadata(元数据)，不必全部指定
            props.put("bootstrap.servers", "master02:9092,slave01:9092,slave02:9092,slave03:9092");

            //request.required.acks
            //0, which means that the producer never waits for an acknowledgement from the broker (the same behavior as 0.7). This option provides the lowest latency but the weakest durability guarantees (some data will be lost when a server fails).
            //1, which means that the producer gets an acknowledgement after the leader replica has received the data. This option provides better durability as the client waits until the server acknowledges the request as successful (only messages that were written to the now-dead leader but not yet replicated will be lost).
            //-1, which means that the producer gets an acknowledgement after all in-sync replicas have received the data. This option provides the best durability, we guarantee that no messages will be lost as long as at least one in sync replica remains.
            props.put("request.required.acks", "-1");

            //最小副本同步成功数，如果broker同步副本的数目小于该设定值，则producer抛出异常
            //只有这个参数和上面的参数同时设定，才能保证生产者消息不丢失
            props.put("min.insync.replicas", 2);

            producer = new org.apache.kafka.clients.producer.KafkaProducer(props);
        }


        /**
         * 不指定分区的生产者
         *
         * @throws ExecutionException
         * @throws InterruptedException
         */
        public void produce() throws ExecutionException, InterruptedException {
            int count = 1000;
            for (int i = 0; i < count; i++) {
                String key = String.valueOf(i);
                String message = "this is message : " + i;
                //这里没有指定分区，默认使用hash(key)作为分区的规则
                ProducerRecord<String, String> record = new ProducerRecord(TOPIC_NAME, key, message);
                Future<RecordMetadata> recordMetadataFuture = producer.send(record);
                RecordMetadata recordMetadata = recordMetadataFuture.get();
                String topic = recordMetadata.topic();
                int partition = recordMetadata.partition();
                long offset = recordMetadata.offset();
                System.out.println("topic = " + topic + ",partition = " + partition + ",offset = " + offset);
            }

            //发送完成后关闭
            producer.close();
        }

        public void run() {
            for (int i = 0; i < PARTITION_NUM; i++) { //往10个分区发数据
                for (int j = 0; j < 10; j++) {
                    ProducerRecord<String, String> record = (new ProducerRecord<String, String>
                            //String topic, int partition ,String key,String message
                            (TOPIC_NAME, i, "key[" + j + "]", "message[" + j + "]"));
                    producer.send(record, (metadata, exception) -> {
                        //注册回调方法
                        if (exception != null)
                            exception.printStackTrace();
                        System.out.printf("Send record topic:%s, partition:%d, offset:%d %n",
                                metadata.topic(), metadata.partition(), metadata.offset());
                    });
                }
            }

            producer.close();
        }
    }

    public static void main(String[] args) {
        Thread t = new Thread(new KafkaProducer());
        t.start();

//        new KafkaProducer().produce();
    }
}
