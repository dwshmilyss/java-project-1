package com.yiban.hadoop.mapreduce.dev.mutlimapreduce;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import java.io.IOException;

/**
 * @author david.duan
 * @packageName com.yiban.hadoop.mapreduce.dev.mutlimapreduce
 * @className InverseIndexStepOneMapper
 * @date 2024/11/28
 * @description
 *  * 读取文件的格式:
 *  * log_a.txt
 *  * hello java
 *  * hello hadoop
 *  * hello java
 *  *
 *  *  倒排索引第一步的Mapper类，
 *  *  输出结果如下：
 *  *  context.wirte("hadoop->log_a.txt", "1")
 *  *  context.wirte("hadoop->log_b.txt", "1")
 *  *  context.wirte("hadoop->log_c.txt", "1")
 *  一般来说输入都是TextInputFormat,那么mapper的第一个参数代表当前行的数据的字节偏移量
 *  例如文件demo.txt，编码为utf-8 那么一个英文字符占一个字节，假如文件中数据如下：
 *  Hello Hadoop
 *  Welcome to MapReduce
 *  Enjoy Learning
 *  那么mapper接收到的数据就是
 *  偏移量  内容
 * 0       Hello Hadoop\n
 * 13      Welcome to MapReduce\n
 * 36      Enjoy Learning
 *
 * 	•	第一行起始字节偏移量是 0。
 * 	•	第二行起始字节偏移量是 13（包括第一行的 12 个字符 + 换行符 \n）。
 * 	•	第三行起始字节偏移量是 36（包括前两行的字符和换行符）。
 */
public class InverseIndexStepOneMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, LongWritable>.Context context) throws IOException, InterruptedException {
        if (value != null) {
            String line = value.toString();//获取一行
            String[] words = line.split(" ");
            if (words.length > 0) {
                FileSplit fileSplit = (FileSplit) context.getInputSplit();
                String fileName = fileSplit.getPath().getName();
                context.write(new Text(words[0]), new LongWritable(1));
                for (String word : words) {
                    context.write(new Text(word + "-->" + fileName),new LongWritable(1));
                }
            }
        }
    }
}
