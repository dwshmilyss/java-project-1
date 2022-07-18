package com.yiban.kafka.kafka_0_10.api.producer;


import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * kafka producer
 *
 * @auther WEI.DUAN
 * @create 2017/5/14
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class KafkaProducerThreadDemo {
    private static final Logger logger = Logger.getLogger(KafkaProducerThreadDemo.class);

    private static final String BOOTSTRAP = "10.21.3.129:9092";
    private static final String TOPICNAME = "test1";
    //多个副本的确认方式
    // 0 不确认，即发送给broker就不管了，也不知道是否成功
    // 1 只有leader确认，不管其他slave
    // 2 leader和所有的slave都确认才算成功，这种方式性能最差，但是数据可靠性最高
    private static final int ACKS = 1;
    private static final int RETRIES = 0;
    private static final int BATCHSIZE = 0;

    private static final int NUMPARTITIONS = 3;

    public static void main(String[] args) {
        // 修改kafka日志输出级别(只针对当前的console)
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        Logger.getLogger("kafka").setLevel(Level.WARN);

        ExecutorService executorService = Executors.newFixedThreadPool(NUMPARTITIONS);
        for (int i = 0; i < NUMPARTITIONS; i++) {
            KafkaProcuderThread kafkaProcuderThread = new KafkaProcuderThread(BOOTSTRAP,ACKS,RETRIES,TOPICNAME);
            executorService.execute(kafkaProcuderThread);
        }


    }
    static class KafkaProcuderThread implements Runnable{

        private KafkaProducer kafkaProducer;
        private String topicName;

        public KafkaProcuderThread(String bootstrap, int acks, int retries,  String topicName) {
            Properties props = new Properties();
            props.put("bootstrap.servers",bootstrap);
            props.put("acks", String.valueOf(acks));
            props.put("retries", String.valueOf(retries));
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            kafkaProducer = new KafkaProducer(props);
            this.topicName = topicName;
        }


        private void pushMsg(){
            try {
                int i = 0;
                while (true){
                    ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicName, String.valueOf(i),"this is message : " + i);
                    kafkaProducer.send(record, new Callback() {
                        //注册回调方法
                        public void onCompletion(RecordMetadata metadata, Exception e) {
                            if (e != null)
                                e.printStackTrace();
//                            System.out.printf("Send record partition:%d, offset:%d, keysize:%d, valuesize:%d %n",
//                                    metadata.partition(), metadata.offset(), metadata.serializedKeySize(),
//                                    metadata.serializedValueSize());
                            System.out.println(Thread.currentThread().getName()+" -- message send to partition " + metadata.partition() + ", offset: " + metadata.offset());
                        }
                    });
                    i++;
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            pushMsg();
        }
    }

}
