package com.yiban.hbase.test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.junit.Test;

import java.io.IOException;

public class TestCoprocessor {
    private static final int NB_SERVERS = 2;

    @Test(timeout = 120000)
    public void testChangeTable() throws Exception {
        TableName tableName = TableName.valueOf("test1");
        Configuration configuration = getConfiguration();
        Connection connection = ConnectionFactory.createConnection(configuration);
        Admin admin = connection.getAdmin();
        HTableDescriptor hTableDescriptor = admin.getTableDescriptor(tableName);
        hTableDescriptor.setRegionReplication(NB_SERVERS);
    }


    private Configuration getConfiguration() {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
//        configuration.set("hbase.zookeeper.quorum", "10.21.3.73,10.21.3.74,10.21.3.75,10.21.3.76,10.21.3.77");
//        configuration.set("hbase.master", "10.21.3.73:60000");
        configuration.set("hbase.zookeeper.quorum", "10.21.3.120,10.21.3.121,10.21.3.122,10.21.3.123,10.21.3.124");
        configuration.set("hbase.master", "10.21.3.120:60000");
//        configuration.set("hbase.column.max.version", "2");
        return configuration;
    }
}
