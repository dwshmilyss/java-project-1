package com.yiban.hadoop.mapreduce.dev.mutlimapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;
/**
 * @author david.duan
 * @packageName com.yiban.hadoop.mapreduce.dev.mutlimapreduce
 * @className InverseIndexRunner
 * @date 2024/11/28
 * @description 倒排索引的执行类
 *
 * 提交任务命令:
 * hadoop  jar /home/hadoop/xxx-1.0-SNAPSHOT.jar com.yiban.hadoop.mapreduce.dev.mutlimapreduce.InverseIndexRunner  /data/input  /data/oneoutput /data/twooutput
 */
public class InverseIndexRunner extends Configured implements Tool {
    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new Configuration(),new InverseIndexRunner(),args);
        System.exit(exitCode);
    }

    @Override
    public int run(String[] args) throws Exception {
        //这里的0和1 类似 return job.waitForCompletion(true) ? 0 : 1
        if (!runStepOneMapReduce(args)) {
            return 1;
        }
        return runStepTwoMapReduce(args) ? 0:1;
    }

    private static boolean runStepOneMapReduce(String[] args) throws Exception {

        Job job = getJob();
        job.setJarByClass(InverseIndexRunner.class);

        job.setMapperClass(InverseIndexStepOneMapper.class);
        job.setReducerClass(InverseIndexStepOneReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        return job.waitForCompletion(true);

    }

    private static boolean runStepTwoMapReduce(String []args) throws Exception {

        Job job = getJob();
        job.setJarByClass(InverseIndexRunner.class);

        job.setMapperClass(InverseIndexStepTwoMapper.class);
        job.setReducerClass(InverseIndexStepTwoReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job,new Path(args[1] + "/part-r-00000"));
        FileOutputFormat.setOutputPath(job,new Path(args[2]));
        return job.waitForCompletion(true);

    }

    private static Job getJob() throws IOException {
        Configuration conf = new Configuration();
        return Job.getInstance(conf);
    }
}
