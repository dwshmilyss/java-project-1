package com.yiban.javaBase.dev;

import java.sql.*;

/**
 * @Description: TODO
 * @Author: David.duan
 * @Date: 2023/9/1
 **/
public class MysqlDemo {
    public static Connection conn;
    public static PreparedStatement pstmt;
    public static ResultSet rs;
    public static String url = "jdbc:mysql://localhost:3306";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306?test", "root", "120653");
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            System.out.println("============数据源信息=============");
            //获取数据源的基本信息
            System.out.println("数据库URL:" + databaseMetaData.getURL());
            System.out.println("数据库用户名:" + databaseMetaData.getUserName());
            System.out.println("数据库产品名:" + databaseMetaData.getDatabaseProductName());
            System.out.println("数据库产品版本:" + databaseMetaData.getDatabaseProductVersion());
            System.out.println("驱动主版本:" + databaseMetaData.getDriverMajorVersion());
            System.out.println("驱动副版本:" + databaseMetaData.getDriverMinorVersion());
            System.out.println("数据库供应商用于schema的首选术语:" + databaseMetaData.getSchemaTerm());
            System.out.println("数据库供应商用于catalog的首选术语:" + databaseMetaData.getCatalogTerm());
            System.out.println("数据库供应商用于procedure的首选术语:" + databaseMetaData.getProcedureTerm());
            System.out.println("null值是否高排序:" + databaseMetaData.nullsAreSortedHigh());
            System.out.println("null值是否低排序:" + databaseMetaData.nullsAreSortedLow());
            System.out.println("数据库是否将表存储在本地文件中:" + databaseMetaData.usesLocalFiles());
            System.out.println("数据库是否为每个表使用一个文件:" + databaseMetaData.usesLocalFilePerTable());
            System.out.println("数据库SQL关键字:" + databaseMetaData.getSQLKeywords());
            System.out.println("============数据库信息=============");
            rs = databaseMetaData.getCatalogs();
            while (rs.next()) {
                String databaseName = rs.getString("TABLE_CAT");
                System.out.println("databaseName = " + databaseName);
            }
            System.out.println("=============schema信息============");
            rs = databaseMetaData.getSchemas();
            while (rs.next()) {
                String tableSchem = rs.getString("TABLE_SCHEM");
                String tableCatalog = rs.getString("TABLE_CATALOG");
                System.out.println("tableSchem = " + tableSchem + ",tableCatalog = " + tableCatalog);
            }
            System.out.println("============表信息=============");
            rs = databaseMetaData.getTables("test", null, null, new String[]{"TABLE"});
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                System.out.println("表名：" + tableName);
                System.out.println("表类型:"+rs.getString("TABLE_TYPE"));
                System.out.println("表注释:"+rs.getString("REMARKS"));
                System.out.println("表所属用户名：" + rs.getString(2));
                ResultSet primaryKeys = databaseMetaData.getPrimaryKeys("test", null, tableName);
                while (primaryKeys.next()){
                    System.out.println("表主键： "+ primaryKeys.getString("COLUMN_NAME"));
                    System.out.println("PKNAME:"+primaryKeys.getString("PK_NAME"));
                }
            }
            System.out.println("==============列信息===========");
            ResultSet columns = databaseMetaData.getColumns("test", null, "test", "%");
            while (columns.next()){
                System.out.println("字段名:"+columns.getString("COLUMN_NAME")+
                        "  字段类型："+columns.getString("DATA_TYPE")+
                        "  字段类型名:"+columns.getString("TYPE_NAME")+
                        "  TABLE_CAT:"+columns.getString("TABLE_CAT")+
                        "  TABLE_SCHEM:"+columns.getString("TABLE_SCHEM")+
                        "  TABLE_NAME:"+columns.getString("TABLE_NAME")+
                        "  COLUMN_SIZE:"+columns.getString("COLUMN_SIZE")+
                        "  DECIMAL_DIGITS:"+columns.getString("DECIMAL_DIGITS")+
                        "  NUM_PREC_RADIX:"+columns.getString("NUM_PREC_RADIX")+
                        "  REMARKS:"+columns.getString("REMARKS"));
            }
            System.out.println("------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
