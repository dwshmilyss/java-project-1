package com.yiban.kafka.kafka_0_8.api;


import kafka.network.BlockingChannel;
import org.apache.log4j.Level;
import kafka.api.ConsumerMetadataRequest;
import kafka.cluster.Broker;
import kafka.common.ErrorMapping;
import kafka.common.OffsetMetadataAndError;
import kafka.common.TopicAndPartition;
import kafka.javaapi.ConsumerMetadataResponse;
import kafka.javaapi.OffsetFetchRequest;
import kafka.javaapi.OffsetFetchResponse;
import kafka.network.BlockingChannel;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.ArrayList;
import java.util.List;

/**
 * 查看kafka offset
 *
 * @auther WEI.DUAN
 * @date 2017/5/15
 * @website http://blog.csdn.net/dwshmilyss
 */
public class KafkaOffsetTools {

    static {
        Logger.getLogger("org").setLevel(Level.WARN);
        Logger.getLogger("akka").setLevel(Level.WARN);
        Logger.getLogger("kafka").setLevel(Level.WARN);
    }

    public static void main(String[] args) {
        getOffset();
    }

    public static void getOffset() {
        BlockingChannel channel = new BlockingChannel("10.21.3.74", 9092,
                BlockingChannel.UseDefaultBufferSize(),
                BlockingChannel.UseDefaultBufferSize(),
                5000 /* read timeout in millis */);
        try {
            channel.connect();
            final String MY_GROUP = "test";
            final String MY_CLIENTID = "none";
            int correlationId = 0;
            final TopicAndPartition testPartition0 = new TopicAndPartition("test_10_3", 0);
//            final TopicAndPartition testPartition1 = new TopicAndPartition("demoTopic", 1);
            channel.send(new ConsumerMetadataRequest(MY_GROUP, ConsumerMetadataRequest.CurrentVersion(), correlationId++, MY_CLIENTID));
            ConsumerMetadataResponse metadataResponse = ConsumerMetadataResponse.readFrom(channel.receive().buffer());

            if (metadataResponse.errorCode() == ErrorMapping.NoError()) {
                Broker offsetManager = metadataResponse.coordinator();
                // if the coordinator is different, from the above channel's host then reconnect
                channel.disconnect();
                channel = new BlockingChannel(offsetManager.host(), offsetManager.port(),
                        BlockingChannel.UseDefaultBufferSize(),
                        BlockingChannel.UseDefaultBufferSize(),
                        5000 /* read timeout in millis */);
                channel.connect();

                List<TopicAndPartition> partitions = new ArrayList<TopicAndPartition>();
                partitions.add(testPartition0);
                OffsetFetchRequest fetchRequest = new OffsetFetchRequest(
                        MY_GROUP,
                        partitions,
                        (short) 1 /* version */, // version 1 and above fetch from Kafka, version 0 fetches from ZooKeeper
                        correlationId,
                        MY_CLIENTID);

                channel.send(fetchRequest.underlying());
                OffsetFetchResponse fetchResponse = OffsetFetchResponse.readFrom(channel.receive().buffer());
                OffsetMetadataAndError result = fetchResponse.offsets().get(testPartition0);
                short offsetFetchErrorCode = result.error();
                if (offsetFetchErrorCode == ErrorMapping.NotCoordinatorForConsumerCode()) {
                    channel.disconnect();
                    // Go to step 1 and retry the offset fetch
                } else if (offsetFetchErrorCode == ErrorMapping.OffsetsLoadInProgressCode()) {
                    // retry the offset fetch (after backoff)
                } else {
                    long retrievedOffset = result.offset();
                    System.out.println("retrievedOffset = "+retrievedOffset);
                    String retrievedMetadata = result.metadata();
                    System.out.println("retrievedMetadata = "+retrievedMetadata);
                }
            } else {
                // retry (after backoff)
            }
        } catch (Exception e) {
            // retry the query (after backoff)
            e.printStackTrace();
            // Go to step 1 and then retry offset fetch after backoff
            channel.disconnect();
        }


    }
}
