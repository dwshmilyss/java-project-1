package com.yiban.hadoop.mapreduce.dev.sogou;

import org.apache.commons.lang.time.StopWatch;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.*;

/**
 * mapreduce
 * 过滤出查询关键字包含“仙剑奇侠传” ，并且次数大于等于3的UUID
 */
public class UUIDQueriesGreaterThan3 {

    public static class UUIDQueriesGreaterThan3Mapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        @Override
        protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException {
            String[] values = value.toString().split("\\s+");
            String keyword = values[2];//keyword
            String uuid = values[1];//keyword
            if (keyword.contains("仙剑奇侠传")) {
                context.write(new Text(uuid), new IntWritable(1));
            }
        }
    }

    public static class UUIDQueriesGreaterThan3Reducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            //遍历方法2
            for (IntWritable val : values) {
                sum += val.get();
            }
            if (sum >= 3) {
                context.write(key,new IntWritable(sum));
            }
        }
    }


    public static void runcount(String inputPathStr, String outputPathStr) {
        Configuration conf = new Configuration();

        System.setProperty("hadoop.home.dir", "D:\\Program Files\\hadoop-2.8.5");
        System.setProperty("HADOOP_USER_NAME", "root");
        conf.addResource("core-site.xml");
        conf.addResource("yarn-site.xml");
        conf.addResource("hdfs-site.xml");
        conf.addResource("mapred-site.xml");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        conf.set("mapreduce.framework.name", "yarn");
        conf.set("mapreduce.job.queuename", "rt1");

        conf.set("mapreduce.app-submission.cross-platform", "true");
        //这里要指定绝对路径
        //如果这里指定了jar 那么下面的setJarByClass就可以省略
        conf.set("mapreduce.job.jar", "D:\\source_code\\java-project-1\\out\\artifacts\\hadoop_jar\\hadoop.jar");

        conf.set("fs.defaultFS", "hdfs://gagcluster");
//        conf.set("mapreduce.framework.name", "local");
        Job job = null;
        try {
            job = Job.getInstance(conf, "sogou_keyword_top10");
        } catch (IOException e) {
            e.printStackTrace();
        }

        job.setJarByClass(UUIDQueriesGreaterThan3.class);
        job.setMapperClass(UUIDQueriesGreaterThan3Mapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        //全局排序是不能设置combiner的
        job.setCombinerClass(UUIDQueriesGreaterThan3Reducer.class);

        job.setReducerClass(UUIDQueriesGreaterThan3Reducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        try {
            FileInputFormat.addInputPath(job, new Path(inputPathStr));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path outputPath = new Path(outputPathStr);
        try {
            FileSystem fs = FileSystem.get(conf);
            if (fs.exists(outputPath)) {
                fs.delete(outputPath, true);
                System.out.println("存在此输出路径，已删除！！！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputFormat.setOutputPath(job, outputPath);

        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            if (job.waitForCompletion(true) ? true : false) {
                stopWatch.stop();
                System.out.println(stopWatch.getTime());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        runcount("hdfs://gagcluster/data/sogou/ext/sogou.500w.utf8.ext", "hdfs://gagcluster/data/sogou/mapreduce/output/uuid_query_greaterthan3_xianjian");
    }
}
