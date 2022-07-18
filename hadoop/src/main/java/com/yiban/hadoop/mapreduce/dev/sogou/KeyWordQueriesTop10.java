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
 * 按照 keyword 查询频度排名（频度最高的前10的keyword）
 */
public class KeyWordQueriesTop10 {

    public static class CountTop10Mapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        @Override
        protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException {
            String[] values = value.toString().split("\\s+");
            String keyword = values[2];//keyword
            context.write(new Text(keyword),new IntWritable(1));
        }
    }

    public static class CountTop10Reducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        //升序
        TreeMap<Integer,String> treeMapASC = new TreeMap<Integer,String>();

        //降序
        TreeMap<Integer,String> treeMapDESC = new TreeMap<Integer,String>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2 - o1;
            }
        });

        //降序2
        Map<String, Integer> hashMap = new HashMap<String, Integer>(100000);

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            //遍历方法1
//            for (Iterator iterator = values.iterator(); iterator.hasNext(); ) {
//                IntWritable next = (IntWritable) iterator.next();
//                sum += next.get();
//            }
            //遍历方法2
            for (IntWritable val : values) {
                sum += val.get();
            }
            //排序1
//            treeMapDESC.put(sum, key.toString());
//            //只保留词频最高的前10条
//            if (treeMapDESC.size() > 10) {
//                treeMapDESC.remove(treeMapDESC.lastKey());
//            }
            //排序2
            hashMap.put(key.toString(), sum);
        }

        //cleanup方法只执行一次，所以对于reduce分组之后再进行的操作，要在这里实现业务逻辑。经典的就是topN的问题
        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
//            for (Map.Entry<Integer, String> entry:
//                    treeMapDESC.entrySet()){
//                context.write(new Text(entry.getValue()),new IntWritable(entry.getKey().intValue()));
//            }
            //降序2的实现
            List<Map.Entry<String, Integer>> sortedList = new ArrayList<Map.Entry<String, Integer>>(hashMap.entrySet());
            Collections.sort(sortedList, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().intValue() - o1.getValue().intValue();
                }
            });
            for (Map.Entry<String,Integer> entry:
                    sortedList.subList(0,10)){
                context.write(new Text(entry.getKey()),new IntWritable(entry.getValue().intValue()));
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

        job.setJarByClass(KeyWordQueriesTop10.class);
        job.setMapperClass(CountTop10Mapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        //全局排序是不能设置combiner的
//        job.setCombinerClass(CountTop10Reducer.class);

        job.setReducerClass(CountTop10Reducer.class);
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
        runcount("hdfs://gagcluster/data/sogou/ext/sogou.500w.utf8.ext", "hdfs://gagcluster/data/sogou/mapreduce/output/keyword_top10");
    }
}
