package com.linkflow.flink.dev.sql;

import com.alibaba.fastjson.JSONObject;
import com.linkflow.flink.dev.pojo.OrderLog;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableEnvironment;
import org.apache.flink.table.api.bridge.java.BatchTableEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import static org.apache.flink.table.api.Expressions.*;
/**
 * @author david.duan
 * @packageName com.linkflow.flink.dev.sql
 * @className FlinkSQLDemo
 * @date 2024/12/20
 * @description
 */
public class FlinkSQLDemo {
    private static final Logger logger = Logger.getLogger(FlinkSQLDemo.class);

    static {
        // Set log level dynamically
        org.apache.log4j.LogManager.getRootLogger().setLevel(Level.ERROR);
    }


    public static void main(String[] args) throws Exception {
//        testDataStreamToTable();
//        testBatchWithCSV();
        testStreamWithKafka();
    }

    private static void testDataStreamToTable() throws Exception {
        // 创建 Flink 环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        // 设置并行度
        env.setParallelism(1);
        DataStream<String> input = env.
                readTextFile(FlinkSQLDemo.class.getClassLoader().getResource("orderLog.txt").getPath());
        DataStream<OrderLog> orderLogDataStream = input.map(new MapFunction<String, OrderLog>() {
            @Override
            public OrderLog map(String value) throws Exception {
                JSONObject jsonObject = JSONObject.parseObject(value);
                String orderId = jsonObject.get("orderId").toString();
                String skuId = jsonObject.get("skuId").toString();
                String priceType = jsonObject.get("priceType").toString();
                Long requestTime = Long.getLong(jsonObject.get("orderId").toString());
                OrderLog orderLog = new OrderLog(orderId, skuId, priceType, requestTime);
                return orderLog;
            }
        });

        // Step 1: 创建 StreamExecutionEnvironment 和 StreamTableEnvironment
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);
        Table orderTable = tableEnv.fromDataStream(orderLogDataStream,"orderId,skuId,priceType,requestTime");

        // 这里的API其实和spark sql 类似
        // 写法.1
//        Step 2: 创建一个临时表，执行查询（例如，按名称分组并统计计数）
//        tableEnv.createTemporaryView("orderTable", orderTable);
//        Table resultTable = tableEnv.sqlQuery("SELECT count(orderId),priceType FROM orderTable group by priceType");

        // 写法.2
        // Step 2: 执行查询（例如，按名称分组并统计计数）
        Table resultTable = orderTable.groupBy($("priceType"))
                .select($("priceType"),$("orderId").count().as("orderId.count"));

        // Step 3: 将 Table 转换回 DataStream（如果需要进一步流式处理）
        DataStream<Tuple2<Boolean, Row>> resultStream = tableEnv.toRetractStream(resultTable,Row.class);

        resultStream.print();
        env.execute("DataStream to Table Example");
    }

    private static void testBatchWithCSV() throws Exception {
        // Create a Flink Configuration object
        Configuration config = new Configuration();
        // Set the metrics.scope.operator property
        config.setString("metrics.scope.operator", "256");

        ExecutionEnvironment env = ExecutionEnvironment.createLocalEnvironment(config);

        // blink planner
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inBatchMode().build();
        TableEnvironment tableEnv = TableEnvironment.create(settings);

        // Step 1: 创建 StreamExecutionEnvironment 和 StreamTableEnvironment
        // Create a Table using SQL DDL
        tableEnv.executeSql(
                "CREATE TABLE my_table (" +
                        "  id INT, " +
                        "  name STRING, " +
                        "  age INT, " +
                        "  gender STRING " +
                        ") WITH (" +
                        "  'connector' = 'filesystem', " +
                        "  'path' = '"+ FlinkSQLDemo.class.getClassLoader().getResource("data.csv").getPath() + "', " +
                        "  'format' = 'csv'" +
                        ")"
        );

        // Create a Table object from the registered source
        Table table = tableEnv.from("my_table");

        // Perform a query
//        Table result = table.filter("age > 25").orderBy($("age").desc());
        Table result = tableEnv.sqlQuery("SELECT * FROM my_table WHERE age > 25 order by age desc");

        result.execute().print();
    }

    private static void testStreamWithKafka() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        // blink planner
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment bsTableEnv = StreamTableEnvironment.create(env, settings);

        // Step 1: 创建 StreamExecutionEnvironment 和 StreamTableEnvironment
        // Create a Table using SQL DDL
        bsTableEnv.executeSql(
                "CREATE TABLE my_table (" +
                        "  id INT, " +
                        "  name STRING, " +
                        "  age INT, " +
                        "  gender STRING, " +
                        "  event_time TIMESTAMP(3), " +
                        "  WATERMARK FOR event_time AS event_time - INTERVAL '5' SECOND" +
                        ") WITH (" +
                        "  'connector' = 'kafka',\n" +
                        "  'topic' = 'test1',\n" +
                        "  'properties.bootstrap.servers' = '192.168.0.35:30092,192.168.0.35:30093,192.168.0.35:30094',\n" +
                        "  'properties.group.id' = 'testGroup',\n" +
                        "  'scan.startup.mode' = 'earliest-offset',\n" +
                        "  'format' = 'csv',\n" +
                        "  'properties.security.protocol' = 'SASL_PLAINTEXT', -- 使用 SASL 认证的通信协议\n" +
                        "  'properties.sasl.mechanism' = 'SCRAM-SHA-256',-- SASL 机制（如 PLAIN、SCRAM-SHA-256、SCRAM-SHA-512）\n" +
                        "  'properties.sasl.jaas.config' = 'org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user1\" password=\"QfGVMFCTFy\";'" +
                        ")"
        );


        // Perform a query
        Table result = bsTableEnv.sqlQuery(
//                "SELECT * \n" +
//                "FROM (\n" +
//                "  SELECT *, ROW_NUMBER() OVER (PARTITION BY TUMBLE(event_time, INTERVAL '5' SECOND) ORDER BY event_time DESC) AS row_num\n" +
//                "  FROM my_table\n" +
//                ")\n" +
//                "WHERE row_num <= 10"
//                "SELECT id, name, event_time FROM my_table WINDOW TUMBLING (event_time, INTERVAL '5' SECOND) ORDER BY id ASC"
                "SELECT \n" +
                        "  id, \n" +
                        "  name, \n" +
                        "  TUMBLE_START(event_time, INTERVAL '5' SECOND) AS window_start,\n" +
                        "  TUMBLE_END(event_time, INTERVAL '5' SECOND) AS window_end\n" +
                        "FROM my_table\n" +
                        "GROUP BY TUMBLE(event_time, INTERVAL '5' SECOND), id, name\n"
//                        "ORDER BY id ASC"
        );


//        Table result = bsTableEnv.sqlQuery("SELECT * FROM my_table WHERE age > 25");
//        String explanation = result.explain();
//        System.out.println("Execution Plan:\n" + explanation);
        result.execute().print();

        // Execute the environment
//        env.execute("Flink Streaming Sort Example");
    }
}
