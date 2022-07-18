package com.yiban.kafka.kafka_0_8.partition;

import kafka.producer.Partitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2018/8/26 0026.
 */
public class MyPartition implements Partitioner {
    private static Logger LOG = LoggerFactory.getLogger(MyPartition.class);
    public int partition(Object key, int numPartitions) {
        return Math.abs(key.hashCode() % numPartitions);
    }
}
