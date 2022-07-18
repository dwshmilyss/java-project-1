package com.yiban.presto.test;

import org.junit.Test;
import java.sql.*;
import java.util.Properties;

public class PrestoIOJDBCTest {
    @Test
    public void test() {
        try {
//            DriverManager.registerDriver(new io.prestosql.jdbc.PrestoDriver());
//            Connection conn = DriverManager.getConnection("jdbc:presto://192.168.26.41:30662/hive?source=adhoc_other", "root", null);
//            Connection conn = DriverManager.getConnection("jdbc:presto://192.168.26.41:30662/hive?applicationNamePrefix=etl", "root", null);
            String path = PrestoIOJDBCTest.class.getClassLoader().getResource("keystore.jks").getPath();
            String url = "jdbc:presto://k8s-worker-4:8443/hive";
            Properties properties = new Properties();
            properties.setProperty("user", "1");
            properties.setProperty("password", "IXdW5SuQ/yY=");
            properties.setProperty("SSL", "true");
            properties.setProperty("SSLKeyStorePath", path);
            properties.setProperty("SSLKeyStorePassword", "120653");
            Connection conn = DriverManager.getConnection(url,properties);
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
