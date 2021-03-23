package com.yiban.hadoop.mapreduce.dev;

import java.io.IOException;

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
import org.apache.hadoop.mapreduce.lib.chain.ChainReducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.StringUtils;

public class ChainMapperAndReducerTest {
    static class MapImpl extends Mapper<LongWritable, Text, Text, Text> {

        private Text k = new Text();
        private Text v = new Text();
        static {
            System.out.println("------MapImpl传出去的数据----------");
        }
        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            String[] tokens = StringUtils.split(value.toString(), ' ');
			/*System.out.println("-----------------");
			System.out.println(Arrays.toString(tokens));
			System.out.println("-----------------");*/
            String key1 = tokens[0] + "-map0";
            String val1 = tokens[1] + "-map0";
            k.set(key1);
            v.set(val1);
            System.out.println("mapper1 : key  = " + k.toString() + " , value = " + v.toString());
            context.write(k, v);
        }
    }


    static class MapImpl1 extends Mapper<Text, Text, Text, Text> {
        private Text k = new Text();
        private Text v = new Text();
        static {
            System.out.println("---------MapImpl1的数据----------");
        }
        @Override
        protected void map(Text key, Text value, Context context)
                throws IOException, InterruptedException {
            String ke = key.toString();
            String ke1 = ke + "-map1";
            k.set(ke1);

            String val = value.toString();
            String val1 = val + "-map1";
            v.set(val1);
            System.out.println("mapper1 接收到的数据: key  = " + ke + " , value = " + val);
            System.out.println("mapper1 传递出去的数据: key =  " + k.toString() + ", value =  " + v.toString());
            context.write(k, v);
        }
    }

    static class MapImpl2 extends Mapper<Text, Text, Text, Text> {
        private Text k = new Text();
        private Text v = new Text();
        static {
            System.out.println("---------MapImpl2的数据----------");
        }
        @Override
        protected void map(Text key, Text value, Context context)
                throws IOException, InterruptedException {
            String ke = key.toString();
            String ke1 = ke + "-map2";
            k.set(ke1);

            String val = value.toString();
            String val1 = val + "-map2";
            v.set(val1);
            System.out.println("mapper2 接收到的数据: key  = " + ke + " , value = " + val);
            System.out.println("mapper2 传递出去的数据: key =  " + k.toString() + ", value =  " + v.toString());
            context.write(k, v);
        }
    }

    static class MapImpl3 extends Mapper<Text, Text, Text, Text> {
        private Text k = new Text();
        private Text v = new Text();
        static {
            System.out.println("---------MapImpl3的数据----------");
        }
        @Override
        protected void map(Text key, Text value, Context context)
                throws IOException, InterruptedException {
            String ke = key.toString();
            String ke1 = ke + "-map3";
            k.set(ke1);

            String val = value.toString();
            String val1 = val + "-map3";
            v.set(val1);
            System.out.println("mapper3 接收到的数据: key  = " + ke + " , value = " + val);
            System.out.println("mapper3 传递出去的数据: key =  " + k.toString() + ", value =  " + v.toString());
            context.write(k, v);
        }
    }




    static class MapImpl4 extends Mapper<Text, Text, IntWritable, Text> {
        private IntWritable k = new IntWritable();
        private Text v = new Text();
        @Override
        protected void map(Text key, Text value, Context context)
                throws IOException, InterruptedException {
            k.set(1);
            String val = value.toString();
            String val1 = val + "-redumap1";
            v.set(val);
            context.write(k, v);
        }
    }



    static class ReducerImpl extends Reducer<Text, Text, Text, Text> {
        private Text k = new Text();
        private Text v = new Text();
        static {
            System.out.println("--------Reducer的数据---------");
        }
        @Override
        protected void reduce(Text arg0, Iterable<Text> arg1, Context context)
                throws IOException, InterruptedException {
            System.out.println("reduce接收到的key : " + arg0.toString());
            String key1 = arg0.toString() + "-reduce";
            k.set(key1);
            for(Text text : arg1) {
                String val = text.toString();
                String val1 = val + "-reduce";
                v.set(val1);
                System.out.println("reducer输出的key =  " + k.toString() + " ,value = " + v.toString());
                context.write(k, v);
            }
        }
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        System.setProperty("hadoop.home.dir", "D:\\Program Files\\hadoop-2.8.5");
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = new Configuration();
        conf.addResource("core-site.xml");
        conf.addResource("yarn-site.xml");
        conf.addResource("hdfs-site.xml");
        conf.addResource("mapred-site.xml");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
        conf.set("fs.defaultFS","hdfs://master01:9000");
        conf.set("mapreduce.framework.name","yarn");
        conf.set("mapreduce.job.queuename","rt1");

        conf.set("mapreduce.app-submission.cross-platform","true");
        //这里要指定绝对路径
        //如果这里指定了jar 那么下面的setJarByClass就可以省略
        conf.set("mapreduce.job.jar","D:\\source_code\\java-project-1\\out\\artifacts\\hadoop_jar\\hadoop.jar");


        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length < 2) {
            System.err.println("Usage: wordcount <in> [<in>...] <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf, "ChainMapperAndReducerTest");

        job.setJarByClass(ChainMapperAndReducerTest.class);
        // 先使用ChainMapper设置两个Mapper
        ChainMapper.addMapper(job, MapImpl.class, LongWritable.class, Text.class,
                Text.class, Text.class, conf);
        ChainMapper.addMapper(job, MapImpl1.class, Text.class, Text.class,
                Text.class, Text.class, conf);

        // 然后使用ChainReducer设置reducer
        ChainReducer.setReducer(job, ReducerImpl.class, Text.class, Text.class,
                Text.class, Text.class, conf);

        // 然后使用ChainReducer设置两个Map
        ChainReducer.addMapper(job, MapImpl2.class, Text.class, Text.class,
                Text.class, Text.class, conf);
        ChainReducer.addMapper(job, MapImpl3.class, Text.class, Text.class,
                Text.class, Text.class, conf);



        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        //指定MapReduce解析输入和输出文件的类。TextInputFormat是默认的(一般来讲都是文本文件)。也可以不写
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
