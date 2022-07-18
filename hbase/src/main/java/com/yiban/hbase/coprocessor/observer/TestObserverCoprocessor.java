package com.yiban.hbase.coprocessor.observer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.coprocessor.BaseRegionObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.regionserver.wal.WALEdit;

import java.io.IOException;

/**
 * 测试一个observer coporcessor
 * 一个粉丝表 一个关注表
 * 在粉丝表上构建一个coprocessor 如果向粉丝表中插入一条数据 例如 a(rowkey) -> b(value)
 * 那么通过coprocessor 也要在关注表中插入一条数据 b(rowkey) -> a(value)
 * 这样就可以实现互粉
 * 操作步骤：
 * create 'guanzhu','f1'
 * create 'fensi','f1'
 * disable 'fensi'
 * alter 'my:fensi',METHOD => 'table_att','coprocessor' => 'hdfs://gagcluster/hbase.jar|com.yiban.hbase.coprocessor.observer.TestObserverCoprocessor|1001|'
 *  #理解 coprocessor 的四个参数，分别用'|'隔开的
 * 1、 你的协处理器 jar 包所在 hdfs 上的路径
 * 2、 协处理器类全限定名
 * 3、 协处理器加载顺序
 * 4、 传参
 *
 * enable 'fensi'
 */
public class TestObserverCoprocessor extends BaseRegionObserver {
    static Configuration configuration = HBaseConfiguration.create();
    static Table table  = null;

    static {
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        //3.73那套集群的hbase
//        configuration.set("hbase.zookeeper.quorum", "10.21.3.73,10.21.3.74,10.21.3.75,10.21.3.76,10.21.3.77");
//        configuration.set("hbase.master", "10.21.3.73:60000");
        //3.120 CDH集群的hbase
        configuration.set("hbase.zookeeper.quorum", "10.21.3.120,10.21.3.121,10.21.3.122,10.21.3.123,10.21.3.124");
        configuration.set("hbase.master", "10.21.3.120:60000");
        try {
            Connection connection = ConnectionFactory.createConnection(configuration);
            //获取关注表
            table = connection.getTable(TableName.valueOf("my:guanzhu"));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

//    @Override
//    public void prePut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability) throws IOException {
////        super.prePut(e, put, edit, durability);
//        //获取粉丝表中插入的rowkey作为关注表的 value
//        byte[] row = put.getRow();
//        //获取粉丝表插入的cell
//        Cell cell = put.get("f1".getBytes(), "from".getBytes()).get(0);
//        //获取粉丝表插入的 value 做为关注表的 rowkey
//        Put putIndex = new Put(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
//        putIndex.addColumn("f1".getBytes(), "from".getBytes(), row);
//        table.put(putIndex);
//        table.close();
//    }


    @Override
    public void postPut(ObserverContext<RegionCoprocessorEnvironment> e, Put put, WALEdit edit, Durability durability) throws IOException {
//        super.postPut(e, put, edit, durability);
        //获取粉丝表中插入的rowkey作为关注表的 value
        byte[] row = put.getRow();
        //获取粉丝表插入的cell
        Cell cell = put.get("f1".getBytes(), "from".getBytes()).get(0);
        //获取粉丝表插入的 value 做为关注表的 rowkey
        Put putIndex = new Put(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
        putIndex.addColumn("f1".getBytes(), "from".getBytes(), row);
        table.put(putIndex);
        table.close();
    }
}
