package com.yiban.javaBase.dev.kafka;

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
 * 工具类
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

    private String[] getIpsFromBrokerList(String brokerlist) {
        StringBuilder sb = new StringBuilder();
        String[] brokers = brokerlist.split(",");
        for (int i = 0; i < brokers.length; i++) {
            brokers[i] = brokers[i].split(":")[0];
        }
        return brokers;
    }

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

    public Map<String, KafkaTopicOffset> getKafkaOffsetByTopicList(String brokers, List<String> topics, String clientId) {
        Map<String, KafkaTopicOffset> map = new HashMap();
        for (int i = 0; i < topics.size(); i++) {
            map.put(topics.get(i), getLastOffsetByTopic(brokers, topics.get(i), clientId));
        }
        return map;
    }

    public static void main(String[] args) {
        String brokers = "10.21.3.129:9092";
        String topicName = "test";
        String partitionId = String.valueOf(0);
        String clientId = "Client_".concat(topicName).concat("_").concat(partitionId);
        try {
            System.out.println(KafkaUtil.getInstance().getKafkaOffsetByTopicList(brokers, Arrays.asList(new String[]{topicName}), clientId));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
