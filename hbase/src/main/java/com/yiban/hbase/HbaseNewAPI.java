package com.yiban.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
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

    /**
     * 创建连接（连接zk和hbase）
     * @return
     * @throws IOException
     */
    public static Connection initHbase() throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.zookeeper.quorum", "127.0.0.1");
        //集群配置↓
        //configuration.set("hbase.zookeeper.quorum", "101.236.39.141,101.236.46.114,101.236.46.113");
        configuration.set("hbase.master", "127.0.0.1:60000");
        Connection connection = ConnectionFactory.createConnection(configuration);
        return connection;
    }

    /**
     * 创建表
     * @param connection 连接zk
     * @param tableName 表名
     * @param columnFamilys 列族
     * @throws IOException
     */
    public static TableName createTable(Connection connection ,String tableName,String[] columnFamilys) throws IOException{
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
     * @param connection 连接zk
     * @param tableName 表名
     * @param user 数据
     * @throws IOException
     */
    public static void insertData(Connection connection ,TableName tableName,User user) throws IOException {
        Put put = new Put(("user-" + user.getId()).getBytes());
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
     * @param connection
     * @param tableName
     * @throws IOException
     */
    public static void getNoDealData(Connection connection,TableName tableName) throws IOException {
            Table table = connection.getTable(tableName);
        Scan scan = new Scan();
        ResultScanner results = table.getScanner(scan);
        for (Result result : results) {
            System.out.println("scan : " + result);
        }
    }

    public static  User getDataByRowKey(Connection connection,TableName tableName,String rowKey) throws IOException {
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
                if(colName.equals("username")){
                    user.setUsername(value);
                }
                if(colName.equals("age")){
                    user.setAge(value);
                }
                if (colName.equals("gender")){
                    user.setGender(value);
                }
                if (colName.equals("phone")){
                    user.setPhone(value);
                }
                if (colName.equals("email")){
                    user.setEmail(value);
                }
            }
        }
        return user;
    }

    //查询指定单cell内容
    public static String getCellData(Connection connection,TableName tableName, String rowKey, String family, String col){

        try {
            Table table = connection.getTable(tableName);
            String result = null;
            Get get = new Get(rowKey.getBytes());
            if(!get.isCheckExistenceOnly()){
                get.addColumn(Bytes.toBytes(family),Bytes.toBytes(col));
                Result res = table.get(get);
                byte[] resByte = res.getValue(Bytes.toBytes(family), Bytes.toBytes(col));
                return result = Bytes.toString(resByte);
            }else{
                return result = "查询结果不存在";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "出现异常";
    }

    //查询指定表名中所有的数据
    public static List<User> getAllData(Connection connection,TableName tableName){

        Table table = null;
        List<User> list = new ArrayList<User>();
        try {
            table = connection.getTable(tableName);
            ResultScanner results = table.getScanner(new Scan());
            User user = null;
            for (Result result : results){
                String id = new String(result.getRow());
                System.out.println("用户名:" + new String(result.getRow()));
                user = new User();
                for(Cell cell : result.rawCells()){
                    String row = Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength());
                    //String family =  Bytes.toString(cell.getFamilyArray(),cell.getFamilyOffset(),cell.getFamilyLength());
                    String colName = Bytes.toString(cell.getQualifierArray(),cell.getQualifierOffset(),cell.getQualifierLength());
                    String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                    user.setId(row);
                    if(colName.equals("username")){
                        user.setUsername(value);
                    }
                    if(colName.equals("age")){
                        user.setAge(value);
                    }
                    if (colName.equals("gender")){
                        user.setGender(value);
                    }
                    if (colName.equals("phone")){
                        user.setPhone(value);
                    }
                    if (colName.equals("email")){
                        user.setEmail(value);
                    }
                }
                list.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    //删除指定cell数据
    public static void deleteByRowKey(String tableName, String rowKey) throws IOException {

        Table table = initHbase().getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        //删除指定列
        //delete.addColumns(Bytes.toBytes("contact"), Bytes.toBytes("email"));
        table.delete(delete);
    }

    //删除表
    public static void deleteTable(String tableName){

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

    public User(){

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