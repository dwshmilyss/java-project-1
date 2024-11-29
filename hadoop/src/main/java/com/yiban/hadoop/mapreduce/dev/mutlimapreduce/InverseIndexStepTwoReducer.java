package com.yiban.hadoop.mapreduce.dev.mutlimapreduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @author david.duan
 * @packageName com.yiban.hadoop.mapreduce.dev.mutlimapreduce
 * @className InverseIndexTwoStepReduce
 * @date 2024/11/28
 *  * @Description: 完成倒排索引第二步的Reducer程序
 *  * 得到的输入信息格式为:
 *  * <"hello", {"log_a.txt->3", "log_b.txt->2", "log_c.txt->2"}>
 *  * 	 * 最终输出结果如下：
 *  * 	 *  hello	log_c.txt-->2 log_b.txt-->2 log_a.txt-->3
 *  * 		hadoop	log_c.txt-->1 log_b.txt-->3 log_a.txt-->1
 *  * 		java	log_c.txt-->1 log_b.txt-->1 log_a.txt-->2
 */
public class InverseIndexStepTwoReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Reducer<Text, Text, Text, Text>.Context context) throws IOException, InterruptedException {
        if (values != null){
            String result = "";
            for (Text value : values) {
                result = result.concat(value.toString()).concat(" ");
            }
            context.write(key,new Text(result));
        }
    }
}
