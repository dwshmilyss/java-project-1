package com.yiban.kafka.kafka_0_10.partitioner;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * 自定义kafka的Partitioner
 *
 * @auther WEI.DUAN
 * @date 2017/5/22
 * @website http://blog.csdn.net/dwshmilyss
 */
public class MyPartitioner implements Partitioner {
    private static Logger LOG = Logger.getLogger(MyPartitioner.class);

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        int partitionNum = 0;
        try {
            //如果是整型就直接赋值
            partitionNum = Integer.parseInt((String) key);
        } catch (Exception e) {
            //如果是字符串就取hashCode
            partitionNum = key.hashCode();
        }
        LOG.info("the message sendTo topic:" + topic + " and the partitionNum:" + partitionNum);
        //取模的绝对值
        return Math.abs(partitionNum % numPartitions);
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
