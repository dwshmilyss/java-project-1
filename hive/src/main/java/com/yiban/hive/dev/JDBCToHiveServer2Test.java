package com.yiban.hive.dev;

import java.sql.*;

/**
 * Created by duanwei on 2017/3/22.
 */
public class JDBCToHiveServer2Test {
    public static void main(String[] args) {
        String jdbcDriver = "org.apache.hive.jdbc.HiveDriver";
//        String jdbcUrl = "jdbc:hive2://10.21.3.75:10000/test";
        String jdbcUrl = "jdbc:hive2://192.168.100.128:10000/test";
        String userName = "";
        String password = "";
        try {
            Class.forName(jdbcDriver);
            Connection connection = DriverManager.getConnection(jdbcUrl,userName,password);
            Statement statement = connection.createStatement();
//            ResultSet rs = statement.executeQuery("SELECT * FROM testa");
            ResultSet rs = statement.executeQuery("SELECT * FROM product_external");
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            System.out.println("一共有："+rs.getRow()+"条记录");
            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                System.out.println("label = "+resultSetMetaData.getColumnLabel(i)+",name = "+resultSetMetaData.getColumnTypeName(i)+",typeName = "+resultSetMetaData.getColumnTypeName(i));
            }
            String str = "";
            while (rs.next()){
                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    str += rs.getString(i) + " ";
                }
                System.out.println(str);
                str = "";
            }

        } catch (ClassNotFoundException|SQLException e) {
            e.printStackTrace();
        }

    }

}
