package com.yiban.hbase.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.ColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.MultipleColumnPrefixFilter;
import org.apache.hadoop.hbase.regionserver.HRegion;
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
    private static final String ROOTDIR = "hdfs://gagcluster/hbase";

    @Test
    public void testMultipleColumnPrefixFilter() throws IOException {
        Configuration configuration = getConfiguration();
//        System.out.println("configuration = " + configuration.get("hbase.column.max.version"));
        Connection connection = ConnectionFactory.createConnection(configuration);
        Admin admin = connection.getAdmin();
        String family = "Family";
        HTableDescriptor hTableDescriptor = new HTableDescriptor(TABLENAME);
        HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(family);
        hColumnDescriptor.setMaxVersions(2);
        hTableDescriptor.addFamily(hColumnDescriptor);
        if (!admin.tableExists(TABLENAME)) {
            admin.createTable(hTableDescriptor);
        }

        Table table = connection.getTable(TABLENAME);

        List<String> rows = generateRandomWords(100, "row");
        List<String> columns = generateRandomWords(10000, "column");
        long maxTimestamp = 2;

        Map<String, List<KeyValue>> prefixMap = new HashMap<String,
                List<KeyValue>>();

        prefixMap.put("p", new ArrayList<KeyValue>());
        prefixMap.put("q", new ArrayList<KeyValue>());
        prefixMap.put("s", new ArrayList<KeyValue>());

        String valueString = "ValueString";
        System.out.println("rows size = " + rows.size());
        for (String row : rows) {
            Put put = new Put(Bytes.toBytes(row));
            for (String column : columns) {
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
            table.put(put);
        }

        System.out.println(prefixMap.get("p").size() + " " + prefixMap.get("q").size());

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
        configuration.set("hbase.zookeeper.quorum", "10.21.3.73,10.21.3.74,10.21.3.75,10.21.3.76,10.21.3.77");
        configuration.set("hbase.master", "10.21.3.73:60000");
//        configuration.set("hbase.column.max.version", "2");
        return configuration;
    }

    @Test
    public void testMultipleColumnPrefixFilterWithManyFamilies() throws IOException {
        String family1 = "Family1";
        String family2 = "Family2";
        HTableDescriptor htd = new HTableDescriptor(TABLENAME);
        htd.addFamily(new HColumnDescriptor(family1));
        htd.addFamily(new HColumnDescriptor(family2));
        HRegionInfo info = new HRegionInfo(htd.getTableName(), null, null, false);
        HRegion region = HRegion.createHRegion(info, new Path(ROOTDIR), getConfiguration(), htd);

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
            region.put(p);
        }

        MultipleColumnPrefixFilter filter;
        Scan scan = new Scan();
        scan.setMaxVersions();
        byte[][] filter_prefix = new byte[2][];
        filter_prefix[0] = new byte[]{'p'};
        filter_prefix[1] = new byte[]{'q'};

        filter = new MultipleColumnPrefixFilter(filter_prefix);
        scan.setFilter(filter);
        List<Cell> results = new ArrayList<Cell>();
        InternalScanner scanner = region.getScanner(scan);
        while (scanner.next(results)) ;
        assertEquals(prefixMap.get("p").size() + prefixMap.get("q").size(), results.size());
    }

    @Test
    public void testMultipleColumnPrefixFilterWithColumnPrefixFilter() throws IOException {
        String family = "Family";
        HTableDescriptor htd = new HTableDescriptor(TABLENAME);
        htd.addFamily(new HColumnDescriptor(family));
        HRegionInfo info = new HRegionInfo(htd.getTableName(), null, null, false);
        HRegion region = HRegion.createHRegion(info, new Path(ROOTDIR), getConfiguration(), htd);

        List<String> rows = generateRandomWords(100, "row");
        List<String> columns = generateRandomWords(10000, "column");
        long maxTimestamp = 2;

        String valueString = "ValueString";

        for (String row : rows) {
            Put p = new Put(Bytes.toBytes(row));
            for (String column : columns) {
                for (long timestamp = 1; timestamp <= maxTimestamp; timestamp++) {
                    KeyValue kv = KeyValueTestUtil.create(row, family, column, timestamp,
                            valueString);
                    p.add(kv);
                }
            }
            region.put(p);
        }

        MultipleColumnPrefixFilter multiplePrefixFilter;
        Scan scan1 = new Scan();
        scan1.setMaxVersions();
        byte[][] filter_prefix = new byte[1][];
        filter_prefix[0] = new byte[]{'p'};

        multiplePrefixFilter = new MultipleColumnPrefixFilter(filter_prefix);
        scan1.setFilter(multiplePrefixFilter);
        List<Cell> results1 = new ArrayList<Cell>();
        InternalScanner scanner1 = region.getScanner(scan1);
        while (scanner1.next(results1)) ;

        ColumnPrefixFilter singlePrefixFilter;
        Scan scan2 = new Scan();
        scan2.setMaxVersions();
        singlePrefixFilter = new ColumnPrefixFilter(Bytes.toBytes("p"));

        scan2.setFilter(singlePrefixFilter);
        List<Cell> results2 = new ArrayList<Cell>();
        InternalScanner scanner2 = region.getScanner(scan1);
        while (scanner2.next(results2)) ;

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
