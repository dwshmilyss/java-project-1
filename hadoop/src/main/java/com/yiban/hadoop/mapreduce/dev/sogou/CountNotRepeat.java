package com.yiban.hadoop.mapreduce.dev.sogou;

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
import java.util.Iterator;

/**
 * mapreduce
 * 汇总没有重复的数据条数
 * 按照 time uuid keyword url
 */
public class CountNotRepeat {

    public static class CountNotRepeatMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        @Override
        protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException {
            String[] values = value.toString().split("\\s+");
            String time = values[0];//time
            String uid = values[1];//uuid
            String name = values[2];//keyword
            String url = values[5];//url
            context.write(new Text(time+"_"+uid+"_"+name+"_"+url),new IntWritable(1));
        }
    }

    public static class CountNotRepeatReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
//            for (Iterator iterator = values.iterator(); iterator.hasNext(); ) {
//                IntWritable next = (IntWritable) iterator.next();
//                sum += next.get();
//            }
            for (IntWritable val : values) {
                sum += val.get();
            }
            context.write(key, new IntWritable(sum));
            context.getCounter(RecordEnum.CountNotRepeat).increment(1);
            System.out.println("count = " + context.getCounter(RecordEnum.CountNotRepeat).getValue());
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
            job = Job.getInstance(conf, "sogou_count_(time_uuid_keyword_url)_notRepeat");
        } catch (IOException e) {
            e.printStackTrace();
        }

        job.setJarByClass(CountNotRepeat.class);
        job.setMapperClass(CountNotRepeatMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        //因为在reducer中要统计条数，所以这里千万不能设置combiner，不然统计的条数就会翻一倍
//        job.setCombinerClass(CountNotRepeatReducer.class);

        job.setReducerClass(CountNotRepeatReducer.class);
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
            //这里一定要等待任务全部完成才能打印总条数
            if (job.waitForCompletion(true) ? true : false) {
                System.out.println("不重复的条数:  " + job.getCounters().findCounter(RecordEnum.CountNotRepeat).getValue());
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
        runcount("hdfs://gagcluster/data/sogou/ext/sogou.500w.utf8.ext", "hdfs://gagcluster/data/sogou/mapreduce/output/count_notRepeat");
    }
}
