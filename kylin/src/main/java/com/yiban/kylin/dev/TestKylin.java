package com.yiban.kylin.dev;


import java.sql.*;
import java.util.Properties;

public class TestKylin {
    public static void main(String[] args) throws Exception {
        //Kylin_URL
        String KYLIN_URL = "jdbc:kylin://slave01:7070/learn_kylin";
        //Kylin的用户名
        String KYLIN_USER = "ADMIN";
        //Kylin的密码
        String KYLIN_PASSWD = "KYLIN";
        Driver driver = (Driver) Class.forName("org.apache.kylin.jdbc.Driver").newInstance();
        Properties info = new Properties();
        info.put("user", "ADMIN");
        info.put("password", "KYLIN");
        Connection conn = driver.connect(KYLIN_URL, info);
        PreparedStatement state = conn.prepareStatement("select sum(price) from kylin_sales");
        ResultSet resultSet = state.executeQuery();

        while (resultSet.next()) {
//            assertEquals("foo", resultSet.getString(1));
//            assertEquals("bar", resultSet.getString(2));
//            assertEquals("tool", resultSet.getString(3));
            System.out.println(resultSet.getInt(1));
            org.junit.Assert.assertEquals(495151, resultSet.getInt(1));
        }
    }
}
