package com.yiban.hive.test;

import com.yiban.hive.dev.JDBCToHiveUtils;

import java.sql.Connection;

/**
 * Created by duanwei on 2017/3/23.
 */
public class Test {
    @org.junit.Test
    public void testCreateConn(){
        try {
            Connection connection = JDBCToHiveUtils.getHiveConnection();
            System.out.println(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
