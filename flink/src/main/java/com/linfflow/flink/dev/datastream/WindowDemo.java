package com.linfflow.flink.dev.datastream;

import com.linfflow.flink.dev.datastream.entity.DriverMileages;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.ProcessingTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * @Description: 各种时间窗口
 * @Author: David.duan
 * @Date: 2023/8/1
 * 主要有两种类型的窗口：
 * 1、window 在keyBy后面调用，根据并行度将不同的key落在不同的task窗口中
 * 2、windowAll 非keyBy算子后面调用 所有的数据都在一个task窗口中计算
 **/
public class WindowDemo {
    public static void main(String[] args) throws Exception {
        int port = 9999;
        String hostname = "localhost";
        String delimiter = "\n";
//        testTumbling(port,hostname,delimiter);
//        testSlidingTumbling(port, hostname, delimiter);
//        testSessionWindow(port, hostname, delimiter);
        testCountWindow(port, hostname, delimiter);
//        testSlidingCountWindow(port, hostname, delimiter);
    }

    /**
     * @Description: 滚动时间窗口
     * .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
     * 简写方式：.timeWindow(Time.seconds(5))
     * 每隔5s计算一下当前窗口的数据(即窗口size=5s)
     * @Date: 2023/8/1
     * @Auther: David.duan
     * @Param args:
     * nc -lk 9999
     * 1001,20,1605419689100
     * 1002,25,1605419690100
     * 1001,20,1605419689100
     **/
    public static void testTumbling(int port, String hostname, String delimiter) throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        environment.setParallelism(1);
        //连接socket获取输入数据
        DataStreamSource<String> data = environment.socketTextStream(hostname, port, delimiter);

        //字符串数据转换为实体类
        data.map(new MapFunction<String, DriverMileages>() {
                    @Override
                    public DriverMileages map(String value) throws Exception {
                        String[] split = value.split(",");
                        DriverMileages driverMileages = new DriverMileages();
                        driverMileages.driverId = split[0];
                        driverMileages.currentMileage = Double.parseDouble(split[1]);
                        driverMileages.timestamp = Long.parseLong(split[2]);
                        return driverMileages;
                    }
                })
                .keyBy(DriverMileages::getDriverId)
                .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                //.timeWindow(Time.seconds(5))
                .sum("currentMileage")
                .print();

        //启动计算任务
        environment.execute("window demo");
    }

    /**
     * @Description: 滑动窗口
     * SlidingProcessingTimeWindows.of(Time.seconds(5), Time.seconds(2)) 每隔2s计算过去5s的数据
     * 简写: .timeWindow(Time.seconds(5),Time.seconds(2))
     * @Date: 2023/8/1
     * @Auther: David.duan
     **/
    public static void testSlidingTumbling(int port, String hostname, String delimiter) throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        environment.setParallelism(1);
        //连接socket获取输入数据
        DataStreamSource<String> data = environment.socketTextStream(hostname, port, delimiter);

        //字符串数据转换为实体类
        data.map(new MapFunction<String, DriverMileages>() {
                    @Override
                    public DriverMileages map(String value) throws Exception {
                        String[] split = value.split(",");
                        DriverMileages driverMileages = new DriverMileages();
                        driverMileages.driverId = split[0];
                        driverMileages.currentMileage = Double.parseDouble(split[1]);
                        driverMileages.timestamp = Long.parseLong(split[2]);
                        return driverMileages;
                    }
                })
                .keyBy(DriverMileages::getDriverId)
                .window(SlidingProcessingTimeWindows.of(Time.seconds(5), Time.seconds(2)))
                //.timeWindow(Time.seconds(5),Time.seconds(2))
                .sum("currentMileage")
                .print();

        //启动计算任务
        environment.execute("window demo");
    }

    /**
     * @Description: 会话窗口 每隔10s如果没有数据进来了则开启一次计算
     * .window(ProcessingTimeSessionWindows.withGap(Time.seconds(10)))
     * @Date: 2023/8/1
     * @Auther: David.duan
     **/
    public static void testSessionWindow(int port, String hostname, String delimiter) throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        environment.setParallelism(1);
        //连接socket获取输入数据
        DataStreamSource<String> data = environment.socketTextStream(hostname, port, delimiter);

        //字符串数据转换为实体类
        data.map(new MapFunction<String, DriverMileages>() {
                    @Override
                    public DriverMileages map(String value) throws Exception {
                        String[] split = value.split(",");
                        DriverMileages driverMileages = new DriverMileages();
                        driverMileages.driverId = split[0];
                        driverMileages.currentMileage = Double.parseDouble(split[1]);
                        driverMileages.timestamp = Long.parseLong(split[2]);
                        return driverMileages;
                    }
                })
                .keyBy(DriverMileages::getDriverId)
                .window(ProcessingTimeSessionWindows.withGap(Time.seconds(10)))
                .sum("currentMileage")
                .print();

        //启动计算任务
        environment.execute("window demo1");
    }

    /**
     * @Description: 滚动计算窗口
     * countWindow(3) 每3条数据开启每一次计算
     * @Date: 2023/8/1
     * @Auther: David.duan
     **/
    public static void testCountWindow(int port, String hostname, String delimiter) throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        environment.setParallelism(1);
        //连接socket获取输入数据
        DataStreamSource<String> data = environment.socketTextStream(hostname, port, delimiter);

        //字符串数据转换为实体类
        data.map(new MapFunction<String, DriverMileages>() {
                    @Override
                    public DriverMileages map(String value) throws Exception {
                        String[] split = value.split(",");
                        DriverMileages driverMileages = new DriverMileages();
                        driverMileages.driverId = split[0];
                        driverMileages.currentMileage = Double.parseDouble(split[1]);
                        driverMileages.timestamp = Long.parseLong(split[2]);
                        return driverMileages;
                    }
                })
                .keyBy(DriverMileages::getDriverId)
                .countWindow(3)
                .sum("currentMileage")
                .print();
        //启动计算任务
        environment.execute("window demo1");
    }

    /**
     * @Description: 滑动计数窗口
     * countWindow(3,1) 每一条数据就开启一个窗口计算前面三次的数据
     * @Date: 2023/8/1
     * @Auther: David.duan
     * @Param port:
     * @Param hostname:
     * @Param delimiter:
     **/
    public static void testSlidingCountWindow(int port, String hostname, String delimiter) throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        environment.setParallelism(1);
        //连接socket获取输入数据
        DataStreamSource<String> data = environment.socketTextStream(hostname, port, delimiter);

        //字符串数据转换为实体类
        data.map(new MapFunction<String, DriverMileages>() {
                    @Override
                    public DriverMileages map(String value) throws Exception {
                        String[] split = value.split(",");
                        DriverMileages driverMileages = new DriverMileages();
                        driverMileages.driverId = split[0];
                        driverMileages.currentMileage = Double.parseDouble(split[1]);
                        driverMileages.timestamp = Long.parseLong(split[2]);
                        return driverMileages;
                    }
                })
                .keyBy(DriverMileages::getDriverId)
                .countWindow(3, 1)
                .sum("currentMileage")
                .print();
        //启动计算任务
        environment.execute("window demo1");
    }
}
