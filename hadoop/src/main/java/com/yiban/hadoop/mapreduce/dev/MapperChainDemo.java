package com.yiban.hadoop.mapreduce.dev;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;
import java.util.StringTokenizer;

public class MapperChainDemo {

    /**
     * 第一个mapper输出<key,1>这样的结构
     */
    public static class Mapper1
            extends Mapper<LongWritable, Text, Text, IntWritable> {
        static {
            System.out.println("---------Mapper1的数据----------");
        }
        @Override
        public void map(LongWritable key, Text value, Context context
        ) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                context.write(new Text(itr.nextToken()), new IntWritable(1));
            }
        }
    }

    /**
     * 第二个mapper输出<key,2>这样的结构 即每个key的value+1
     */
    public static class Mapper2
            extends Mapper<Text, IntWritable, Text, IntWritable> {
        static {
            System.out.println("---------Mapper2的数据----------");
        }
        @Override
        public void map(Text key, IntWritable value, Context context
        ) throws IOException, InterruptedException {
            int oldValue = value.get();
            System.out.println("mapper1.oldValue = " + oldValue);
            int newValue = oldValue + 1;
            System.out.println("mapper1.newValue = " + newValue);
            context.write(key, new IntWritable(newValue));
        }
    }


    static class ReducerImpl extends Reducer<Text, IntWritable, Text, Text> {
        private Text k = new Text();
        private Text v = new Text();
        static {
            System.out.println("--------Reducer的数据---------");
        }
        @Override
        protected void reduce(Text arg0, Iterable<IntWritable> arg1, Context context)
                throws IOException, InterruptedException {
            System.out.println("reduce接收到的key : " + arg0.toString());
            String key1 = arg0.toString() + "-reduce";
            k.set(key1);
            for(IntWritable value : arg1) {
                String val = String.valueOf(value.get());
                String val1 = val + "-reduce";
                v.set(val1);
                System.out.println("reducer输出的key =  " + k.toString() + " ,value = " + v.toString());
                context.write(k, v);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        System.setProperty("hadoop.home.dir", "D:\\Program Files\\hadoop-2.8.5");
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();
        conf.addResource("core-site.xml");
        conf.addResource("yarn-site.xml");
        conf.addResource("hdfs-site.xml");
        conf.addResource("mapred-site.xml");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        conf.set("fs.defaultFS", "hdfs://master01:9000");
        conf.set("mapreduce.framework.name", "yarn");
        conf.set("mapreduce.job.queuename", "rt1");

        conf.set("mapreduce.app-submission.cross-platform", "true");
        //这里要指定绝对路径
        //如果这里指定了jar 那么下面的setJarByClass就可以省略
        conf.set("mapreduce.job.jar", "D:\\source_code\\java-project-1\\out\\artifacts\\hadoop_jar\\hadoop.jar");


        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length < 2) {
            System.err.println("Usage: wordcount <in> [<in>...] <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf, "MapperChainDemo");
        //这里的setJarByClass会有问题，会找不到Mapper类
        //所以最好还是用上面指定mapreduce.job.jar的方式
        job.setJarByClass(MapperChainDemo.class);
        ChainMapper.addMapper(job, Mapper1.class, LongWritable.class, Text.class, Text.class, IntWritable.class, conf);
        ChainMapper.addMapper(job, Mapper2.class, Text.class, IntWritable.class, Text.class, IntWritable.class, conf);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        Path inpath = new Path(args[0]);
        Path outpath = new Path(args[1]);

        FileSystem fs = FileSystem.get(conf);
        if(!fs.exists(inpath)) {
            System.err.println("<error,输入的路径  "+ inpath.getName()+"不存在>");
            System.exit(1);
        }
        if(fs.exists(outpath)) {
            System.err.println("<error,输出路径已存在，正在删除...>");
            fs.delete(outpath,true);
        }
        FileInputFormat.setInputPaths(job, inpath);
        FileOutputFormat.setOutputPath(job, outpath);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}