package com.yiban.hadoop.mapreduce.dev.mutlimapreduce;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @author david.duan
 * @packageName com.yiban.hadoop.mapreduce.dev.mutlimapreduce
 * @className InverseIndexTwoStepMapper
 * @date 2024/11/28
 * @description 倒排索引第二步 mapper
 *  * * 	    hello-->log_a.txt	3
 *  *  		hello-->log_b.txt	2
 *  *  		hello-->log_c.txt	2
 *  *  		hadoop-->log_a.txt	1
 *  *  		hadoop-->log_b.txt	3
 *  *  		hadoop-->log_c.txt	1
 *  *  		java-->log_a.txt	2
 *  *  		java-->log_b.txt	1
 *  *  		java-->log_c.txt	1
 *  *
 *  *  输出的信息为：
 *  * 	context.write("hadoop", "log_a.txt->1")
 *  *  context.write("hadoop", "log_b.txt->3")
 *  *  context.write("hadoop", "log_c.txt->1")
 *  *
 *  *  context.write("hello", "log_a.txt->3")
 *  *  context.write("hello", "log_b.txt->2")
 *  *  context.write("hello", "log_c.txt->2")
 *  *
 *  *  context.write("java", "log_a.txt->2")
 *  *  context.write("java", "log_b.txt->1")
 *  *  context.write("java", "log_c.txt->1")
 */
public class InverseIndexStepTwoMapper extends Mapper<LongWritable, Text, Text, Text> {

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context) throws IOException, InterruptedException {
        if (value != null) {
            String line = value.toString();
            // 将第一步的Reduce输出结果按照 \t 拆分
            String[] fields = line.split("\t");
            // 将拆分后的结果数组的第一个元素再按照 --> 分隔
            String[] wordAndFileName = fields[0].split("-->");
            // 获取到单词
            String word = wordAndFileName[0];
            // 获取到文件名
            String fileName = wordAndFileName[1];
            // 获取到单词数量
            long count = Long.parseLong(fields[1]);
            context.write(new Text(word),new Text(fileName + "-->" + count));
        }
    }
}
