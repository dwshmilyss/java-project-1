package com.yiban.influxdb.dev;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Demo1 {
    public static void main(String[] args) {
        InfluxDB influxDB = InfluxDBFactory.connect("http://10.21.3.77:8086");
//        InfluxDB influxDB = InfluxDBFactory.connect("http://10.21.3.77:8086", "admin", "admin123");
        String dbName = "test";
        String retentionPolicy = "rp_day_30";

        Point point1 = Point.measurement("cpu")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag("ip", "192.168.1.22")
                .addField("service", "serviceB")
                .addField("used", 0.14)
                .build();

        Point point2 = Point.measurement("memory")
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .tag("host", "192.168.1.22")
                .addField("service", "serviceB")
                .addField("used", 67821)
                .build();

        influxDB.write(dbName, retentionPolicy, point1);
        influxDB.write(dbName, retentionPolicy, point2);


        Query query = new Query("SELECT * FROM cpu", dbName);
        QueryResult queryResult = influxDB.query(query);
        List<QueryResult.Result> list = queryResult.getResults();
        for (QueryResult.Result result : list) {
            System.out.println("------------------");

            List<QueryResult.Series> l = result.getSeries();

            for (QueryResult.Series series : l) {
                System.out.println("Measurement Name: " + series.getName());

                Map<String, String> map = series.getTags();
                if (map != null) {
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        System.out.println("tag: " + entry.getKey() + ", value: " + entry.getValue());
                    }
                }

                List<String> columns = series.getColumns();
                for (String column : columns) {
                    System.out.println("column name: " + column);
                }

                List<List<Object>> values = series.getValues();
                for (List<Object> ll : values) {
                    for (Object obj : ll) {
                        System.out.println("Object type: " + obj.getClass() + ", value: " + String.valueOf(obj));
                    }
                }
            }
        }
        //influxDB.deleteDatabase(dbName);
    }
}
