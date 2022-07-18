package com.yiban.hadoop.mapreduce.dev.sogou;

import org.apache.commons.lang.StringUtils;
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
 * 过滤出查询次数大于2次的UUID，并查看占比
 */
public class UUIDQueriesGreaterThan2 {

    public static class UUIDQueriesGreaterThan2Mapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        @Override
        protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException {
            String[] values = value.toString().split("\\s+");
            String keyword = values[1];//UUID
            context.write(new Text(keyword),new IntWritable(1));
            context.getCounter("sogou","totalCount").increment(1);
        }
    }

    public static class UUIDQueriesGreaterThan2Reducer extends Reducer<Text, IntWritable, Text, IntWritable> {

        Map<String, Integer> hashMap = new HashMap<String, Integer>(100000);

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            //遍历方法2
            for (IntWritable val : values) {
                sum += val.get();
            }
            if (sum > 2) {
                hashMap.put(key.toString(), sum);
                context.getCounter("sogou","UUIDCountGreaterThan2").increment(1);
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            List<Map.Entry<String, Integer>> sortedList = new ArrayList<Map.Entry<String, Integer>>(hashMap.entrySet());
            Collections.sort(sortedList, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                    return o2.getValue().intValue() - o1.getValue().intValue();
                }
            });
            for (int i = 0; i < 10; i++) {
                context.write(new Text(sortedList.get(i).getKey()),new IntWritable(sortedList.get(i).getValue().intValue()));
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

        job.setJarByClass(UUIDQueriesGreaterThan2.class);
        job.setMapperClass(UUIDQueriesGreaterThan2Mapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        //全局排序是不能设置combiner的
//        job.setCombinerClass(CountTop10Reducer.class);

        job.setReducerClass(UUIDQueriesGreaterThan2Reducer.class);
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
                long total = job.getCounters().findCounter("sogou", "totalCount").getValue();
                long greaterThan2 = job.getCounters().findCounter("sogou","UUIDCountGreaterThan2").getValue();
                System.out.println("greaterThan2 = " + greaterThan2);
                System.out.println("total = " + total);
                double ratio = (double) greaterThan2 / (double) total;
                System.out.println("ratio = " + ratio * 100.0);
                System.out.println("查询超过2次的UUID占总数的 = " + String.format("%.2f",ratio * 100.0) + "%");
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
        runcount("hdfs://gagcluster/data/sogou/ext/sogou.500w.utf8.ext", "hdfs://gagcluster/data/sogou/mapreduce/output/uuid_query_top10");
    }
}
