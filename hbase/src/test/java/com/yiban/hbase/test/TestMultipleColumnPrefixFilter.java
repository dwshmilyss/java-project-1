package com.yiban.hbase.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.ColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.MultipleColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.IncreasingToUpperBoundRegionSplitPolicy;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @auther WEI.DUAN
 * @date 2019/7/19
 * @website http://blog.csdn.net/dwshmilyss
 */
public class TestMultipleColumnPrefixFilter {
    private static final TableName TABLENAME = TableName.valueOf("test1");
    private static final TableName TABLENAME1 = TableName.valueOf("test2");
    private static final String ROOTDIR = "hdfs://gagcluster/hbase";

    @Test
    public void testMultipleColumnPrefixFilter() throws IOException {
        long start = System.currentTimeMillis();
        //创建表
        Configuration configuration = getConfiguration();
//        System.out.println("configuration = " + configuration.get("hbase.column.max.version"));
        Connection connection = ConnectionFactory.createConnection(configuration);
        Admin admin = connection.getAdmin();
        String family = "Family";
        HTableDescriptor hTableDescriptor = new HTableDescriptor(TABLENAME);
        HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(family);
        //因为下面插入数据的时候会有两个版本号，所以这里要设置最大版本号
        hColumnDescriptor.setMaxVersions(2);
        hTableDescriptor.addFamily(hColumnDescriptor);
        if (!admin.tableExists(TABLENAME)) {
            admin.createTable(hTableDescriptor);
        }
        Table table = connection.getTable(TABLENAME);

        List<String> rows = generateRandomWords(100, "row");
        List<String> columns = generateRandomWords(10000, "column");
        //这个变量相当于版本号
        long maxTimestamp = 2;

        //用于存储三个前缀各有多少条数据
        Map<String, List<KeyValue>> prefixMap = new HashMap<String,
                List<KeyValue>>();
        prefixMap.put("p", new ArrayList<KeyValue>());
        prefixMap.put("q", new ArrayList<KeyValue>());
        prefixMap.put("s", new ArrayList<KeyValue>());
        List<Put> putList = new ArrayList<>(500);
        String valueString = "ValueString";
        System.out.println("rows size = " + rows.size());
        //开始插入数据
        for (String row : rows) {
            Put put = new Put(Bytes.toBytes(row));
            for (String column : columns) {
                //每条数据两个版本
                for (long timestamp = 1; timestamp <= maxTimestamp; timestamp++) {
                    KeyValue kv = KeyValueTestUtil.create(row, family, column, timestamp,
                            valueString);
                    put.add(kv);
                    for (String s : prefixMap.keySet()) {
                        if (column.startsWith(s)) {
                            prefixMap.get(s).add(kv);
                        }
                    }
                }
            }
            putList.add(put);
        }
        table.put(putList);

        System.out.println(prefixMap.get("p").size() + " " + prefixMap.get("q").size());
        //8251
        System.out.println(System.currentTimeMillis() - start);
        //定义检索条件 这里按照列名的前缀进行查找
        MultipleColumnPrefixFilter filter;
        Scan scan = new Scan();
        scan.setMaxVersions();
        byte[][] filter_prefix = new byte[2][];
        filter_prefix[0] = new byte[]{'p'};
        filter_prefix[1] = new byte[]{'q'};

        filter = new MultipleColumnPrefixFilter(filter_prefix);
        scan.setFilter(filter);
        List<KeyValue> results = new ArrayList<KeyValue>();
        ResultScanner scanner = table.getScanner(scan);
        for (Result result : scanner) {
            for (KeyValue keyValue : result.raw()) {
                System.out.println("rowkey = " + new String(keyValue.getRow()) + "," + new String(keyValue.getFamily())  + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()) + "version = " + keyValue.getTimestamp());
                results.add(keyValue);
            }
        }
        System.out.println("results size = " + results.size());
        assertEquals(prefixMap.get("p").size() + prefixMap.get("q").size(), results.size());
    }

    private Configuration getConfiguration() {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
//        configuration.set("hbase.zookeeper.quorum", "10.21.3.73,10.21.3.74,10.21.3.75,10.21.3.76,10.21.3.77");
//        configuration.set("hbase.master", "10.21.3.73:60000");
        configuration.set("hbase.zookeeper.quorum", "10.21.3.120,10.21.3.121,10.21.3.122,10.21.3.123,10.21.3.124");
        configuration.set("hbase.master", "10.21.3.120:60000");
//        configuration.set("hbase.column.max.version", "2");
        return configuration;
    }

    @Test
    public void testMultipleColumnPrefixFilterWithManyFamilies() throws IOException {
        Configuration configuration = getConfiguration();
        Connection connection = ConnectionFactory.createConnection(configuration);
        Admin admin = connection.getAdmin();

        String family1 = "Family1";
        String family2 = "Family2";
        HTableDescriptor hTableDescriptor = new HTableDescriptor(TABLENAME1);
        HColumnDescriptor hColumnDescriptor1 = new HColumnDescriptor(family1);
        HColumnDescriptor hColumnDescriptor2 = new HColumnDescriptor(family2);
        hColumnDescriptor1.setMaxVersions(3);
        hColumnDescriptor2.setMaxVersions(3);
        hTableDescriptor.addFamily(hColumnDescriptor1);
        hTableDescriptor.addFamily(hColumnDescriptor2);
        //设置表的region的拆分策略
//        hTableDescriptor.setRegionSplitPolicyClassName("IncreasingToUpperBoundRegionSplitPolicy");
        if (!admin.tableExists(TABLENAME1)) {
            admin.createTable(hTableDescriptor);
        }
        Table table = connection.getTable(TABLENAME1);

        List<String> rows = generateRandomWords(100, "row");
        List<String> columns = generateRandomWords(10000, "column");
        long maxTimestamp = 3;

        List<KeyValue> kvList = new ArrayList<KeyValue>();

        Map<String, List<KeyValue>> prefixMap = new HashMap<String,
                List<KeyValue>>();

        prefixMap.put("p", new ArrayList<KeyValue>());
        prefixMap.put("q", new ArrayList<KeyValue>());
        prefixMap.put("s", new ArrayList<KeyValue>());

        String valueString = "ValueString";

        for (String row : rows) {
            Put p = new Put(Bytes.toBytes(row));
            for (String column : columns) {
                for (long timestamp = 1; timestamp <= maxTimestamp; timestamp++) {
                    double rand = Math.random();
                    KeyValue kv;
                    if (rand < 0.5)
                        kv = KeyValueTestUtil.create(row, family1, column, timestamp,
                                valueString);
                    else
                        kv = KeyValueTestUtil.create(row, family2, column, timestamp,
                                valueString);
                    p.add(kv);
                    kvList.add(kv);
                    for (String s : prefixMap.keySet()) {
                        if (column.startsWith(s)) {
                            prefixMap.get(s).add(kv);
                        }
                    }
                }
            }
            table.put(p);
        }

        MultipleColumnPrefixFilter filter;
        Scan scan = new Scan();
        scan.setMaxVersions();
        byte[][] filter_prefix = new byte[2][];
        filter_prefix[0] = new byte[]{'p'};
        filter_prefix[1] = new byte[]{'q'};

        filter = new MultipleColumnPrefixFilter(filter_prefix);
        scan.setFilter(filter);
        List<KeyValue> results = new ArrayList<KeyValue>();
        ResultScanner scanner = table.getScanner(scan);
        for (Result result : scanner) {
            for (KeyValue keyValue : result.raw()) {
                System.out.println("rowkey = " + new String(keyValue.getRow()) + "," + new String(keyValue.getFamily())  + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()) + "version = " + keyValue.getTimestamp());
                results.add(keyValue);
            }
        }
        System.out.println("results size = " + results.size());
        assertEquals(prefixMap.get("p").size() + prefixMap.get("q").size(), results.size());
    }


    /**
     * 对比 MultipleColumnPrefixFilter vs ColumnPrefixFilter
     * @throws IOException
     */
    @Test
    public void testMultipleColumnPrefixFilterWithColumnPrefixFilter() throws IOException {
        Configuration configuration = getConfiguration();
        Connection connection = ConnectionFactory.createConnection(configuration);
        Table table = connection.getTable(TABLENAME);

        MultipleColumnPrefixFilter multiplePrefixFilter;
        Scan scan1 = new Scan();
        scan1.setMaxVersions();
        scan1.setMaxResultsPerColumnFamily(1);
        PageFilter pageFilter = new PageFilter(10);
        byte[][] filter_prefix = new byte[1][];
        filter_prefix[0] = new byte[]{'p'};

        multiplePrefixFilter = new MultipleColumnPrefixFilter(filter_prefix);
        FilterList filterList1 = new FilterList();
        filterList1.addFilter(pageFilter);//控制返回的条数
        filterList1.addFilter(multiplePrefixFilter);//过滤条件
        scan1.setFilter(filterList1);
//        scan1.setFilter(multiplePrefixFilter);
//        scan1.setFilter(pageFilter);
        List<KeyValue> results1 = new ArrayList<KeyValue>();
        ResultScanner scanner1 = table.getScanner(scan1);
        for (Result result : scanner1) {
            for (KeyValue keyValue : result.raw()) {
                System.out.println("rowkey = " + new String(keyValue.getRow()) + "," + new String(keyValue.getFamily())  + ":" + new String(keyValue.getQualifier()) + " = " + new String(keyValue.getValue()) + ",version = " + keyValue.getTimestamp());
                results1.add(keyValue);
            }
        }
        System.out.println("================================");
        ColumnPrefixFilter singlePrefixFilter;
        Scan scan2 = new Scan();
        scan2.setMaxVersions();
        scan2.setMaxResultsPerColumnFamily(1);
        singlePrefixFilter = new ColumnPrefixFilter(Bytes.toBytes("p"));
        FilterList filterList2 = new FilterList();
        filterList2.addFilter(pageFilter);
        filterList2.addFilter(singlePrefixFilter);
        scan2.setFilter(filterList2);
        scan2.setReversed(true);
//        scan2.setFilter(singlePrefixFilter);
        List<KeyValue> results2 = new ArrayList<KeyValue>();
        ResultScanner scanner2 = table.getScanner(scan2);
        for (Result result : scanner2) {
            for (KeyValue keyValue : result.raw()) {
                System.out.println("rowkey = " + new String(keyValue.getRow()) + "," + new String(keyValue.getFamily())  + ":" + new String(keyValue.getQualifier()) + " = " + new String(keyValue.getValue()) + ",version = " + keyValue.getTimestamp());
                results2.add(keyValue);
            }
        }

        assertEquals(results1.size(), results2.size());
    }

    List<String> generateRandomWords(int numberOfWords, String suffix) {
        Set<String> wordSet = new HashSet<String>();
        for (int i = 0; i < numberOfWords; i++) {
            int lengthOfWords = (int) (Math.random() * 2) + 1;
            char[] wordChar = new char[lengthOfWords];
            for (int j = 0; j < wordChar.length; j++) {
                wordChar[j] = (char) (Math.random() * 26 + 97);
            }
            String word;
            if (suffix == null) {
                word = new String(wordChar);
            } else {
                word = new String(wordChar) + suffix;
            }
            wordSet.add(word);
        }
        List<String> wordList = new ArrayList<String>(wordSet);
        return wordList;
    }
}
