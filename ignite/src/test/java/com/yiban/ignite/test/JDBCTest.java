package com.yiban.ignite.test;

import com.yiban.ignite.dev.entity.Emp;
import com.yiban.ignite.dev.entity.Person;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;
import org.apache.ignite.configuration.*;
import org.apache.ignite.events.EventType;
import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.sql.*;
import java.util.*;

import static org.apache.ignite.configuration.DeploymentMode.CONTINUOUS;

public class JDBCTest {

    public Connection conn;
    public PreparedStatement pstmt;
    public String url = "jdbc:ignite:thin://localhost;partitionAwareness=true";

    @Before
    public void init() {
        try {
            Class.forName("org.apache.ignite.IgniteJdbcThinDriver");
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsert() {
        try {
            String insertSql = "insert into emp (id,empno,ename) values(?,?,?);";
            for (long i = 1006; i <= 20000; i++) {
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setLong(1,i);
                pstmt.setLong(2,i);
                pstmt.setString(3,"zhangsan"+i);
                int effectNum = pstmt.executeUpdate();
                System.out.println("effectNum = " + effectNum);
            }
//            conn.setAutoCommit(false);
//            conn.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    @Test
    public void testMergeIdentity() {
        String[] strings = {"anonumousId", "uuid", "externalId"};
        try {
//            conn.setAutoCommit(false);
            String insertSql = "insert into identity_test (id,type,contact_id,udc_channel_id,channel_id,identity_id,date_created) values(?,?,?,?,?,?,?);";
            int i = 1191742;
            for (; i <= 50000000; i++) {
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setString(1, UUID.randomUUID().toString());
                pstmt.setString(2,strings[new Random().nextInt(3)]);
                pstmt.setLong(3,i);
                pstmt.setLong(4,i);
                pstmt.setLong(5,37L);
                pstmt.setLong(6,35821L);
                pstmt.setLong(7,1631615246362L);
//                pstmt.addBatch();
                int effectNum = pstmt.executeUpdate();
                System.out.println("effectNum = " + effectNum + ",total = " + i);
//                if (i % 100 == 0) {
//                    pstmt.executeBatch();
//                    System.out.println("total = " + i);
//                    conn.commit();
//                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testMerge() {
        try {
            String insertSql = "merge into emp (id,empno,ename) values(?,?,?);";
            for (long i = 5; i <= 20000; i++) {
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setLong(1,i);
                pstmt.setLong(2,i);
                pstmt.setString(3,"zhangsan");
                int effectNum = pstmt.executeUpdate();
                System.out.println("effectNum = " + effectNum);
            }
//            conn.setAutoCommit(false);
//            conn.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testSelectById() {
        String selectSql = "select * from emp1;";
        try {
            pstmt = conn.prepareStatement(selectSql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("EMPNO = " + rs.getString("EMPNO") + ",ENAME = " + rs.getString("ENAME"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testSelectByJDBC() {
        StopWatch watch = new StopWatch();
        watch.start();
        String selectSql = "select count(1) as cn from identity_test where type = 'anonumousId';";
        try {
            pstmt = conn.prepareStatement(selectSql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                System.out.println("count is = " + rs.getString("cn"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        watch.stop();
        System.out.println(watch.getTotalTimeMillis());
    }

    @Test
    public void testByGrid() {
        StopWatch watch = new StopWatch();
        watch.start();

        watch.stop();
        System.out.println(watch.getTotalTimeMillis());
    }
}
