package com.yiban.hive.dev;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Created by duanwei on 2017/3/23.
 */
public class JDBCToHiveUtils {

    public static Connection getHiveConnection() throws IOException, ClassNotFoundException, SQLException {
        Properties properties = new Properties();
        InputStream inStream = JDBCToHiveUtils.class.getClassLoader()
                .getResourceAsStream("jdbc.properties");
        properties.load(inStream);

        // 1. 准备获取连接的 4 个字符串: user, password, url, jdbcDriver
        String user = properties.getProperty("userName");
        String password = properties.getProperty("password");
        String url = properties.getProperty("jdbcUrl");
        String jdbcDriver = properties.getProperty("jdbcDriver");

        // 2. 加载驱动: Class.forName(driverClass)
        System.out.println("jdbcDriver = " + jdbcDriver);
//        Class.forName(jdbcDriver);
        Class.forName("org.apache.hive.jdbc.HiveDriver");

        // 3.获取数据库连接
        Connection connection = DriverManager.getConnection(url, user,
                password);
        System.out.println(connection);
        return connection;
    }

    public static void releaseConn(ResultSet resultSet, Statement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
