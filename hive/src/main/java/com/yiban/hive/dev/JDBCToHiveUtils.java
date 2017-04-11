package com.yiban.hive.dev;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Created by duanwei on 2017/3/23.
 */
public class JDBCToHiveUtils {
    private static final String jdbcDriver = "org.apache.hive.jdbc.HiveDriver";
    private static final String jdbcUrl = "jdbc:hive2://10.21.3.77:10000/db_hive_test";
    private static final String userName = "root";
    private static final String password = "root";


    public static Connection getHiveConnection() throws Exception {
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
        Class.forName(jdbcDriver);

        // 3.获取数据库连接
        Connection connection = DriverManager.getConnection(url, user,
                password);
        System.out.println(connection);
        return connection;
    }

    public void releaseConn(ResultSet resultSet, Statement statement, Connection connection) {
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
