package com.yiban.hadoop.mapreduce.dev.partitioner;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Partitioner;

import javax.xml.soap.Text;

/**
 * @auther WEI.DUAN
 * @date 2021/3/23
 * @website http://blog.csdn.net/dwshmilyss
 * 数据：
 * 0,hello world
 * 1,hello ketty
 * 2,hello tom
 * 0,hello lyf
 * 0,good morning
 * 2,test
 * 3,33333
 * 需求：把这些数据经过map阶段后，相同数字的输出到同一个分区里。
 */
public class MyOldPartitioner implements Partitioner<IntWritable, Text> {
    @Override
    public int getPartition(IntWritable key, Text value, int numPartitions) {
        int id = key.get();
        switch (id) {
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
        }
        return 0;
    }

    @Override
    public void configure(JobConf jobConf) {

    }
}