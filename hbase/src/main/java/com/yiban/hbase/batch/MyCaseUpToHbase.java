package com.yiban.hbase.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * 读取生成的测试数据文件，写入到hbase中
 * 先建表
 * create 'test_lcc_mycase','case_lizu'
 */
public class MyCaseUpToHbase {
    public static void main(String[] args) throws IOException {
        Configuration configuration = HBaseConfiguration.create();

        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.zookeeper.quorum", "10.21.3.120,10.21.3.121,10.21.3.122,10.21.3.123,10.21.3.124");
        configuration.set("hbase.master", "10.21.3.120:60000");
        configuration.set("hbase.client.write.buffer", "2097152");//2mb的写缓存

        //默认connection实现是org.apache.hadoop.hbase.client.ConnectionManager.HConnectionImplementation
        Connection connection = ConnectionFactory.createConnection(configuration);
        //默认table实现是org.apache.hadoop.hbase.client.HTable
        Table table = connection.getTable(TableName.valueOf("test_lcc_mycase"));

        //3177不是我杜撰的，是2*hbase.client.write.buffer/put.heapSize()计算出来的
        int bestBathPutSize = 3177;//1918

        try {
            // Use the table as needed, for a single operation and a single thread
            // construct List<Put> putLists
            List<Put> putLists = new ArrayList<Put>();
            for (int k = 1; k <= 57; k++) {
                String fileName = "mycase" + k + ".txt";

                String filePath = "D:/tmp/" + fileName;
                File file = new File(filePath);
                BufferedReader reader = null;
                System.out.println("以行为单位读取文件内容，一次读一整行：");

                InputStreamReader insReader = new InputStreamReader(new FileInputStream(file), "UTF-8");
                reader = new BufferedReader(insReader);

                String tempString = null;
                int line = 1;
                // 一次读入一行，直到读入null为文件结束
                while ((tempString = reader.readLine()) != null) {
                    // 显示行号
                    System.out.println("line " + line + ": " + tempString);
                    String[] array = tempString.split(",");
                    String c_code = array[0];
                    String c_rcode = array[1];
                    String c_region = array[2];
                    String c_cate = array[3];
                    String c_start = array[4];
                    String c_end = array[5];
                    String c_start_m = array[6];
                    String c_end_m = array[7];
                    String c_name = array[8];
                    String c_mark = array[9];

                    Put put = new Put(Bytes.toBytes(c_code));
                    //Put put = new Put(rowkey.getBytes());
                    put.addImmutable("case_lizu".getBytes(), "c_code".getBytes(), c_code.getBytes("UTF-8"));
                    put.addImmutable("case_lizu".getBytes(), "c_rcode".getBytes(), c_rcode.getBytes("UTF-8"));
                    put.addImmutable("case_lizu".getBytes(), "c_region".getBytes(), c_region.getBytes("UTF-8"));
                    put.addImmutable("case_lizu".getBytes(), "c_cate".getBytes(), c_cate.getBytes("UTF-8"));
                    put.addImmutable("case_lizu".getBytes(), "c_start".getBytes(), c_start.getBytes("UTF-8"));
                    put.addImmutable("case_lizu".getBytes(), "c_end".getBytes(), c_end.getBytes("UTF-8"));
                    put.addImmutable("case_lizu".getBytes(), "c_start_m".getBytes(), c_start_m.getBytes("UTF-8"));
                    put.addImmutable("case_lizu".getBytes(), "c_end_m".getBytes(), c_end_m.getBytes("UTF-8"));
                    put.addImmutable("case_lizu".getBytes(), "c_name".getBytes(), c_name.getBytes("UTF-8"));
                    put.addImmutable("case_lizu".getBytes(), "c_mark".getBytes(), c_mark.getBytes("UTF-8"));

                    //这里面c_mark.getBytes("UTF-8") 这个指定utf-8很重要，不然会乱码
                    put.setDurability(Durability.SKIP_WAL);
                    putLists.add(put);
                    if (putLists.size() == bestBathPutSize) {
                        //达到最佳大小值了，马上提交一把
                        table.put(putLists);
                        putLists.clear();
                    }

                    line++;
                }
                reader.close();
                //剩下的未提交数据，最后做一次提交
                table.put(putLists);
            }
        } finally {
            table.close();
            connection.close();
        }
    }
}
