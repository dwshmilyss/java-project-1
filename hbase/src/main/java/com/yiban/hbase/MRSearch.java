package com.yiban.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValueTestUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.MultipleColumnPrefixFilter;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * @auther WEI.DUAN
 * @date 2019/7/23
 * @website http://blog.csdn.net/dwshmilyss
 *  基于hbase1.2.5
 */
public class MRSearch {
    private static final TableName TABLENAME = TableName.valueOf("test1");
    private static final TableName TABLENAME1 = TableName.valueOf("test2");

    private static Configuration getConfiguration() {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.zookeeper.quorum", "10.21.3.73,10.21.3.74,10.21.3.75,10.21.3.76,10.21.3.77");
        configuration.set("hbase.master", "10.21.3.73:60000");
//        configuration.set("hbase.column.max.version", "2");
        return configuration;
    }

    public static class MyTableMapper extends TableMapper<ImmutableBytesWritable, Text>{
        private Text text = new Text();
        @Override
        protected void map(ImmutableBytesWritable rowKey, Result value, Context context) throws IOException, InterruptedException {
            String val = new String(value.getValue(Bytes.toBytes("Family"),
                    Bytes.toBytes("qzcolumn")));
            text.set(val);
            context.write(rowKey,text);
        }
    }

    public static class MyMapper extends Mapper<Object, Text,ImmutableBytesWritable, Text> {
        private Text text = new Text();

        protected void map(ImmutableBytesWritable rowKey, Result value, Context context) throws IOException, InterruptedException {
            String val = new String(value.getValue(Bytes.toBytes("Family"),
                    Bytes.toBytes("qzcolumn")));
            text.set(val);
            context.write(rowKey,text);
        }
    }

    /**
     * 这里不能用@Override修饰 不知道为什么
     * 会报 java.lang.ClassCastException:  Text cannot be cast Mutation
     */
    public static class MyTableRedcuer extends TableReducer<ImmutableBytesWritable, Text, ImmutableBytesWritable> {
//        @Override
        protected void reduce(ImmutableBytesWritable rowKey, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            //reducer逻辑
            for (Text text : values) {
                Put put = new Put(rowKey.get());
                KeyValue kv = KeyValueTestUtil.create(new String(rowKey.get()), "family", "column", 1,
                        text.toString());
                put.add(kv);
                context.write(null, put);
            }
        }
    }

    public static void testTableMapperJob() throws Exception {
        System.setProperty("hadoop.home.dir", "G:\\soft\\hadoop-2.8.2\\hadoop-2.8.2");
        System.setProperty("HADOOP_USER_NAME", "root");
        Configuration conf = getConfiguration();
        conf.set("fs.defaultFS","hdfs://master01:9000");
        conf.set("mapreduce.framework.name","yarn");
        conf.set("mapreduce.job.queuename","rt1");

        conf.addResource("3.73/core-site.xml");
        conf.addResource("3.73/yarn-site.xml");
        conf.addResource("3.73/hdfs-site.xml");
        conf.addResource("3.73/mapred-site.xml");
        conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");

        conf.set("mapreduce.app-submission.cross-platform","true");
        conf.set("mapreduce.job.jar","D:\\source_code\\java-project-1\\out\\artifacts\\hbase_jar\\hbase.jar");

        Connection connection = ConnectionFactory.createConnection(conf);
        Admin admin = connection.getAdmin();
        Table table = connection.getTable(TABLENAME);

        MultipleColumnPrefixFilter filter;
        Scan scan = new Scan();
        scan.setMaxVersions();
        byte[][] filter_prefix = new byte[2][];
        filter_prefix[0] = new byte[]{'p'};
        filter_prefix[1] = new byte[]{'q'};

        filter = new MultipleColumnPrefixFilter(filter_prefix);
        scan.setFilter(filter);
        Job job = Job.getInstance(conf, "testTableMapperJob");
        job.setJarByClass(MRSearch.class);
//        job.setMapperClass(MyMapper.class);
//        job.setMapOutputKeyClass(ImmutableBytesWritable.class);
//        job.setMapOutputValueClass(Text.class);
//        job.setOutputKeyClass(ImmutableBytesWritable.class);
//        job.setOutputValueClass(Put.class);
        TableMapReduceUtil.initTableMapperJob(TABLENAME,scan, MyTableMapper.class, ImmutableBytesWritable.class, Text.class,job);
        Path outPath = new Path("/search_result/testTableMapperJob");
        HdfsTools hdfsTools = new HdfsTools();
        hdfsTools.DelFile(conf, outPath.getName(), true); // 若已存在，则先删除
        FileOutputFormat.setOutputPath(job, outPath);// 输出结果
        TableMapReduceUtil.initTableReducerJob("test_1_copy",MyTableRedcuer.class,job);
        job.setNumReduceTasks(1);

        long startTime = System.currentTimeMillis();
        job.waitForCompletion(true);
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }

    public static void main(String[] args) {
        try {
            testTableMapperJob();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}