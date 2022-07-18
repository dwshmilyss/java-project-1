package com.yiban.kafka.kafka_0_8.api;

import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.TopicAndPartition;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.consumer.SimpleConsumer;

import java.io.Serializable;
import java.util.*;

/**
 * 利用kafka的低阶API获取一些topic信息。
 * 低阶API可以做：
 *  1、一个消息读取多次
    2、在一个处理过程中只消费Partition其中的一部分消息
    3、添加事务管理机制以保证消息被处理且仅被处理一次
 *
 * @auther WEI.DUAN
 * @date 2017/5/17
 * @website http://blog.csdn.net/dwshmilyss
 */
public class KafkaUtil implements Serializable {
    private static KafkaUtil kafkaUtil = null;

    private KafkaUtil() {
    }

    public static KafkaUtil getInstance() {
        if (kafkaUtil == null) {
            kafkaUtil = new KafkaUtil();
        }
        return kafkaUtil;
    }

    /**
     * 操作字符串，读取其中的broker server IP 封装到数组中
     *
     * @param brokerlist
     * @return
     */
    private String[] getIpsFromBrokerList(String brokerlist) {
        StringBuilder sb = new StringBuilder();
        String[] brokers = brokerlist.split(",");
        for (int i = 0; i < brokers.length; i++) {
            brokers[i] = brokers[i].split(":")[0];
        }
        return brokers;
    }

    /**
     * 操作字符串，读取其中的broker server套接字 封装到map中
     *
     * @param brokerlist
     * @return key:broker Server IP | value: broker server port
     */
    private Map<String, Integer> getPortFromBrokerList(String brokerlist) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        String[] brokers = brokerlist.split(",");
        for (String item : brokers) {
            String[] itemArr = item.split(":");
            if (itemArr.length > 1) {
                map.put(itemArr[0], Integer.parseInt(itemArr[1]));
            }
        }
        return map;
    }

    public KafkaTopicOffset topicMetadataRequest(String brokerlist, String topic, String clientId) {
        List<String> topics = Collections.singletonList(topic);
        TopicMetadataRequest topicMetadataRequest = new TopicMetadataRequest(topics);

        KafkaTopicOffset kafkaTopicOffset = new KafkaTopicOffset(topic);
        String[] seeds = getIpsFromBrokerList(brokerlist);
        Map<String, Integer> portMap = getPortFromBrokerList(brokerlist);

        for (int i = 0; i < seeds.length; i++) {
            SimpleConsumer consumer = null;
            try {
                consumer = new SimpleConsumer(seeds[i],
                        portMap.get(seeds[i]),
                        Constant.TIMEOUT,
                        Constant.BUFFERSIZE,
                        clientId);
                kafka.javaapi.TopicMetadataResponse resp = consumer.send(topicMetadataRequest);
                List<TopicMetadata> metaData = resp.topicsMetadata();
                for (TopicMetadata item : metaData) {
                    for (PartitionMetadata part : item.partitionsMetadata()) {
                        kafkaTopicOffset.getLeaderList().put(part.partitionId(), part.leader().host());
                        kafkaTopicOffset.getOffsetList().put(part.partitionId(), 0L);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (consumer != null) {
                    consumer.close();
                }
            }
        }

        return kafkaTopicOffset;
    }

    public KafkaTopicOffset getLastOffsetByTopic(String brokerlist, String topic, String clientId) {
        KafkaTopicOffset kafkaTopicOffset = topicMetadataRequest(brokerlist, topic, clientId);
        String[] seeds = getIpsFromBrokerList(brokerlist);
        Map<String, Integer> portMap = getPortFromBrokerList(brokerlist);

        for (int i = 0; i < seeds.length; i++) {
            SimpleConsumer consumer = null;
            Iterator iterator = kafkaTopicOffset.getOffsetList().entrySet().iterator();

            try {
                consumer = new SimpleConsumer(seeds[i],
                        portMap.get(seeds[i]),
                        Constant.TIMEOUT,
                        Constant.BUFFERSIZE,
                        clientId
                );

                while (iterator.hasNext()) {
                    Map.Entry<Integer, Long> entry = (Map.Entry<Integer, Long>) iterator.next();
                    int partitonId = entry.getKey();

                    if (!kafkaTopicOffset.getLeaderList().get(partitonId).equals(seeds[i])) {
                        continue;
                    }

                    TopicAndPartition topicAndPartition = new TopicAndPartition(topic,
                            partitonId);
                    Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo =
                            new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();

                    requestInfo.put(topicAndPartition,
                            new PartitionOffsetRequestInfo(kafka.api.OffsetRequest.LatestTime(), 1)
                    );
                    kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(
                            requestInfo, kafka.api.OffsetRequest.CurrentVersion(),
                            clientId);
                    OffsetResponse response = consumer.getOffsetsBefore(request);
                    long[] offsets = response.offsets(topic, partitonId);
                    if (offsets.length > 0) {
                        kafkaTopicOffset.getOffsetList().put(partitonId, offsets[0]);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (consumer != null) {
                    consumer.close();
                }
            }
        }
        return kafkaTopicOffset;
    }


    public KafkaTopicOffset getEarlyOffsetByTopic(String brokerlist, String topic, String clientId) {
        KafkaTopicOffset kafkaTopicOffset = topicMetadataRequest(brokerlist, topic, clientId);
        String[] seeds = getIpsFromBrokerList(brokerlist);
        Map<String, Integer> portMap = getPortFromBrokerList(brokerlist);

        for (int i = 0; i < seeds.length; i++) {
            SimpleConsumer consumer = null;
            Iterator iterator = kafkaTopicOffset.getOffsetList().entrySet().iterator();

            try {
                consumer = new SimpleConsumer(seeds[i],
                        portMap.get(seeds[i]),
                        Constant.TIMEOUT,
                        Constant.BUFFERSIZE,
                        clientId);

                while (iterator.hasNext()) {
                    Map.Entry<Integer, Long> entry = (Map.Entry<Integer, Long>) iterator.next();
                    int partitonId = entry.getKey();

                    if (!kafkaTopicOffset.getLeaderList().get(partitonId).equals(seeds[i])) {
                        continue;
                    }

                    TopicAndPartition topicAndPartition = new TopicAndPartition(topic,
                            partitonId);
                    Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo =
                            new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();

                    requestInfo.put(topicAndPartition,
                            new PartitionOffsetRequestInfo(kafka.api.OffsetRequest.EarliestTime(), 1)
                    );
                    kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(
                            requestInfo, kafka.api.OffsetRequest.CurrentVersion(),
                            clientId);
                    OffsetResponse response = consumer.getOffsetsBefore(request);
                    long[] offsets = response.offsets(topic, partitonId);
                    if (offsets.length > 0) {
                        kafkaTopicOffset.getOffsetList().put(partitonId, offsets[0]);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (consumer != null) {
                    consumer.close();
                }
            }
        }

        return kafkaTopicOffset;
    }

    public Map<String, KafkaTopicOffset> getKafkaOffsetByTopicList(String brokers, List<String> topics, String clientId) {
        Map<String, KafkaTopicOffset> map = new HashMap();
        for (int i = 0; i < topics.size(); i++) {
            map.put(topics.get(i), getLastOffsetByTopic(brokers, topics.get(i), clientId));
        }
        return map;
    }


    public Map<String, KafkaTopicOffset> getKafkaEarlyOffsetByTopicList(String brokerList, List<String> topics, String clientId) {
        Map<String, KafkaTopicOffset> map = new HashMap<String, KafkaTopicOffset>();
        for (int i = 0; i < topics.size(); i++) {
            map.put(topics.get(i), getEarlyOffsetByTopic(brokerList, topics.get(i), clientId));
        }
        return map;
    }

    public static void main(String[] args) {
        String brokers = "10.21.3.74:9092,10.21.3.75:9092,10.21.3.76:9092,10.21.3.77:9092";
        String topicName = "test_10_3";
        String partitionId = String.valueOf(0);
        String clientId = "Client_test_10_3_0";
        try {
//            System.out.println(KafkaUtil.getInstance().getKafkaOffsetByTopicList(brokers, Arrays.asList(new String[]{topicName}), clientId));
//            System.out.println(KafkaUtil.getInstance().getKafkaEarlyOffsetByTopicList(brokers, Arrays.asList(new String[]{topicName}), clientId));
            Map<String, KafkaTopicOffset> currentEarlyestOffsetMap = KafkaUtil.getInstance().getKafkaOffsetByTopicList(brokers, Arrays.asList(new String[]{topicName}), clientId);
            long currentEarlyestOffset = currentEarlyestOffsetMap.get(topicName).getOffsetList().get(Integer.parseInt(partitionId));
            Map<String, KafkaTopicOffset> currentLatestOffsetMap = KafkaUtil.getInstance().getKafkaEarlyOffsetByTopicList(brokers, Arrays.asList(new String[]{topicName}), clientId);
            long currentLatestOffset = currentLatestOffsetMap.get(topicName).getOffsetList().get(Integer.parseInt(partitionId));
            System.out.println("currentEarlyestOffset = " + currentEarlyestOffset + ",||currentLatestOffset = " + currentLatestOffset);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
