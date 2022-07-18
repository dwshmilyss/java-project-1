package com.yiban.hbase;

import com.yiban.hbase.entity.User;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.regionserver.*;
import org.apache.hadoop.hbase.regionserver.compactions.FIFOCompactionPolicy;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther WEI.DUAN
 * @date 2019/3/12
 * @website http://blog.csdn.net/dwshmilyss
 * 此代码基于cdh5.7.6-hbase1.2.0
 */
public class HbaseNewAPI {
    private static Admin admin;
    /**
     * Logger
     **/
    private static final Logger LOGGER = LoggerFactory.getLogger(HbaseNewAPI.class);

    public static void main(String[] args) {
        try (Connection connection = getConnection()){
            //创建表
//            TableName tableName = createTable(connection, "test_1_copy", new String[]{"family", "column"});
//            TableName tableName = TableName.valueOf("user");
//            TableName tableName = TableName.valueOf("test1");

//            TableName tableName = TableName.valueOf("my:fensi");
//            testPut(tableName, "c", "f1", "from", "d");

            TableName tableName = TableName.valueOf("test:testBeforeCreateRegion");
            //用16进制数字型字符串预创建2个region
//            byte[][] splits = getHexSplits("001", "100", 2);
//            boolean flag = createTableByBeforeCreateRegion(connection, tableName, new String[]{"cf"}, splits);
//            System.out.println("flag = " + flag);

//            //插入数据 begin
//            List<User> list = new ArrayList<>(101);
//            for (int i = 1; i <= 100; i++) {
//                User user = new User(i + "", "dw:" + i, "123", "unknown", "30", "1311234"+i, "4064865@163.com");
//                list.add(user);
//            }
//
//            for (User user : list) {
//                insertData(connection,tableName,user);
//            }
//                //插入数据 end
//            getNoDealData(connection, tableName);//按照hbase的格式输出显示所有数据

            //统计表行数测试
//            String coprocessorClassName = "org.apache.hadoop.hbase.coprocessor.AggregateImplementation";
//            HbaseNewAPI.addTableCoprocessor(tableName, coprocessorClassName);
//            long rowCount = HbaseNewAPI.rowCount(tableName, "Family");
//            System.out.println("rowCount = " + rowCount);
            //查询所有数据并填充User对象
//            List<User> list = getAllData(connection, tableName);
//            if (list != null) {
//                for (User user : list) {
//                    System.out.println(user);
//                }
//            }

//            //获取某个cell的值
//            System.out.println("res = " + getCellData(connection, tableName, "user-50", "info", "age"));

//          //根据startRowkey 和 stopRowKey查找数据
//            getDataByStartAndEnd(connection, tableName, "user-51", "user-55");

//            RegexStringComparator regexStringComparator = new RegexStringComparator("^(.*)@163.com"); // 支持正则表达式的值比较 (以 you 开头的字符串)
//            SubstringComparator substringComparator = new SubstringComparator("dwshmilyss"); // 查找包含 dwshmilyss 的字符串
//            BinaryPrefixComparator binaryPrefixComparator = new BinaryPrefixComparator(Bytes.toBytes("dwshmilyss")); //
//            //TODO 值比较的话用这两种比较器没有效果 还是老老实实指定列值吧
//            BinaryComparator binaryComparator = new BinaryComparator(Bytes.toBytes(18));
//            LongComparator longComparator = new LongComparator(18);
//
//            //指定列值作为过滤条件
////            getDataBySingleColumnValueFilter(connection, tableName, "info", "age", "18",null);
//            //指定过滤器作为过滤条件
////            getDataBySingleColumnValueFilter(connection, tableName, "contact", "email", null, regexStringComparator);
//
            //测试按单列查询条件（带分页，但是如果有多个region，pagefilter依然是只作用于每个region，所以每次返回的结果数就是 region number * pageSize）
//            getDataBySingleColumnValueFilter(connection, tableName, "cf", "c1", "a",null);
//            /**
//             * 组合实现复杂查询
//             * 可以利用FilterList的嵌套来实现and 和 or 同时存在的条件
//             * MUST_PASS_ONE => OR
//             * MUST_PASS_ALL => AND
//             */
//            System.out.println("========  muliti filter start ==============");
//            SingleColumnValueFilter singleColumnValueFilter1 = new SingleColumnValueFilter("contact".getBytes(),"email".getBytes(), CompareFilter.CompareOp.EQUAL,substringComparator);
//            SingleColumnValueFilter singleColumnValueFilter2 = new SingleColumnValueFilter("contact".getBytes(),"phone".getBytes(), CompareFilter.CompareOp.LESS_OR_EQUAL,"1311234657".getBytes());
//            SingleColumnValueFilter singleColumnValueFilter3 = new SingleColumnValueFilter("contact".getBytes(),"phone".getBytes(), CompareFilter.CompareOp.GREATER_OR_EQUAL,"1311234001".getBytes());
//            singleColumnValueFilter1.setFilterIfMissing(true);
//            singleColumnValueFilter1.setLatestVersionOnly(true);
//            List<Filter> filters1 = new ArrayList<>();
//            filters1.add(singleColumnValueFilter1);
//            filters1.add(singleColumnValueFilter2);
//            filters1.add(singleColumnValueFilter3);
//
//            SingleColumnValueFilter singleColumnValueFilter4 = new SingleColumnValueFilter("info".getBytes(),"gender".getBytes(), CompareFilter.CompareOp.EQUAL,"female".getBytes());
//            SingleColumnValueFilter singleColumnValueFilter5 = new SingleColumnValueFilter("info".getBytes(),"gender".getBytes(), CompareFilter.CompareOp.EQUAL,"male".getBytes());
//            List<Filter> filters2 = new ArrayList<>();
//            filters2.add(singleColumnValueFilter4);
//            filters2.add(singleColumnValueFilter5);
//            //TODO OR
//            FilterList filterList1 = new FilterList(FilterList.Operator.MUST_PASS_ONE, filters1);
//            //TODO AND
//            FilterList filterList2 = new FilterList(FilterList.Operator.MUST_PASS_ALL, filters2);
//
//            List<Filter> filters = new ArrayList<>();
//            filters.add(filterList1);
//            filters.add(filterList2);
//            //TODO 最后再AND
//            FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL, filters);
//            getDataByMultiFilter(connection,tableName,filterList);
//            System.out.println("========  muliti filter end ==============");
//
//            RegexStringComparator regexStringComparator1 = new RegexStringComparator("^(.*)-99");
//            SubstringComparator substringComparator1 = new SubstringComparator("admin");
//            getDataByRowFilter(connection, tableName, substringComparator1);

//            deleteByRowKey(connection,tableName,"user-50");

//            System.out.println(getDataByRowKey(getConnection(), tableName, "user-99"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用预创建region的方式建表
     *
     * @param connection
     * @param tableName
     * @param columnFamilys
     * @param splits
     * @return
     * @throws IOException
     */
    public static boolean createTableByBeforeCreateRegion(Connection connection, TableName tableName, String[] columnFamilys, byte[][] splits)
            throws IOException {
        HTableDescriptor hTableDescriptor = null;
        try {
            admin = connection.getAdmin();
            if (admin.tableExists(tableName)) {
                System.out.println("表已存在");
            } else {
                hTableDescriptor = new HTableDescriptor(tableName);
                //在表上设置compaction策略
//                hTableDescriptor.setConfiguration(DefaultStoreEngine.DEFAULT_COMPACTION_POLICY_CLASS_KEY, FIFOCompactionPolicy.class.getName());
                for (String columnFamily : columnFamilys) {
                    HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(columnFamily);
                    //在列上设置compaction策略
//                hColumnDescriptor.setConfiguration(DefaultStoreEngine.DEFAULT_COMPACTION_POLICY_CLASS_KEY, FIFOCompactionPolicy.class.getName());
                    hTableDescriptor.addFamily(hColumnDescriptor);
                }
                admin.createTable(hTableDescriptor, splits);
            }
            return true;
        } catch (TableExistsException e) {
            LOGGER.info("table " + hTableDescriptor.getNameAsString() + " already exists");
            // the table already exists...
            return false;
        }
    }

    public static byte[][] getHexSplits(String startKey, String endKey, int numRegions) { //start:001,endkey:100,10region [001,010]
        byte[][] splits = new byte[numRegions - 1][];
        BigInteger lowestKey = new BigInteger(startKey, 16);
        BigInteger highestKey = new BigInteger(endKey, 16);
        BigInteger range = highestKey.subtract(lowestKey);
        BigInteger regionIncrement = range.divide(BigInteger.valueOf(numRegions));
        lowestKey = lowestKey.add(regionIncrement);
        for (int i = 0; i < numRegions - 1; i++) {
            BigInteger key = lowestKey.add(regionIncrement.multiply(BigInteger.valueOf(i)));
            byte[] b = String.format("%016x", key).getBytes();
            splits[i] = b;
        }
        return splits;
    }

    /**
     * 测试插入数据
     * 用于为TestObserverCoprocessor.java生成测试数据
     *
     * @param tableName
     * @param rowKey
     * @param family
     * @param qualifier
     * @param value
     * @throws Exception
     */
    public static void testPut(TableName tableName, String rowKey, String family, String qualifier, String value) throws Exception {
        Table table = getConnection().getTable(tableName);
        Put put = new Put(rowKey.getBytes());
        put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
        table.put(put);
        System.out.println("成功插入一条数据");
        table.close();
    }

    public static void addTableCoprocessor(TableName tableName, String coprocessorClassName) {
        try {
            admin = getConnection().getAdmin();
            admin.disableTable(tableName);
            HTableDescriptor hTableDescriptor = admin.getTableDescriptor(tableName);
            hTableDescriptor.addCoprocessor(coprocessorClassName);
            admin.modifyTable(tableName, hTableDescriptor);
            admin.enableTable(tableName);
        } catch (IOException e) {
            LOGGER.info(e.getMessage(), e);
        }
    }

    /**
     * 统计表行数
     * 这里用的就是coprocessor
     *
     * @param tableName
     * @param family
     * @return
     */
    public static long rowCount(TableName tableName, String family) {
        AggregationClient ac = new AggregationClient(getConfiguration());
        Scan scan = new Scan();
        //指定扫描的列簇
        scan.addFamily(Bytes.toBytes(family));
        long rowCount = 0;
        try {
            rowCount = ac.rowCount(tableName, new LongColumnInterpreter(), scan);
        } catch (Throwable e) {
            LOGGER.info(e.getMessage(), e);
        }
        return rowCount;
    }


    /**
     * 创建连接（连接zk和hbase master）
     *
     * @return
     * @throws IOException
     */
    public static Connection getConnection() throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        //3.73那套集群的hbase
//        configuration.set("hbase.zookeeper.quorum", "10.21.3.73,10.21.3.74,10.21.3.75,10.21.3.76,10.21.3.77");
//        configuration.set("hbase.master", "10.21.3.73:60000");
        //3.120 CDH集群的hbase
        configuration.set("hbase.zookeeper.quorum", "10.21.3.120,10.21.3.121,10.21.3.122,10.21.3.123,10.21.3.124");
        configuration.set("hbase.master", "10.21.3.120:60000");
        Connection connection = ConnectionFactory.createConnection(configuration);
        return connection;
    }

    public static Configuration getConfiguration() {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        //3.73那套集群的hbase
//        configuration.set("hbase.zookeeper.quorum", "10.21.3.73,10.21.3.74,10.21.3.75,10.21.3.76,10.21.3.77");
//        configuration.set("hbase.master", "10.21.3.73:60000");
        //3.120 CDH集群的hbase
        configuration.set("hbase.zookeeper.quorum", "10.21.3.120,10.21.3.121,10.21.3.122,10.21.3.123,10.21.3.124");
        configuration.set("hbase.master", "10.21.3.120:60000");
        return configuration;
    }


    /**
     * 创建表
     *
     * @param connection    连接zk
     * @param tableName     表名
     * @param columnFamilys 列族 列族最好不要超过3个 因为列族多了容易引发IO瓶颈
     * @throws IOException
     */
    public static TableName createTable(Connection connection, String tableName, String[] columnFamilys) throws IOException {
        TableName tableName1 = TableName.valueOf(tableName);
        admin = connection.getAdmin();
        if (admin.tableExists(tableName1)) {
            System.out.println("表已存在");
        } else {
            HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName1);
            //在表上设置compaction策略
            hTableDescriptor.setConfiguration(DefaultStoreEngine.DEFAULT_COMPACTION_POLICY_CLASS_KEY, FIFOCompactionPolicy.class.getName());
            for (String columnFamily : columnFamilys) {
                HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(columnFamily);
                //在列上设置compaction策略
//                hColumnDescriptor.setConfiguration(DefaultStoreEngine.DEFAULT_COMPACTION_POLICY_CLASS_KEY, FIFOCompactionPolicy.class.getName());
                hTableDescriptor.addFamily(hColumnDescriptor);
            }
            admin.createTable(hTableDescriptor);
        }
        return tableName1;
    }

    /**
     * 插入数据
     *
     * @param connection 连接zk
     * @param tableName  表名
     * @param user       数据
     * @throws IOException
     */
    public static void insertData(Connection connection, TableName tableName, User user) throws IOException {
        Put put = new Put(("admin-" + user.getId()).getBytes());
        //参数：1.列族  2.列名  3.值
        put.addColumn("info".getBytes(), "username".getBytes(), user.getUsername().getBytes());
        put.addColumn("info".getBytes(), "age".getBytes(), user.getAge().getBytes());
        put.addColumn("info".getBytes(), "gender".getBytes(), user.getGender().getBytes());
        put.addColumn("contact".getBytes(), "phone".getBytes(), user.getPhone().getBytes());
        put.addColumn("contact".getBytes(), "email".getBytes(), user.getEmail().getBytes());
        Table table = connection.getTable(tableName);
        table.put(put);
    }

    private static final byte[] POSTFIX = new byte[]{0x00};

    /**
     * 扫描表 直接输出（按照hbase的格式输出）
     *
     * @param connection
     * @param tableName
     * @throws IOException
     */
    public static void getNoDealData(Connection connection, TableName tableName) throws IOException, InterruptedException, KeeperException {
        int totalRows = 0;
        byte[] lastRow = null;
        Filter pageFilter = new PageFilter(10);
        Table table = connection.getTable(tableName);
        RegionLocator regionLocator = connection.getRegionLocator(tableName);
        System.out.println("region number = " + regionLocator.getAllRegionLocations().size());
//        byte[][] startKeys = regionLocator.getStartKeys();
//        for (byte[] key : startKeys) {
//            System.out.println(new String(key) + " ");
//        }
//        System.out.println("==============================");
//        byte[][] endKeys = regionLocator.getEndKeys();
//        for (byte[] key : endKeys) {
//            System.out.println(new String(key) + " ");
//        }
//        System.out.println("==============first================");
//        Pair<byte[][], byte[][]> startEndKeys = regionLocator.getStartEndKeys();
//        byte[][] first = startEndKeys.getFirst();//这个其实就是getStartKeys
//        byte[][] second = startEndKeys.getSecond();//这个其实就是getEndKeys
//        for (byte[] key : first) {
//            System.out.println(new String(key) + " ");
//        }
//        System.out.println("=============second=================");
//        for (byte[] key : second) {
//            System.out.println(new String(key) + " ");
//        }

        HRegionLocation hRegionLocation = regionLocator.getRegionLocation("003".getBytes());
        System.out.println(hRegionLocation.getRegionInfo() + " ");
        Scan scan = new Scan();
        HRegionServer hRegionServer = new HRegionServer(getConfiguration());
        System.out.println("hRegionServer = " + hRegionServer.toString());
        scan.setMaxVersions();
        scan.setFilter(pageFilter);
        admin = connection.getAdmin();
        List<HRegionInfo> hRegionInfos = admin.getTableRegions(tableName);
        System.out.println("region name = " + hRegionInfos.get(0).getEncodedName());
        Region region = hRegionServer.getRegionByEncodedName(hRegionInfos.get(0).getEncodedName());
//        ResultScanner tableScanner = table.getScanner(scan);
        RegionScanner regionScanner = region.getScanner(scan);
        System.out.println(regionScanner.getRegionInfo());
        int cn = 0;
        List<Cell> cellList = new ArrayList<>();
//        regionScanner.next(cellList);
        while (regionScanner.nextRaw(cellList)) {
            cn++;
           for (Cell cell : cellList) {
                String rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                String familyName = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
                String colName = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                System.out.println("rowKey = " + rowKey + ",family = " + familyName + ",column = " + colName + ",value = " + value);
            }
        }
//        for (Result result : tableScanner.nextRaw()) {//等价于results.next()
//            cn++;
//            System.out.println("scan : " + result);
//        }
        System.out.println("cn = " + cn);

//        while (true) {
//            Scan scan = new Scan();
//            scan.setMaxVersions();
//            scan.setFilter(pageFilter);
//            if (lastRow != null) {//获取上一次查询的最后一个rowkey
//                byte[] startRow = Bytes.add(lastRow, POSTFIX);
//                System.out.println("start row: " + Bytes.toStringBinary(startRow));
//                scan.setStartRow(startRow);
//            }
////        scan.setMaxResultsPerColumnFamily(1);
////        scan.setReversed(true);
////        scan.setBatch(2);
////        scan.setAllowPartialResults(true);//允许获取非整行数据
//            ResultScanner scanner = table.getScanner(scan);
//            int localRows = 0;
////        System.out.println("results = " + results.next(20).length);
////            for (Result result : scanner) {//等价于results.next()
//            Result result;
//            while ((result = scanner.next()) != null) {
//                localRows++;
//                totalRows++;
//                lastRow = result.getRow();
//                System.out.println("scan : " + result);
//                //第一种遍历每个列方式
////            for (Cell cell : result.rawCells()) {
////                String rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
////                String familyName = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
////                String colName = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
////                String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
////                System.out.println("rowKey = " + rowKey + ",family = " + familyName + ",column = " + colName + ",value = " + value);
////            }
//                //第二种遍历方式
////            for (Cell cell : result.listCells()) {
////                String rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
////                String familyName = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
////                String colName = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
////                String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
////                System.out.println("rowKey = " + rowKey + ",family = " + familyName + ",column = " + colName + ",value = " + value);
////            }
//                // 第三种遍历方式，不过这种方式拿rowKey好麻烦啊
////            for (KeyValue keyValue :
////                    result.raw()) {
////                keyValue.getTypeByte();
////                //获取个rowkey真是麻烦
//////                int rowlength = Bytes.toShort(keyValue.getBuffer(), keyValue.getOffset() + KeyValue.ROW_OFFSET);
//////                String rowKey = Bytes.toStringBinary(keyValue.getBuffer(), keyValue.getOffset() + KeyValue.ROW_OFFSET + Bytes.SIZEOF_SHORT, rowlength);
////                //当然也可以用简单的API获取rowkey
////                String rowKey = Bytes.toString(keyValue.getRowArray(), keyValue.getRowOffset(), keyValue.getRowLength());
////                String family = Bytes.toString(keyValue.getFamilyArray(), keyValue.getFamilyOffset(), keyValue.getFamilyLength());
////                String column = Bytes.toString(keyValue.getQualifierArray(), keyValue.getQualifierOffset(), keyValue.getQualifierLength());
////                String value = Bytes.toString(keyValue.getValueArray(), keyValue.getValueOffset(), keyValue.getValueLength());
////                System.out.println("rowKey = " + rowKey + ",family = " + family + ",column = " + column + ",value = " + value);
////            }
//                // 第四种遍历方式
////            for (KeyValue keyValue :
////                    result.list()) {
////                String rowKey = Bytes.toString(keyValue.getRowArray(), keyValue.getRowOffset(), keyValue.getRowLength());
////                String family = Bytes.toString(keyValue.getFamilyArray(), keyValue.getFamilyOffset(), keyValue.getFamilyLength());
////                String column = Bytes.toString(keyValue.getQualifierArray(), keyValue.getQualifierOffset(), keyValue.getQualifierLength());
////                String value = Bytes.toString(keyValue.getValueArray(), keyValue.getValueOffset(), keyValue.getValueLength());
////                System.out.println("rowKey = " + rowKey + ",family = " + family + ",column = " + column + ",value = " + value);
////            }
//                System.out.println("--------------------------------");
//            }
//            scanner.close();
//            if (localRows == 0) break;
//        }
//        System.out.println("totalRows = " + totalRows);
    }

    public static User getDataByRowKey(Connection connection, TableName tableName, String rowKey) throws IOException {
        Table table = connection.getTable(tableName);
        Get get = new Get(rowKey.getBytes());
        User user = new User();
        user.setId(rowKey);

        //先判断是否有该rowKey对应的数据
        if (!get.isCheckExistenceOnly()) {
            Result result = table.get(get);
            for (Cell cell : result.rawCells()) {
                String colName = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                if (colName.equals("username")) {
                    user.setUsername(value);
                }
                if (colName.equals("age")) {
                    user.setAge(value);
                }
                if (colName.equals("gender")) {
                    user.setGender(value);
                }
                if (colName.equals("phone")) {
                    user.setPhone(value);
                }
                if (colName.equals("email")) {
                    user.setEmail(value);
                }
            }
        }
        return user;
    }

    /**
     * 查询指定单cell内容
     *
     * @param connection
     * @param tableName
     * @param rowKey
     * @param family
     * @param col
     * @return
     */
    public static String getCellData(Connection connection, TableName tableName, String rowKey, String family, String col) {
        try {
            Table table = connection.getTable(tableName);
            String result = null;
            Get get = new Get(rowKey.getBytes());
            if (!get.isCheckExistenceOnly()) {
                get.addColumn(Bytes.toBytes(family), Bytes.toBytes(col));
                Result res = table.get(get);
                byte[] resByte = res.getValue(Bytes.toBytes(family), Bytes.toBytes(col));
                return result = Bytes.toString(resByte);
            } else {
                return result = "查询结果不存在";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "出现异常";
    }

    /**
     * 查询指定表名中所有的数据
     */
    public static List<User> getAllData(Connection connection, TableName tableName) {
        Table table = null;
        List<User> userList = null;
        try {
            table = connection.getTable(tableName);
            ResultScanner results = table.getScanner(new Scan());
            System.out.println(results.next());
            if (results.next() != null) {
                //有数据才初始化list
                userList = new ArrayList<User>();
                User user = null;
                for (Result result : results) {
                    //获取rowKey
                    String id = new String(result.getRow());
                    System.out.println("用户名:" + id);
                    user = new User();
                    Cell[] cells = result.rawCells();
                    if (cells != null) {
                        for (Cell cell : result.rawCells()) {
                            //获取rowkey columnfamily 列名 值
                            String rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                            String family = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
                            String colName = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                            String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                            System.out.println("rowKey = " + rowKey + ",family = " + family + ",colName = " + colName + ",value = " + value);
                            user.setId(rowKey);
                            switch (colName) {
                                case "username":
                                    user.setUsername(value);
                                    break;
                                case "age":
                                    user.setAge(value);
                                    break;
                                case "gender":
                                    user.setGender(value);
                                    break;
                                case "phone":
                                    user.setPhone(value);
                                    break;
                                case "email":
                                    user.setEmail(value);
                                    break;
                                default:
                                    break;
                            }

                        }
                        userList.add(user);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userList;
    }

    /**
     * 删除指定cell数据
     *
     * @param connection
     * @param tableName
     * @param rowKey
     * @throws IOException
     */
    public static void deleteByRowKey(Connection connection, TableName tableName, String rowKey) throws IOException {

        Table table = connection.getTable(tableName);
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        //也可以只删除指定的列
        delete.addColumns(Bytes.toBytes("contact"), Bytes.toBytes("email"));
        table.delete(delete);
    }

    /**
     * 批量查询
     *
     * @param tableName
     * @param >=startRow
     * @param <          stopRow 注意这里不包含stopRow
     * @throws Exception
     */
    public static void getDataByStartAndEnd(Connection connection, TableName tableName, String startRow, String stopRow) {
        Table table = null;
        try {
            table = connection.getTable(tableName);
            Scan scan = new Scan();

            //这里可以只获取某个列族的某个列
            scan.addColumn("info".getBytes(), "username".getBytes());

            //rowkey>=a && rowkey<b
            scan.setStartRow(startRow.getBytes());
            scan.setStopRow(stopRow.getBytes());
            ResultScanner scanner = table.getScanner(scan);
            System.out.println(scanner.next());

            for (Result result : scanner) {
                //三种输出方式都可以
                //rawCells 返回Cell[]
                for (Cell cell : result.rawCells()) {
                    String rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                    String family = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
                    String colName = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                    String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    System.out.println("rowKey = " + rowKey + ",family = " + family + ",colName = " + colName + ",value = " + value);
                }

                //listCells返回List
//                for (Cell cell : result.listCells()) {
//                    String rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
//                    String family = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
//                    String colName = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
//                    String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
//                    System.out.println("rowKey = " + rowKey + ",family = " + family + ",colName = " + colName + ",value = " + value);
//                }

//                for (KeyValue keyValue : result.raw()) {
//                    System.out.println("rowkey = " + new String(keyValue.getRow()) + ",family = " + new String(keyValue.getFamily()) + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()));
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 过滤器查询：SingleColumnValueFilter 过滤器
     *
     * @param connection
     * @param tableName
     * @param family
     * @param column
     * @param columnValue * CompareFilter.CompareOp:
     *                    *                   LESS  <
     *                    *                   LESS_OR_EQUAL <=
     *                    *                   EQUAL =
     *                    *                   NOT_EQUAL <>
     *                    *                   GREATER_OR_EQUAL >=
     *                    *                   GREATER >
     *                    *                   NO_OP no operation
     */
    public static void getDataBySingleColumnValueFilter(Connection connection, TableName tableName, String family, String column, String columnValue, ByteArrayComparable byteArrayComparable) {
        try {
            Table table = connection.getTable(tableName);
            Scan scan = new Scan();
//            scan.addColumn("info".getBytes(), "age".getBytes());
//            scan.addColumn("contact".getBytes(), "email".getBytes());
            scan.addColumn("cf".getBytes(), "c1".getBytes());
//            scan.setMaxResultSize(10);
//            scan.setCaching(10);
            PageFilter pageFilter = new PageFilter(10);
            Filter singleColumnValueFilter = null;
            FilterList filterList = new FilterList();
            //如果列的值没传，那么用比较器
            if (columnValue == null) {
                singleColumnValueFilter = new SingleColumnValueFilter(family.getBytes(), column.getBytes(), CompareFilter.CompareOp.EQUAL, byteArrayComparable);
            }
            //如果比较器没传，那么用列值
            if (byteArrayComparable == null) {
                singleColumnValueFilter = new SingleColumnValueFilter(family.getBytes(), column.getBytes(), CompareFilter.CompareOp.EQUAL, columnValue.getBytes());
            }
            filterList.addFilter(singleColumnValueFilter);
            filterList.addFilter(pageFilter);
            scan.setFilter(filterList);
            ResultScanner scanner = table.getScanner(scan);
            int cn = 0;
            for (Result result : scanner) {
                cn ++ ;
                for (KeyValue keyValue : result.raw()) {
                    System.out.println("rowkey = " + new String(keyValue.getRow()) + "," + new String(keyValue.getFamily()) + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()));
                }
            }
            System.out.println("cn = " + cn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getDataByMultiFilter(Connection connection, TableName tableName, FilterList filterList) {
        try {
            Table table = connection.getTable(tableName);
            Scan scan = new Scan();
            scan.setFilter(filterList);
            ResultScanner scanner = table.getScanner(scan);

            for (Result result : scanner) {
                for (KeyValue keyValue : result.raw()) {
                    System.out.println("第 " + new String(keyValue.getRow()) + " 行 ," + new String(keyValue.getFamily()) + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()));
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 过滤器查询：RowFilter过滤器
     *
     * @param connection
     * @param tableName
     * @param byteArrayComparable
     */
    public static void getDataByRowFilter(Connection connection, TableName tableName, ByteArrayComparable byteArrayComparable) {
        try {
            Table table = connection.getTable(tableName);
            Scan scan = new Scan();
            /**
             * 这里的第二个参数可以使用多种Comparator
             */
            RowFilter rowFilter = new RowFilter(CompareFilter.CompareOp.EQUAL, byteArrayComparable);
            RowFilter lastRowFilter = new RowFilter(CompareFilter.CompareOp.GREATER, new BinaryComparator(Bytes.toBytes("admin-17")));
            PageFilter pageFilter = new PageFilter(10);
            FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
            filterList.addFilter(rowFilter);
            filterList.addFilter(lastRowFilter);
            filterList.addFilter(pageFilter);
            scan.setFilter(filterList);
            ResultScanner scanner = table.getScanner(scan);
            int cn = 0;
            for (Result result : scanner) {
                ++cn;
                for (KeyValue keyValue : result.raw()) {
                    System.out.println("第 " + new String(keyValue.getRow()) + " 行 ," + new String(keyValue.getFamily()) + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()));
                }
            }
            System.out.println("cn = " + cn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除表
     */
    public static void deleteTable(String tableName) {
        try {
            TableName tablename = TableName.valueOf(tableName);
            admin = getConnection().getAdmin();
            admin.disableTable(tablename);
            admin.deleteTable(tablename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}