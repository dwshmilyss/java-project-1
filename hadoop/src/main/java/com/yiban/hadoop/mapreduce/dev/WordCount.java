package com.yiban.hadoop.mapreduce.dev;

import java.io.IOException;
import java.util.StringTokenizer;

import com.yiban.hadoop.mapreduce.dev.partitioner.MyPartitioner;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class WordCount {

    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, IntWritable>{

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        @Override
        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                context.write(word, one);
            }
        }
    }

    public static class IntSumReducer
            extends Reducer<Text,IntWritable,Text,IntWritable> {
        private IntWritable result = new IntWritable();

        @Override
        public void reduce(Text key, Iterable<IntWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
//        Configuration conf = new Configuration();
//        conf.set("mapreduce.app-submission.cross-platform","true");
        //这里要指定绝对路径
        //如果这里指定了jar 那么下面的setJarByClass就可以省略
//        conf.set("mapreduce.job.jar","D:\\source_code\\java-project-1\\out\\artifacts\\hadoop_jar\\hadoop.jar");

        //in有多个路径的时候
//        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (args.length < 2) {
            System.err.println("Usage: wordcount <in> [<in>...] <out>");
            System.exit(2);
        }
        System.out.println("S3AFileSystem.DEFAULT_BLOCKSIZE -> " + S3AFileSystem.DEFAULT_BLOCKSIZE);
        Job job = Job.getInstance();
        job.setJobName("wordcount");
        //这里的setJarByClass会有问题，会找不到Mapper类
        //所以最好还是用上面指定mapreduce.job.jar的方式
        job.setJarByClass(WordCount.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        //设置自己的partitioner
//        job.setPartitionerClass(MyPartitioner.class);
        job.setReducerClass(IntSumReducer.class);
        //设置reducer输出数据<K,V>的类型，因为mapper的输出类型和reducer是一致的(都是<Text,IntWritable>)，所以都用这个指定就可以了
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        //但是如果mapper和reducer的输出类型不一致，例如mapper输出<Text,Text>，reducer输出<Text,IntWritable>，这时候需要单独给mapper指定
        // 而且如果mapper和reducer的输出类型不一致，是不能setCombinerClass()指定为reducer的。道理很简单，combiner也是在mapper中执行的
        // 如果指定的类型不一致，还是会报错。如果真想指定combiner，也要指定和mapper一样的<K,V>类型
//        job.setMapOutputKeyClass(Text.class);
//        job.setMapOutputValueClass(Text.class);
        System.out.println("args = " + args[0]);
        FileInputFormat.addInputPath(job, new Path(args[1]));
        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}