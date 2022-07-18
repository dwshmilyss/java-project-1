package com.yiban.hadoop.mapreduce.dev.sogou;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * mapreduce
 * 查找非空关键字的条数
 */
public class CountKeyWordNotEmpty {

    public static class KeyWordNotEmptyMapper extends Mapper<LongWritable, Text, Text, Text> {
        @Override
        protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context) {
            // 这里是判断错误的数据条数，当然也可以利用contains实现其他功能。例如包含某个字符串的数据条数
            String[] valueArray = value.toString().split("\\s+");
            if (valueArray[2] != null && !"".equals(valueArray[2]) ) {
                context.getCounter(RecordEnum.KeyWordNotEmpty).increment(1);
            }
        }
    }

    public static void count(String inputPathStr,String outputPathStr) {
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
            job = Job.getInstance(conf, "sogou_keyword_notEmpty_count");
        } catch (IOException e) {
            e.printStackTrace();
        }
        job.setJarByClass(CountKeyWordNotEmpty.class);
        job.setMapperClass(KeyWordNotEmptyMapper.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

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
            //这里一定要等待任务全部完成才能打印总条数
            if (job.waitForCompletion(true) ? true : false) {
                System.out.println("非空查询关键字的条数:  " + job.getCounters().findCounter(RecordEnum.KeyWordNotEmpty).getValue());
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
        count("hdfs://gagcluster/data/sogou/ext/sogou.500w.utf8.ext","hdfs://gagcluster/data/sogou/mapreduce/output/keyword_notEmpty_count");
    }
}
