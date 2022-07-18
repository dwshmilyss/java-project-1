package com.yiban.ignite.dev;

import java.sql.*;

public class JDBCTest {
    public static Connection conn;
    public static PreparedStatement pstmt;
    public static String url = "jdbc:ignite:thin://localhost;partitionAwareness=true";

    public static void main(String[] args) {
        try {
            Class.forName("org.apache.ignite.IgniteJdbcThinDriver");
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        String insertSql = "insert into city (id, name) values(?, ?);";
//        try {
//            pstmt = conn.prepareStatement(insertSql);
//            pstmt.setLong(1,5L);
//            pstmt.setString(2,"zhangsan");
//            int effectNum = pstmt.executeUpdate();
//            System.out.println("effectNum = " + effectNum);
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
        String selectSql = "select * from city where id = ?;";
        try {
            pstmt = conn.prepareStatement(selectSql);
            pstmt.setLong(1,1L);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("rs = " + rs.getString("name"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
