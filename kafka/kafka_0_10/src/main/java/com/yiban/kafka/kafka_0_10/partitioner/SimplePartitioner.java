package com.yiban.kafka.kafka_0_10.partitioner;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * @auther WEI.DUAN
 * @date 2018/8/21
 * @website http://blog.csdn.net/dwshmilyss
 */
public class SimplePartitioner implements Partitioner {
    public SimplePartitioner(VerifiableProperties props) {
    }

    @Override
    public int partition(Object key, int numPartitions) {
        int partition = 0;
        String k = (String)key;
        partition = Math.abs(k.hashCode()) % numPartitions;
        return partition;
    }
}
