package com.linkflow.flink.dev;

import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
/**
 * @author david.duan
 * @packageName PACKAGE_NAME
 * @className FlinkTimeDemo
 * @date 2024/12/15
 * @description
 */
public class FlinkTimeDemo {
    private static final Logger logger = Logger.getLogger(FlinkTimeDemo.class);

    static {
        // Set log level dynamically
        org.apache.log4j.LogManager.getRootLogger().setLevel(Level.ERROR);
    }

    public static void main(String[] args) throws Exception {

        testProcessingTime();
//        testIngestionTime();
    }

    /**
     * 使用processing time 不能使用有限流，因为有限流的数据可能在一瞬间就加载完成，这时候还没有到窗口期的时间(比如说5s，也就是说数据加载完成还不足5s)，这时候就不会触发窗口计算
     * @throws Exception
     */
    public static void testProcessingTime() throws Exception {
        // 创建 Flink 环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        // 设置并行度
        env.setParallelism(1);
        DataStreamSource<String> stringDataStreamSource = env.addSource(new SourceFunction<String>() {
            @Override
            public void run(SourceFunction.SourceContext<String> ctx) throws Exception {
                for (int i=1;;) {
                    ctx.collect("event" + i);
                    i++;
                    Thread.sleep(1000); // 模拟无限数据流
                }
            }

            @Override
            public void cancel() {}
        });

//        DataStreamSource<String> stringDataStreamSource = env.socketTextStream("localhost", 9999);

        //打印收到的数据
  /*      stringDataStreamSource.map(event -> {
            System.out.println("Received: " + event);
            return event;
        });*/

        //但因数据的process time，其实就是当前系统时间 System.currentTimeMillis()
/*        stringDataStreamSource.process(new ProcessFunction<String, String>() {
            @Override
            public void processElement(String value, Context ctx, Collector<String> out) {
                long processTime = ctx.timerService().currentProcessingTime();
                out.collect("Value: " + value + ", Process Time: " + processTime);
            }
        }).print();*/

        stringDataStreamSource
                .timeWindowAll(Time.seconds(5)) // Processing Time 窗口
                .process(new ProcessAllWindowFunction<String, String, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<String> elements, Collector<String> out) {
                        long processTime = System.currentTimeMillis(); // 获取 Processing Time
                        out.collect("Window Process Time: " + processTime + ", Elements: " + elements);
                    }
                }).print();

        // 创建数据流（模拟输入数据）
/*        SingleOutputStreamOperator<String> process = stringDataStreamSource
//                .assignTimestampsAndWatermarks(WatermarkStrategy.noWatermarks()) // 不需要水位线
                .timeWindowAll(Time.seconds(5)) // 使用 Processing Time 窗口
                .process(new ProcessAllWindowFunction<String, String, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<String> elements, Collector<String> out) throws InterruptedException {
                        out.collect("Processing Time Window: " + elements);
                    }
                });*/
//        process.print();

        env.execute("Processing Time Example");
    }

    public static void testIngestionTime() throws Exception {
        // 创建 Flink 环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置时间特性为 Ingestion Time
        env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);

        // 创建数据流（模拟输入数据）
        SingleOutputStreamOperator<String> process = env.fromElements("event1", "event2", "event3")
                .timeWindowAll(Time.seconds(10)) // 使用 Ingestion Time 窗口
                .process(new ProcessAllWindowFunction<String, String, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<String> elements, Collector<String> out) throws InterruptedException {
                        out.collect("Ingestion Time Window: " + elements);
//                        Thread.sleep(200);
                    }
                });
        process.print();
        env.execute("Ingestion Time Example");
    }
}
