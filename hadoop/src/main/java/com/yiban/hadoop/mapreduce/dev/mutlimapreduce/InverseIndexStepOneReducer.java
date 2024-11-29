package com.yiban.hadoop.mapreduce.dev.mutlimapreduce;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @author david.duan
 * @packageName com.yiban.hadoop.mapreduce.dev.mutlimapreduce
 * @className InverseIndexStepOneReduce
 * @date 2024/11/28
 * @description
 *  * 	    最终输出结果为：
 *  * 	 	hello-->log_a.txt	3
 *  * 		hello-->log_b.txt	2
 *  * 		hello-->log_c.txt	2
 *  * 		hadoop-->log_a.txt	1
 *  * 		hadoop-->log_b.txt	3
 *  * 		hadoop-->log_c.txt	1
 *  * 		java-->log_a.txt	2
 *  * 		java-->log_b.txt	1
 *  * 		java-->log_c.txt	1
 */
public class InverseIndexStepOneReducer extends Reducer<Text, LongWritable, Text, LongWritable> {
    @Override
    protected void reduce(Text key, Iterable<LongWritable> values, Reducer<Text, LongWritable, Text, LongWritable>.Context context) throws IOException, InterruptedException {
        if (values != null) {
            long sum = 0;
            for (LongWritable val : values) {
                sum += val.get();
            }
            context.write(key, new LongWritable(sum));
        }
    }
}
