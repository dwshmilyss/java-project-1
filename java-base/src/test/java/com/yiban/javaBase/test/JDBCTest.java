package com.yiban.javaBase.test;

import com.yiban.javaBase.dev.redis.RedisPool;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.sql.*;

public class JDBCTest {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/hue";
    // 数据库的用户名与密码，需要根据自己的设置
    static final String USER = "root";
    static final String PASS = "120653";

    @Test
    public void test(){
        Connection conn = null;
        PreparedStatement pstmt = null;
        try{
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL,USER,PASS);
            String sql;
            sql = "select a.id,b.id,a.alias,b.name,b.street from contact_meta_prop a,(select id,name,state,street from contact where id = 5333) b;";
            pstmt = conn.prepareStatement(sql);
            ResultSetMetaData meta = pstmt.getMetaData();
            System.out.println(meta.getColumnCount());
            System.out.println(meta.getColumnName(1));
            System.out.println(meta.getColumnName(3));
//            ResultSet rs = stmt.executeQuery(sql);

//            // 展开结果集数据库
//            while(rs.next()){
//                // 通过字段检索
//                int id  = rs.getInt("id");
//                String name = rs.getString("name");
//                String url = rs.getString("url");
//
//                // 输出数据
//                System.out.print("ID: " + id);
//                System.out.print(", 站点名称: " + name);
//                System.out.print(", 站点 URL: " + url);
//                System.out.print("\n");
//            }
//            // 完成后关闭
//            rs.close();
            pstmt.close();
            conn.close();
        }catch(Exception e){
            // 处理 Class.forName 错误
            e.printStackTrace();
        }finally{
            // 关闭资源
            try{
                if(pstmt!=null) pstmt.close();
                if(conn!=null) conn.close();
            }catch(Exception se){
                se.printStackTrace();
            }
        }
    }
}
