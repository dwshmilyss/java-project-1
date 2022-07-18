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
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * @auther WEI.DUAN
 * @date 2021/3/20
 * @website http://blog.csdn.net/dwshmilyss
 */
public class TopN {

    /**
     * Logger
     **/
    private static final Logger LOGGER = LoggerFactory.getLogger(TopN.class);

    /**
     * 找出重复次数最多的topN(mapper)
     */
    public static class SumTopNMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

        @Override
        public void run(Context context) throws IOException, InterruptedException {
            super.run(context);
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            super.cleanup(context);
        }

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer stringTokenizer = new StringTokenizer(value.toString());
            while (stringTokenizer.hasMoreTokens()) {
                context.write(new Text(stringTokenizer.nextToken()), new IntWritable(1));
            }
        }
    }

    /**
     * 找出重复次数最多的topN(reducer)
     */
    public static class TopNReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        Map<Text, Integer> map = new HashMap();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
        }

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            map.put(key, sum);
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            List<Map.Entry<Text, Integer>> list = new ArrayList<>(map.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<Text, Integer>>() {
                @Override
                public int compare(Map.Entry<Text, Integer> o1, Map.Entry<Text, Integer> o2) {
                    return (o2.getValue().intValue() - o1.getValue().intValue());
                }
            });
            for (int i = 0; i < 10; i++) {
                Map.Entry<Text, Integer> entry = list.get(i);
                context.write(entry.getKey(), new IntWritable(entry.getValue().intValue()));
            }
        }

        @Override
        public void run(Context context) throws IOException, InterruptedException {
            super.run(context);
        }
    }

    /**
     * 运行获取重复条目最多的top n
     *
     * @param inputPath
     * @param outputPath
     * @throws InterruptedException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void runSumTopN(String inputPath, String outputPath) throws InterruptedException, IOException, ClassNotFoundException {
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
//        conf.set("mapreduce.job.queuename","rt1");

        conf.set("mapreduce.app-submission.cross-platform", "true");
        //这里要指定绝对路径
        //如果这里指定了jar 那么下面的setJarByClass就可以省略
        conf.set("mapreduce.job.jar", "D:\\source_code\\java-project-1\\out\\artifacts\\hadoop_jar\\hadoop.jar");


        Job job = Job.getInstance(conf, "TopN");
        //这里的setJarByClass会有问题，会找不到Mapper类
        //所以最好还是用上面指定mapreduce.job.jar的方式
//        job.setJarByClass(WordCount.class);
        job.setMapperClass(SumTopNMapper.class);
        job.setCombinerClass(TopNReducer.class);
        job.setReducerClass(TopNReducer.class);
        //设置reducer输出数据<K,V>的类型，因为mapper的输出类型和reducer是一致的(都是<Text,IntWritable>)，所以都用这个指定就可以了
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        //但是如果mapper和reducer的输出类型不一致，例如mapper输出<Text,Text>，reducer输出<Text,IntWritable>，这时候需要单独给mapper指定
        // 而且如果mapper和reducer的输出类型不一致，是不能setCombinerClass()指定为reducer的。道理很简单，combiner也是在mapper中执行的
        // 如果指定的类型不一致，还是会报错。如果真想指定combiner，也要指定和mapper一样的<K,V>类型
//        job.setMapOutputKeyClass(Text.class);
//        job.setMapOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(inputPath));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }


    /**
     * 按某一列统计TOP N
     * key 1
     * value 3
     * aa 4
     * deng 5
     * haha 8
     * tt 8
     */
    public static class GetTopNMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        TreeMap<Integer, String> treeMap = new TreeMap<>();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] strings = value.toString().split(" ");
            //treemap自动按key升序排序，这里把value设为key，让其自动排序
            treeMap.put(new Integer(strings[1]), strings[0]);
            //一旦发现map元素超过10个，则移除最前面的key
            if (treeMap.size() > 10) {
                treeMap.remove(treeMap.firstKey());
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            //在mapper结束时，把treemap中的数据发送给reducer
            for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
                context.write(new Text(entry.getValue()), new IntWritable(entry.getKey().intValue()));
            }
        }
    }

    /**
     * 按某一列统计TOP N
     * 这里只能设置一个reducer
     */
    public static class GetTopNReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        TreeMap<Integer, String> treeMap = new TreeMap<>();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            //到reducer中再一次排序
            treeMap.put(values.iterator().next().get(), key.toString());
            if (treeMap.size() > 10) {
                treeMap.remove(treeMap.firstKey());
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            //在reducer结束时，把treemap中的数据写入HDFS
            for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
                context.write(new Text(entry.getValue()), new IntWritable(entry.getKey().intValue()));
            }
        }
    }

    /**
     * 运行按某列排序获取top n
     *
     * @param inPutPathString
     * @param outPutPathString
     * @throws InterruptedException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void runGetTopN(String inPutPathString, String outPutPathString) throws InterruptedException, IOException, ClassNotFoundException {
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
//        conf.set("mapreduce.job.queuename","rt1");

        conf.set("mapreduce.app-submission.cross-platform", "true");
        //这里要指定绝对路径
        //如果这里指定了jar 那么下面的setJarByClass就可以省略
        conf.set("mapreduce.job.jar", "D:\\source_code\\java-project-1\\out\\artifacts\\hadoop_jar\\hadoop.jar");


        Job job = Job.getInstance(conf, "TopN");
        //这里的setJarByClass会有问题，会找不到Mapper类
        //所以最好还是用上面指定mapreduce.job.jar的方式
//        job.setJarByClass(WordCount.class);
        job.setMapperClass(GetTopNMapper.class);
        job.setReducerClass(GetTopNReducer.class);
        //设置reducer输出数据<K,V>的类型，因为mapper的输出类型和reducer是一致的(都是<Text,IntWritable>)，所以都用这个指定就可以了
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        //但是如果mapper和reducer的输出类型不一致，例如mapper输出<Text,Text>，reducer输出<Text,IntWritable>，这时候需要单独给mapper指定
        // 而且如果mapper和reducer的输出类型不一致，是不能setCombinerClass()指定为reducer的。道理很简单，combiner也是在mapper中执行的
        // 如果指定的类型不一致，还是会报错。如果真想指定combiner，也要指定和mapper一样的<K,V>类型
//        job.setMapOutputKeyClass(Text.class);
//        job.setMapOutputValueClass(Text.class);
        FileSystem fileSystem = FileSystem.get(conf);
        Path inPutPath = new Path(inPutPathString);
        if (!fileSystem.exists(inPutPath)) {
            LOGGER.error("input path does not exists,exit");
            System.exit(-1);
        }
        FileInputFormat.addInputPath(job, inPutPath);
        Path outPutPath = new Path(inPutPathString);
        if (fileSystem.exists(outPutPath)) {
            LOGGER.warn("输出目录存在，需要先删除！");
            fileSystem.delete(outPutPath, true);
        }
        FileOutputFormat.setOutputPath(job, outPutPath);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        if (args.length != 2) {
            LOGGER.error("参数个数错误");
            System.exit(2);
        }
        runSumTopN(args[0], args[1]);
//        runGetTopN(args[0],args[1]);
    }
}