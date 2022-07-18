package com.yiban.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * demo1
 *
 * @auther WEI.DUAN
 * @date 2017/5/26
 * @website http://blog.csdn.net/dwshmilyss
 */
public class HbaseOldAPI {
    public static Configuration configuration = null;
    public static Admin admin = null;
    public static Connection connection = null;

    static {
        String zklist = "10.21.3.73,10.21.3.74,10.21.3.75,10.21.3.76,10.21.3.77";
        //建立hbase连接
        configuration = HBaseConfiguration.create(); // 获得配置文件对象
        configuration.set("hbase.zookeeper.quorum", zklist);
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
//        configuration.set("hbase.master", "10.21.3.73:60000");
//        configuration.set("zookeeper.session.timeout", "10000");
//        configuration.set("hbase.rootdir", "hdfs://master01:9000/hbase");
//        configuration.addResource("hbase-site.xml");
//        configuration.addResource("core-site.xml");
//        configuration.addResource("hdfs-site.xml");

//        configuration.addResource(new Path(System.getenv("HBASE_CONF_DIR"), "hbase-site.xml"));
//        configuration.addResource(new Path(System.getenv("HADOOP_CONF_DIR"), "core-site.xml"));
//        configuration.addResource(new Path(System.getenv("HADOOP_CONF_DIR"), "hdfs-site.xml"));
        try {
            connection = ConnectionFactory.createConnection(configuration);
            admin = connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws Exception {

//        //获得连接对象
//        Connection con = ConnectionFactory.createConnection(conf);
//        Admin admin = con.getAdmin();
//
//        TableName tn = TableName.valueOf("t1"); //创建表名对象
//        HTable table = new HTable(conf, tn);
////        Filter filter = new RowFilter(CompareFilter.CompareOp.GREATER_OR_EQUAL,
////                new BinaryComparator(Bytes.toBytes("rowkey001")));
//        Filter filter2 = new SingleColumnValueFilter(Bytes
//                .toBytes("col1"), null, CompareFilter.CompareOp.EQUAL, Bytes
//                .toBytes("value01"));
//        Scan scan = new Scan();
//        scan.setFilter(filter2);
//        ResultScanner rs = table.getScanner(scan);
//        String s1 = null;
//        int s2 = 0;
//        long s3 = 0;
//        for (Result result : rs) {
//            System.out.println("rowkey:" + new String(result.getRow()));
//            for (KeyValue keyValue : result.raw()) {
//                if (new String(keyValue.getQualifier()).equals("col1")) {
//                    s1 = Bytes.toString(keyValue.getValue());
//                    System.out.println("列族:" + new String(keyValue.getFamily())
//                            + " 列:" + new String(keyValue.getQualifier()) + ":"
//                            + s1);
//                }
//            }
//        }
        getResult("t1", "rowkey001");
//        System.out.println(System.getenv("HADOOP_CONF_DIR"));
//        System.out.println(System.getenv("HBASE_CONF_DIR"));
    }

    public static void queryAll() throws IOException {
        TableName tn = TableName.valueOf("t1"); //创建表名对象
        HTable table = new HTable(configuration, tn);
//        Filter filter = new RowFilter(CompareFilter.CompareOp.GREATER_OR_EQUAL,
//                new BinaryComparator(Bytes.toBytes("rowkey001")));
        Filter filter2 = new SingleColumnValueFilter(Bytes
                .toBytes("col1"), null, CompareFilter.CompareOp.EQUAL, Bytes
                .toBytes("value01"));
        Scan scan = new Scan();
        scan.setFilter(filter2);
        ResultScanner rs = table.getScanner(scan);
        String s1 = null;
        int s2 = 0;
        long s3 = 0;
        for (Result result : rs) {
            System.out.println("rowkey:" + new String(result.getRow()));
            for (KeyValue keyValue : result.raw()) {
                if (new String(keyValue.getQualifier()).equals("col1")) {
                    s1 = Bytes.toString(keyValue.getValue());
                    System.out.println("列族:" + new String(keyValue.getFamily())
                            + " 列:" + new String(keyValue.getQualifier()) + ":"
                            + s1);
                }
            }
        }
    }


    public static Result getResult(String tableName, String rowKey)
            throws IOException {
        Get get = new Get(Bytes.toBytes(rowKey));
        Table table = connection.getTable(TableName.valueOf(tableName));
        System.out.println("1");
        Result result = table.get(get);
        System.out.println("2");
        for (KeyValue kv : result.list()) {
            System.out.println("3");
            System.out.println("family:" + Bytes.toString(kv.getFamily()));
            System.out
                    .println("qualifier:" + Bytes.toString(kv.getQualifier()));
            System.out.println("value:" + Bytes.toString(kv.getValue()));
            System.out.println("Timestamp:" + kv.getTimestamp());
            System.out.println("-------------------------------------------");
        }
        return result;
    }


    /**
     * 根据给定的表名创建表
     *
     * @param name 表名
     * @throws Exception
     */
    public static void createTable(String name) throws Exception {
        TableName tableName = TableName.valueOf(name);
        if (admin.tableExists(tableName)) {
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
            System.out.println("表已存在，先删除...");
        }

        HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
        tableDescriptor.addFamily(new HColumnDescriptor("cf1"));
        admin.createTable(tableDescriptor);
        admin.close();
    }

    /**
     * 初始化数据
     *
     * @param tableName
     * @throws Exception
     */
    public static void initData(String tableName) throws Exception {
        HTable table = new HTable(configuration, tableName);
        for (int i = 10; i < 22; i++) {
            String ii = String.valueOf(i);
            Put put = new Put(ii.getBytes());
            put.addColumn("cf1".getBytes(), "column1".getBytes(), "the first column".getBytes());
            put.addColumn("cf1".getBytes(), "column2".getBytes(), "the second column".getBytes());
            put.addColumn("cf1".getBytes(), "column3".getBytes(), "the third column".getBytes());
            table.put(put);
        }
        table.close();
    }

    /**
     * 删除一行数据
     *
     * @param tableName 表名
     * @param rowKey    rowkey
     * @throws Exception
     */
    public static void deleteRow(String tableName, String rowKey) throws Exception {
        HTable table = new HTable(configuration, tableName);
        Delete delete = new Delete(rowKey.getBytes());
        table.delete(delete);
    }

    /**
     * 删除rowkey列表
     *
     * @param tableName
     * @param rowKeys
     * @throws Exception
     */
    public static void deleteRowKeys(String tableName, List<String> rowKeys) throws Exception {
        HTable table = new HTable(configuration, tableName);
        List<Delete> deletes = new ArrayList<Delete>();
        for (String rowKey : rowKeys) {
            Delete delete = new Delete(rowKey.getBytes());
            deletes.add(delete);
        }
        table.delete(deletes);
        table.close();
    }

    /**
     * 根据rowkey获取所有column值
     *
     * @param tableName
     * @param rowKey
     * @throws Exception
     */
    public static void get(String tableName, String rowKey) throws Exception {
        HTable table = new HTable(configuration, tableName);
        Get get = new Get(rowKey.getBytes());
        Result result = table.get(get);
        for (KeyValue kv : result.raw()) {
            System.out.println("cf=" + new String(kv.getFamily()) + ", columnName=" + new String(kv.getQualifier()) + ", value=" + new String(kv.getValue()));
        }
    }


    /**
     * 批量查询
     *
     * @param tableName
     * @param startRow
     * @param stopRow
     * @throws Exception select column1,column2,column3 from test_table where id between ... and
     */
    public static void scan(String tableName, String startRow, String stopRow) throws Exception {
        HTable table = new HTable(configuration, tableName);
        Scan scan = new Scan();
        scan.addColumn("cf1".getBytes(), "column1".getBytes());
        scan.addColumn("cf1".getBytes(), "column2".getBytes());
        scan.addColumn("cf1".getBytes(), "column3".getBytes());

        //rowkey>=a && rowkey<b
        scan.setStartRow(startRow.getBytes());
        scan.setStopRow(stopRow.getBytes());
        ResultScanner scanner = table.getScanner(scan);

        for (Result result : scanner) {
            for (KeyValue keyValue : result.raw()) {
                System.out.println(new String(keyValue.getFamily()) + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()));
            }
        }

    }

    /**
     * 单条件查询：测试SingleColumnValueFilter过滤器
     *
     * @param tableName
     * @param columnValue
     * @throws Exception LESS  <
     *                   LESS_OR_EQUAL <=
     *                   EQUAL =
     *                   NOT_EQUAL <>
     *                   GREATER_OR_EQUAL >=
     *                   GREATER >
     *                   NO_OP no operation
     */
    public static void testSingleColumnValueFilter(String tableName, String columnValue) throws Exception {
        HTable table = new HTable(configuration, tableName);
        Scan scan = new Scan();
        scan.addColumn("cf1".getBytes(), "column1".getBytes());
        scan.addColumn("cf1".getBytes(), "column2".getBytes());
        scan.addColumn("cf1".getBytes(), "column3".getBytes());
        //根据rowkey查询
        //RowFilter filter = new RowFilter(CompareOp.EQUAL, new RegexStringComparator("1"));
        //通过column查询
        Filter filter = new SingleColumnValueFilter("cf1".getBytes(), "column1".getBytes(), CompareFilter.CompareOp.GREATER, columnValue.getBytes());
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);

        for (Result result : scanner) {
            for (KeyValue keyValue : result.raw()) {
                System.out.println("第 " + new String(keyValue.getRow()) + " 行 ," + new String(keyValue.getFamily()) + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()));
            }
            System.out.println();
        }

    }

    /**
     * 模糊匹配rowkey
     *
     * @param tableName
     * @param rowKeyRegex
     * @throws Exception
     */
    public static void fuzzyQueryByRowkey(String tableName, String rowKeyRegex) throws Exception {
        HTable table = new HTable(configuration, tableName);
        RowFilter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator(rowKeyRegex));
//      PrefixFilter filter = new PrefixFilter(rowKeyRegex.getBytes());
        Scan scan = new Scan();
        scan.addColumn("cf1".getBytes(), "column1".getBytes());
        scan.addColumn("cf1".getBytes(), "column2".getBytes());
        scan.addColumn("cf1".getBytes(), "column3".getBytes());
        scan.setFilter(filter);

        ResultScanner scanner = table.getScanner(scan);
        int num = 0;
        for (Result result : scanner) {
            num++;

            System.out.println("rowKey:" + new String(result.getRow()));
            for (KeyValue keyValue : result.raw()) {
                System.out.println(new String(keyValue.getFamily()) + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()));
            }
            System.out.println();
        }
        System.out.println(num);
    }


    /**
     * 组合条件查询
     *
     * @param tableName
     * @param column1
     * @param column2
     * @param column3
     * @throws Exception
     */
    public static void multiConditionQuery(String tableName, String column1, String column2, String column3) throws Exception {
        HTable table = new HTable(configuration, tableName);
        Scan scan = new Scan();
        scan.addColumn("cf1".getBytes(), "column1".getBytes());
        scan.addColumn("cf1".getBytes(), "column2".getBytes());
        scan.addColumn("cf1".getBytes(), "column3".getBytes());

        SingleColumnValueFilter filter1 = new SingleColumnValueFilter("cf1".getBytes(), "column1".getBytes(), CompareFilter.CompareOp.EQUAL, column1.getBytes());
        SingleColumnValueFilter filter2 = new SingleColumnValueFilter("cf1".getBytes(), "column2".getBytes(), CompareFilter.CompareOp.EQUAL, column2.getBytes());
        SingleColumnValueFilter filter3 = new SingleColumnValueFilter("cf1".getBytes(), "column3".getBytes(), CompareFilter.CompareOp.EQUAL, column3.getBytes());

        FilterList filterAll = new FilterList();
        filterAll.addFilter(filter1);

        //与sql查询的in (?,?)一样的效果
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);
        filterList.addFilter(filter2);
        filterList.addFilter(filter3);

        filterAll.addFilter(filterList);
        scan.setFilter(filterAll);

        ResultScanner scanner = table.getScanner(scan);
        for (Result result : scanner) {
            System.out.println("rowKey:" + new String(result.getRow()));
            for (KeyValue keyValue : result.raw()) {
                System.out.println(new String(keyValue.getFamily()) + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()));
            }
            System.out.println();
        }

    }
}
