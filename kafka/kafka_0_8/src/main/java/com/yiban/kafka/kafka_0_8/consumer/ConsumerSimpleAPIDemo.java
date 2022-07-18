package com.yiban.kafka.kafka_0_8.consumer;

import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.Broker;
import kafka.common.ErrorMapping;
import kafka.common.TopicAndPartition;
import kafka.javaapi.*;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * 低阶API
 *
 * @auther WEI.DUAN
 * @date 2018/8/31
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ConsumerSimpleAPIDemo {

    //broker 副本
    private List<String> m_replicaBrokers = new ArrayList<String>();

    public ConsumerSimpleAPIDemo(List<String> m_replicaBrokers) {
        this.m_replicaBrokers = new ArrayList<String>();
    }


    public static void main(String[] args) {
        // 最大读取消息数量
        long maxReads = Long.parseLong("1");
        // 要订阅的topic
        String topic = "test_10_3";
        // 要查找的分区
        int partition = Integer.parseInt("1");
        // broker节点的ip
        List<String> seeds = new ArrayList<String>();
        seeds.add("10.21.3.74");
        seeds.add("10.21.3.75");
        seeds.add("10.21.3.76");
        seeds.add("10.21.3.77");
        // 端口
        int port = Integer.parseInt("9092");
        ConsumerSimpleAPIDemo example = new ConsumerSimpleAPIDemo(seeds);
        try {
            example.run(maxReads, topic, partition, seeds, port);
        } catch (Exception e) {
            System.out.println("Oops:" + e);
            e.printStackTrace();
        }
    }


    /**
     * 找出哪个代理是您的主题和分区的领导者。
     *
     * @param a_seedBrokers 种子broker(一般设置为所有的broker)
     * @param a_port        端口
     * @param a_topic       主题
     * @param a_partition   分区
     * @return
     */
    private PartitionMetadata findLeader(List<String> a_seedBrokers, int a_port, String a_topic, int a_partition) {
        PartitionMetadata returnMetaData = null;
        loop:
        //遍历所有的种子broker
        for (String host : a_seedBrokers) {
            SimpleConsumer consumer = null;
            try {
                /**
                 * host -> 主机（IP地址或hostname）
                 * a_port -> 端口
                 * timeout -> 100000(超时时间 100秒)
                 * buffer -> 64 KB
                 * clientName -> "leaderLookup"
                 */
                consumer = new SimpleConsumer(host, a_port, 100000, 64 * 1024, "leaderLookup");

                List<String> topics = Collections.singletonList(a_topic);
                TopicMetadataRequest request = new TopicMetadataRequest(topics);
                TopicMetadataResponse response = consumer.send(request);

                //向broker调用topicsMetadata()可以获取到topic的详细信息
                List<TopicMetadata> metadatas = response.topicsMetadata();
                //遍历每个partition的metadata
                for (TopicMetadata item : metadatas) {
                    for (PartitionMetadata part : item.partitionsMetadata()) {
                        // 判断是否是要找的partition
                        if (part.partitionId() == a_partition) {
                            returnMetaData = part;
                            break loop;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error communicating with Broker [" + host + "] to find Leader for [" + a_topic
                        + ", " + a_partition + "] Reason: " + e);
            } finally {
                if (consumer != null) consumer.close();
            }
        }
        if (returnMetaData != null) {
            m_replicaBrokers.clear();
            for (Broker replica : returnMetaData.replicas()) {
                m_replicaBrokers.add(replica.host());
            }
        }
        return returnMetaData;
    }

    /**
     * 找到要开始消费的offset
     *
     * @param consumer   低阶API消费者对象
     * @param topic
     * @param partition
     * @param whichTime  从哪个时间开始消费
     * @param clientName 客户端
     * @return offset
     */
    public static long getLastOffset(SimpleConsumer consumer, String topic, int partition, long whichTime, String clientName) {
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap();
        //PartitionOffsetRequestInfo(whichTime,1) 第二个参数是获取的offset的最大个数，这里只获取1个
        requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(whichTime, 1));
        kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(requestInfo, kafka.api.OffsetRequest.CurrentVersion(), clientName);
        OffsetResponse response = consumer.getOffsetsBefore(request);

        //如果有错误，则offset=0
        if (response.hasError()) {
            System.out.println("Error fetching data Offset Data the Broker. Reason: " + response.errorCode(topic, partition));
            return 0;
        }
        long[] offsets = response.offsets(topic, partition);
        return offsets[0];
    }


    /**
     * 在这里，一旦fetch返回一个错误，我们记录原因，关闭使用者，然后尝试找出谁是新的领导者。
     *
     * @param a_oldLeader
     * @param a_topic
     * @param a_partition
     * @param a_port
     * @return
     * @throws Exception
     */
    private String findNewLeader(String a_oldLeader, String a_topic, int a_partition, int a_port) throws Exception {
        for (int i = 0; i < 3; i++) {
            boolean goToSleep = false;
            //找出leader
            PartitionMetadata metadata = findLeader(m_replicaBrokers, a_port, a_topic, a_partition);
            //metadata为null
            if (metadata == null) {
                goToSleep = true;
            } else if (metadata.leader() == null) {//一旦找不到leader
                goToSleep = true;
            } else if (a_oldLeader.equalsIgnoreCase(metadata.leader().host()) && i == 0) {
                goToSleep = true;
            } else {
                return metadata.leader().host();
            }
            if (goToSleep) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                }
            }
        }
        System.out.println("Unable to find new leader after Broker failure. Exiting");
        throw new Exception("Unable to find new leader after Broker failure. Exiting");
    }

    public void run(long a_maxReads, String a_topic, int a_partition, List<String> a_seedBrokers, int a_port) throws Exception {
        //find the meta data about the topic and partition we are interested in
        PartitionMetadata metadata = findLeader(a_seedBrokers, a_port, a_topic, a_partition);
        if (metadata == null) {
            System.out.println("Can't find metadata for Topic and Partition. Exiting");
            return;
        }
        if (metadata.leader() == null) {
            System.out.println("Can't find Leader for Topic and Partition. Exiting");
            return;
        }
        System.out.println(metadata.leader().host() + " " + metadata.partitionId());
        String leadBroker = metadata.leader().host();
        String clientName = "Client_" + a_topic + "_" + a_partition +"1";
        SimpleConsumer consumer = new SimpleConsumer(leadBroker, a_port, 100000, 64 * 1024, clientName);
        long readOffset = getLastOffset(consumer, a_topic, a_partition, kafka.api.OffsetRequest.EarliestTime(), clientName);
        System.out.println("readOffset = " + readOffset);
        int numErrors = 0;
        while (a_maxReads > 0) {
            if (consumer == null) {
                consumer = new SimpleConsumer(leadBroker, a_port, 100000, 64 * 1024, clientName);
            }
            kafka.api.FetchRequest fetchRequest = new kafka.api.FetchRequestBuilder().clientId(clientName)
                    .addFetch(a_topic, a_partition, readOffset, 100000)// Note: this fetchSize of 100000 might need to be increased if large batches are written to Kafka
                    .build();
            FetchResponse fetchResponse = consumer.fetch(fetchRequest);

            if (fetchResponse.hasError()) {
                numErrors++;
                // Something went wrong!
                short code = fetchResponse.errorCode(a_topic, a_partition);
                System.out.println("Error fetching data from the Broker:" + leadBroker + " Reason: " + code);
                if (numErrors > 5) break;
                if (code == ErrorMapping.OffsetOutOfRangeCode()) {
                    // We asked for an invalid offset. For simple case ask for the last element to reset
                    readOffset = getLastOffset(consumer, a_topic, a_partition, kafka.api.OffsetRequest.LatestTime(), clientName);
                    continue;
                }
                consumer.close();
                consumer = null;
                leadBroker = findNewLeader(leadBroker, a_topic, a_partition, a_port);
                continue;
            }
            numErrors = 0;

            long numRead = 0;


            for (MessageAndOffset messageAndOffset : fetchResponse.messageSet(a_topic, a_partition)) {
                long currentOffset = messageAndOffset.offset();
                if (currentOffset < readOffset) {
                    System.out.println("Found an old offset: " + currentOffset + " Expecting: " + readOffset);
                    continue;
                }
                readOffset = messageAndOffset.nextOffset();
                ByteBuffer payload = messageAndOffset.message().payload();

                byte[] bytes = new byte[payload.limit()];
                payload.get(bytes);
                System.out.println(String.valueOf(messageAndOffset.offset()) + ": " + new String(bytes, "UTF-8"));
                numRead++;
                a_maxReads--;
            }

            if (numRead == 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                }
            }
            while (true){}
        }
        if (consumer != null) consumer.close();
    }

}
