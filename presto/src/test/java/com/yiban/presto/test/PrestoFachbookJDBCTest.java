package com.yiban.presto.test;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class PrestoFachbookJDBCTest {
    @Test
    public void test() {
        try {
            //这里去注册驱动类，但是由于io的包中也有同样全限定名的类，所以有可能加载不到fackbook包中的这个类
            //解决的办法就是在maven中调整导入jar的顺序
            DriverManager.registerDriver(new com.facebook.presto.jdbc.PrestoDriver());
            //如果用facebood的presto驱动，这里就只能用applicationNamePrefix=etl，而不能用source=adhoc_other
            Connection conn = DriverManager.getConnection("jdbc:presto://192.168.26.41:30662/hive?applicationNamePrefix=etl", "root", null);
            Statement stmt = conn.createStatement();
            try {
                ResultSet rs = stmt.executeQuery("SELECT * from dw_t1.contact limit 5");
                while(rs.next()) {
                    String _hoodie_commit_time = rs.getString(1);
                    String _hoodie_commit_seqno = rs.getString(2);
                    String _hoodie_record_key = rs.getString(3);
                    System.out.println(String.format("_hoodie_commit_time=%s, _hoodie_commit_seqno=%s, _hoodie_record_key=%s", _hoodie_commit_time, _hoodie_commit_seqno, _hoodie_record_key));
                }
            }
            finally {
                stmt.close();
                conn.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
