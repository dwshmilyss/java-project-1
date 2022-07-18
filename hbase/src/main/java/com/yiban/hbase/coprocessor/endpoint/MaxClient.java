package com.yiban.hbase.coprocessor.endpoint;

import com.google.protobuf.ServiceException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.coprocessor.Batch;
import org.apache.hadoop.hbase.ipc.BlockingRpcCallback;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.Map;

public class MaxClient {
    //设置参数
    private static final String TABLE_NAME = "Student";
    private static final String FAMILY = "baseInfo";
    private static final String COLUMN = "age";
    private static final byte[] STRAT_KEY = Bytes.toBytes("a");
    private static final byte[] END_KEY = Bytes.toBytes("z");

    public static void main(String[] args) throws IOException {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        //3.73那套集群的hbase
//        configuration.set("hbase.zookeeper.quorum", "10.21.3.73,10.21.3.74,10.21.3.75,10.21.3.76,10.21.3.77");
//        configuration.set("hbase.master", "10.21.3.73:60000");
        //3.120 CDH集群的hbase
        configuration.set("hbase.zookeeper.quorum", "10.21.3.120,10.21.3.121,10.21.3.122,10.21.3.123,10.21.3.124");
        configuration.set("hbase.master", "10.21.3.120:60000");
        configuration.setLong("hbase.rpc.timeout", 600000);
        Connection connection = ConnectionFactory.createConnection(configuration);
        System.setProperty("HADOOP_USER_NAME", "hdfs");
        HTable table = (HTable) connection.getTable(TableName.valueOf(TABLE_NAME));

        // 设置请求对象
        final maxProtocol.maxRequest request = maxProtocol.maxRequest.newBuilder().setFamily(FAMILY).setColumn(COLUMN).build();
        try {
            // 获得返回值
            Map<byte[], Double> result = table.coprocessorService(maxProtocol.maxService.class, STRAT_KEY,  END_KEY,
                    new Batch.Call<maxProtocol.maxService, Double>() {
                        @Override
                        public Double call(maxProtocol.maxService maxService) throws IOException {
                            BlockingRpcCallback<maxProtocol.maxResponse> rpcCallback = new BlockingRpcCallback<maxProtocol.maxResponse>();
                            maxService.getmax(null, request, rpcCallback);
                            maxProtocol.maxResponse response = rpcCallback.get();
                            return response.getMax();
                        }
                    });
            Double max = null;
            // 将返回值进行迭代相加
            for (Double temp : result.values()) {
                max = max != null && (temp == null || compare(temp, max) <= 0) ? max : temp;
            }
            // 结果输出
            System.out.println("max: " + max);

        } catch (ServiceException e) {
            e.printStackTrace();
        }catch (Throwable e) {
            e.printStackTrace();
        }
        table.close();
        connection.close();

    }

    public static int compare(Double l1, Double l2) {
        if (l1 == null ^ l2 == null) {
            return l1 == null ? -1 : 1; // either of one is null.
        } else if (l1 == null)
            return 0; // both are null
        return l1.compareTo(l2); // natural ordering.
    }
}
