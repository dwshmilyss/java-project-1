package com.yiban.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther WEI.DUAN
 * @date 2019/3/12
 * @website http://blog.csdn.net/dwshmilyss
 * 使用hbase 1.3.1 API
 */
public class HbaseNewAPI {
    private static Admin admin;


    public static void main(String[] args) {
        try {
            Connection connection = initHbase();
            //创建表
//            TableName tableName = createTable(connection, "user", new String[]{"info", "contact"});
            TableName tableName = TableName.valueOf("user");


//            List<User> list = new ArrayList<>(101);
//            for (int i = 1; i <= 100; i++) {
//                User user = new User(i + "", "dw:" + i, "123", "unknown", "30", "1311234"+i, "4064865@163.com");
//                list.add(user);
//            }
//
//            for (User user : list) {
//                insertData(connection,tableName,user);
//            }

//            getNoDealData(connection,tableName);

//            getAllData(connection, tableName);

//            System.out.println("res = " + getCellData(connection, tableName, "user-50", "info", "age"));

//            getDataByStartAndEnd(connection, tableName, "user-51", "user-55");

            RegexStringComparator regexStringComparator = new RegexStringComparator("^(.*)@163.com"); // 支持正则表达式的值比较 (以 you 开头的字符串)
            SubstringComparator substringComparator = new SubstringComparator("@163.com"); // 查找包含 dwshmilyss 的字符串
            BinaryPrefixComparator binaryPrefixComparator = new BinaryPrefixComparator(Bytes.toBytes("@163.com")); //
////            getDataBySingleColumnValueFilter(connection, tableName, "info", "age", "18",null);
////            getDataBySingleColumnValueFilter(connection, tableName, "info", "email", null,binaryPrefixComparator);
//
//
//            SingleColumnValueFilter singleColumnValueFilter4 = new SingleColumnValueFilter("info".getBytes(),"gender".getBytes(), CompareFilter.CompareOp.EQUAL,"female".getBytes());
//            SingleColumnValueFilter singleColumnValueFilter5 = new SingleColumnValueFilter("info".getBytes(),"gender".getBytes(), CompareFilter.CompareOp.EQUAL,"male".getBytes());
//            SingleColumnValueFilter singleColumnValueFilter1 = new SingleColumnValueFilter("contact".getBytes(),"email".getBytes(), CompareFilter.CompareOp.EQUAL,substringComparator);
//            singleColumnValueFilter1.setFilterIfMissing(true);
//            singleColumnValueFilter1.setLatestVersionOnly(true);
//            SingleColumnValueFilter singleColumnValueFilter2 = new SingleColumnValueFilter("contact".getBytes(),"phone".getBytes(), CompareFilter.CompareOp.LESS_OR_EQUAL,"1311234657".getBytes());
//            SingleColumnValueFilter singleColumnValueFilter3 = new SingleColumnValueFilter("contact".getBytes(),"phone".getBytes(), CompareFilter.CompareOp.GREATER_OR_EQUAL,"1311234001".getBytes());
//            List<Filter> filters1 = new ArrayList<>();
//            List<Filter> filters2 = new ArrayList<>();
//            filters1.add(singleColumnValueFilter1);
//            filters1.add(singleColumnValueFilter2);
//            filters1.add(singleColumnValueFilter3);
//            filters2.add(singleColumnValueFilter4);
//            filters2.add(singleColumnValueFilter5);
//            /**
//             * 可以利用FilterList的嵌套来实现and 和 or 同时存在的条件
//             */
//            FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE, filters2);
//            filters1.add(filterList);
//            getDataByMultiFilter(connection,tableName,filters1);

            RegexStringComparator regexStringComparator1 = new RegexStringComparator("^(.*)-99");
            SubstringComparator substringComparator1 = new SubstringComparator("admin");
              getDataByRowFilter(connection,tableName,regexStringComparator1);
//            deleteByRowKey(connection,tableName,"user-50");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建连接（连接zk和hbase）
     *
     * @return
     * @throws IOException
     */
    public static Connection initHbase() throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.zookeeper.quorum", "10.21.3.73");
        //集群配置↓
        //configuration.set("hbase.zookeeper.quorum", "101.236.39.141,101.236.46.114,101.236.46.113");
        configuration.set("hbase.master", "10.21.3.73:60000");
        Connection connection = ConnectionFactory.createConnection(configuration);
        return connection;
    }

    /**
     * 创建表
     *
     * @param connection    连接zk
     * @param tableName     表名
     * @param columnFamilys 列族
     * @throws IOException
     */
    public static TableName createTable(Connection connection, String tableName, String[] columnFamilys) throws IOException {
        TableName tableName1 = TableName.valueOf(tableName);
        admin = connection.getAdmin();
        if (admin.tableExists(tableName1)) {
            System.out.println("表已存在");
        } else {
            HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName1);
            for (String columnFamily : columnFamilys) {
                HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(columnFamily);
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

    /**
     * 扫描表 获取原始数据
     *
     * @param connection
     * @param tableName
     * @throws IOException
     */
    public static void getNoDealData(Connection connection, TableName tableName) throws IOException {
        Table table = connection.getTable(tableName);
        Scan scan = new Scan();
        ResultScanner results = table.getScanner(scan);
        for (Result result : results) {
            System.out.println("scan : " + result);
        }
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
        List<User> list = new ArrayList<User>();
        try {
            table = connection.getTable(tableName);
            ResultScanner results = table.getScanner(new Scan());
            User user = null;
            for (Result result : results) {
                String id = new String(result.getRow());
                System.out.println("用户名:" + new String(result.getRow()));
                user = new User();
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
                list.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
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
     * @param >=        startRow
     * @param <         stopRow
     * @throws Exception
     */
    public static void getDataByStartAndEnd(Connection connection, TableName tableName, String startRow, String stopRow) {
        Table table = null;
        try {
            table = connection.getTable(tableName);
            Scan scan = new Scan();

            //这里可以只获取某个列族
            //这里可以只获取某个列
            scan.addColumn("info".getBytes(), "username".getBytes());

            //rowkey>=a && rowkey<b
            scan.setStartRow(startRow.getBytes());
            scan.setStopRow(stopRow.getBytes());
            ResultScanner scanner = table.getScanner(scan);

            for (Result result : scanner) {

                for (Cell cell : result.rawCells()) {
                    String rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                    String family = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
                    String colName = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                    String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    System.out.println("rowKey = " + rowKey + ",family = " + family + ",colName = " + colName + ",value = " + value);
                }

                System.out.println("----------------------------");
                for (Cell cell : result.listCells()) {
                    String rowKey = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                    String family = Bytes.toString(cell.getFamilyArray(), cell.getFamilyOffset(), cell.getFamilyLength());
                    String colName = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                    String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    System.out.println("rowKey = " + rowKey + ",family = " + family + ",colName = " + colName + ",value = " + value);
                }
                System.out.println("----------------------------");

                for (KeyValue keyValue : result.raw()) {
                    System.out.println("rowkey = " + new String(keyValue.getRow()) + ",family = " + new String(keyValue.getFamily()) + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()));
                }


                System.out.println(" ======================= ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 过滤器查询：SingleColumnValueFilter过滤器
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
            Filter filter = null;
            if (columnValue == null) {
                filter = new SingleColumnValueFilter(family.getBytes(), column.getBytes(), CompareFilter.CompareOp.GREATER_OR_EQUAL, byteArrayComparable);
            }
            if (byteArrayComparable == null) {
                filter = new SingleColumnValueFilter(family.getBytes(), column.getBytes(), CompareFilter.CompareOp.GREATER_OR_EQUAL, columnValue.getBytes());
            }
            scan.setFilter(filter);
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

    public static void getDataByMultiFilter(Connection connection, TableName tableName, List<Filter> filters) {
        try {
            Table table = connection.getTable(tableName);
            Scan scan = new Scan();
            /**
             * MUST_PASS_ONE => or
             * MUST_PASS_ALL => and
             */
            FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL, filters);
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
            RowFilter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, byteArrayComparable);
            scan.setFilter(filter);
            ResultScanner scanner = table.getScanner(scan);
            int cn = 0;
            for (Result result : scanner) {
                ++cn;
                for (KeyValue keyValue : result.raw()) {
                    System.out.println("第 " + new String(keyValue.getRow()) + " 行 ," + new String(keyValue.getFamily()) + ":" + new String(keyValue.getQualifier()) + "=" + new String(keyValue.getValue()));
                }
                System.out.println();
            }
            System.out.println("cn = " + cn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //删除表
    public static void deleteTable(String tableName) {
        try {
            TableName tablename = TableName.valueOf(tableName);
            admin = initHbase().getAdmin();
            admin.disableTable(tablename);
            admin.deleteTable(tablename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


class User {
    private String id;
    private String username;
    private String password;
    private String gender;
    private String age;
    private String phone;
    private String email;

    public User(String id, String username, String password, String gender, String age, String phone, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.email = email;
    }

    public User() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}