package com.yiban.hadoop.mapreduce.dev.partitioner;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

/**
 * @auther WEI.DUAN
 * @date 2021/3/23
 * @website http://blog.csdn.net/dwshmilyss
 */
public class MyPartitioner extends Partitioner<Text, IntWritable> {
    @Override
    public int getPartition(Text key, IntWritable intWritable, int numPartitions) {
        //TODO
        return 0;
    }
}