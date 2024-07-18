package com.linkflow.flink.dev.datastream.window;

import com.linkflow.flink.dev.pojo.WaterSensor;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.time.Duration;

public class WatermarkDemo {
    /**
     * nc -lk 9999
     * 11000 a
     * 12000 b
     * 13000 a
     * 19888 a
     * 19999 a
     * 20000 b
     * 23000 a watermark=23000-3000 = 20000 >= window_end_time [0~10000),[10000~20000)，区间为左闭右开，注意，如果maxOutOfOrderness=0,那么19999就会触发[10000~20000)的窗口计算
     * res: (a,4) (b,1) 因为20000已经属于[20000~30000)的窗口了，所以不在这次的窗口计算范围内
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
//        testWatermarkAndAllowedLatenessAndOutputLateData();
//        testWatermarkAndAllowedLateness();
//        testWithOrdernessStream();
        testWithoutOrdernessStream();
    }

    /**
     * 对于有序流，时间戳单调递增，不存在乱序、迟到的情况，直接调用WatermarkStrategy.forMonotonousTimestamps()方法即可实现
     *
     * @throws Exception
     */
    public static void testWithOrdernessStream() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        SingleOutputStreamOperator<WaterSensor> source = env
                .fromElements(
                        new WaterSensor("sensor_1", 1547718110L, 1),
                        new WaterSensor("sensor_2", 1547718111L, 2),
                        new WaterSensor("sensor_1", 1547718112L, 3),
                        new WaterSensor("sensor_2", 1547718113L, 4));

        // TODO 1.定义Watermark策略
        WatermarkStrategy<WaterSensor> watermarkStrategy = WatermarkStrategy
                // 1.1 指定watermark生成：升序的watermark，没有等待时间
                .<WaterSensor>forMonotonousTimestamps()
                // 1.2 指定 时间戳分配器，从数据中提取
                .withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                    @Override
                    public long extractTimestamp(WaterSensor waterSensor, long l) {
                        System.out.println("数据=" + waterSensor + ",recordTs=" + l);
                        // 返回的时间戳单位为毫秒
                        return waterSensor.getTs() * 1000L;
                    }
                });

        // TODO 2. 指定 watermark策略
        SingleOutputStreamOperator<WaterSensor> watermark = source.assignTimestampsAndWatermarks(watermarkStrategy);

        KeyedStream<WaterSensor, String> keyBy = watermark.keyBy(
                new KeySelector<WaterSensor, String>() {
                    @Override
                    public String getKey(WaterSensor waterSensor) throws Exception {
                        return waterSensor.getId();
                    }
                }
        );

        // TODO 3.使用 事件时间语义 的窗口
        WindowedStream<WaterSensor, String, TimeWindow> sensorWS = keyBy.window(TumblingEventTimeWindows.of(Time.seconds(10)));

        SingleOutputStreamOperator<String> process = sensorWS.process(
                // IN,KEY,OUT,Window
                new ProcessWindowFunction<WaterSensor, String, String, TimeWindow>() {
                    @Override
                    public void process(String s, Context context, Iterable<WaterSensor> iterable, Collector<String> collector) throws Exception {
                        // 拿到窗口的开始时间、结束时间
                        long startTS = context.window().getStart();
                        long endTS = context.window().getEnd();
                        String start_time = DateFormatUtils.format(startTS, "yyyy-MM-dd HH:mm:ss.SSS");
                        String end_time = DateFormatUtils.format(endTS, "yyyy-MM-dd HH:mm:ss.SSS");
                        // 去除窗口的size
                        long count = iterable.spliterator().estimateSize();
                        collector.collect("key=" + s + "的窗口[" + start_time + "->" +
                                end_time + "),长度为" + count + "条数据---->" + iterable.toString());
                    }

                }
        );
        process.print();
        env.execute();
    }

    /**
     * 乱序流中需要等待迟到的数据，因此需要设置一个迟到时间，例如size为10s的窗口，延迟时间设置为3s，那么直到事件时间为13s的数据到达，才会促发[0,10)的窗口执行，
     * 调用WatermarkStrategy.forBoundedOutOfOrderness()方法就可以实现。
     * 这个方法需要传入一个maxOutOfOrderness参数，表示“最大乱序程度”，它表示数据流中乱序数据时间戳的最大差值,也就是等待的时间。
     *
     * @throws Exception
     */
    public static void testWithoutOrdernessStream() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        env.getConfig().setAutoWatermarkInterval(100L);
        //窗口从 [1547718110,1547718120)  watermark触发窗口计算的时间为(1547718120 + 3s = 1547718123),allowedLateness为3s，所以最迟的数据不能超过(1547718123+3s=1547718126L)。
        SingleOutputStreamOperator<WaterSensor> source = env
                .fromElements(
                        new WaterSensor("sensor_2", 1547718110L, 1),
                        new WaterSensor("sensor_2", 1547718111L, 2),
                        new WaterSensor("sensor_2", 1547718112L, 3),
                        new WaterSensor("sensor_2", 1547718123L, 4),
                        new WaterSensor("sensor_2", 1547718124L, 5),
                        new WaterSensor("sensor_2", 1547718126L, 6), //这里如果改为1547718126L  下面的那一条就不能参与计算了，因为过了allowedLateness的时间(1547718126)
                        new WaterSensor("sensor_2", 1547718114L, 7));


        //1. 定义Watermark策略，最大允许延迟3秒的数据到达
        WatermarkStrategy<WaterSensor> watermarkStrategy = WatermarkStrategy
                //1.1.1 和1.1.2实现同样的效果
                .forGenerator(new WatermarkGeneratorSupplier<WaterSensor>() {
                    @Override
                    public WatermarkGenerator<WaterSensor> createWatermarkGenerator(Context context) {
                        return new BoundedOutOfOrdernessWatermarks(Duration.ofSeconds(3L));
                    }
                })
                // 1.1.2 指定watermark生成：乱序，等待3s 相当于开窗时间延迟3s
//                .<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(3L))
                // 1.2 指定 时间戳分配器，从数据中提取
                .withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
                    @Override
                    public long extractTimestamp(WaterSensor waterSensor, long recordTimestamp) {
//                        System.out.println("数据 = " + waterSensor + ",recordTs=" + recordTimestamp);
                        //这里要返回毫秒
                        return waterSensor.getTs() * 1000L;
                    }
                });

        //2. 指定Watermark策略
        SingleOutputStreamOperator<WaterSensor> singleOutputStreamOperator = source.assignTimestampsAndWatermarks(watermarkStrategy);

        //由于用的是模拟的集合数据，flink加载的很快，而默认的watermark更新时间是200ms，如果不做处理，可能数据加载完成后watermark还没有更新，就会一直是-9223372036854775808
        SingleOutputStreamOperator<WaterSensor> tempStream = singleOutputStreamOperator.process(new ProcessFunction<WaterSensor, WaterSensor>() {
            @Override
            public void processElement(WaterSensor value, ProcessFunction<WaterSensor, WaterSensor>.Context ctx, Collector<WaterSensor> out) throws Exception {
                out.collect(value);
                Thread.sleep(100);
            }
        });

        //3. keyby
        KeyedStream<WaterSensor, String> keyedStream = tempStream.keyBy(new KeySelector<WaterSensor, String>() {
            @Override
            public String getKey(WaterSensor waterSensor) throws Exception {
                return waterSensor.getId();
            }
        });
        keyedStream.process(new KeyedProcessFunction<String, WaterSensor, WaterSensor>() {
            @Override
            public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, WaterSensor>.Context ctx, Collector<WaterSensor> out) throws Exception {
                System.out.println("value = " + value + ",watermark = " + ctx.timerService().currentWatermark());
            }
        }).print();
        //定义一个侧输出流来保存窗口关闭之后到来的数据
        OutputTag<WaterSensor> waterSensorOutputTag = new OutputTag<WaterSensor>("late", TypeInformation.of(WaterSensor.class));
        //4. window
        WindowedStream<WaterSensor, String, TimeWindow> sensorWS = keyedStream
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .allowedLateness(Time.seconds(3L))//相当于窗口关闭时间延迟3s
                .sideOutputLateData(waterSensorOutputTag);


        //5. process 底层的window API，可以打印出窗口的一些信息
        SingleOutputStreamOperator<String> process = sensorWS.process(
                // IN,KEY,OUT,Window
                new ProcessWindowFunction<WaterSensor, String, String, TimeWindow>() {
                    @Override
                    public void process(String s, Context context, Iterable<WaterSensor> iterable, Collector<String> collector) throws Exception {
                        // 拿到窗口的开始时间、结束时间
                        long startTS = context.window().getStart();
                        long endTS = context.window().getEnd();
                        long currentWatermarkTS = context.currentWatermark();
                        String start_time = DateFormatUtils.format(startTS, "yyyy-MM-dd HH:mm:ss.SSS");
                        String end_time = DateFormatUtils.format(endTS, "yyyy-MM-dd HH:mm:ss.SSS");
//                        String currentWatermark = DateFormatUtils.format(currentWatermarkTS, "yyyy-MM-dd HH:mm:ss.SSS");
                        // 去除窗口的size
                        long count = iterable.spliterator().estimateSize();
                        collector.collect("key=" + s + "的窗口[" + start_time + "->" +
                                end_time + " , 当前水位:" + currentWatermarkTS + "),长度为" + count + "条数据---->" + iterable.toString());
                    }

                }
        );

/*        sensorWS.reduce(new ReduceFunction<WaterSensor>() {
            @Override
            public WaterSensor reduce(WaterSensor value1, WaterSensor value2) throws Exception {
                System.out.println("调用reduce方法，之前的结果:" + value1 + ",现在来的数据:" + value2);
                return new WaterSensor(value1.getId(), value2.getTs(), value1.getVc() + value2.getVc());
            }
        }).print();*/

        //6.逻辑处理完之后 如果侧输出流中有数据 还应该将侧流中的数据提取出来进行进一步的处理,调用.getSideOutput()方法，传入对应的侧输出标签，就可以获取到迟到数据所在的流了。
        //这里注意，getSideOutput()是 SingleOutputStreamOperator 的方法，获取到的侧输出流数据类型应该和 OutputTag 指定的类型一致，与窗口聚合之后流中的数据类型可以不同。
        DataStream<WaterSensor> lateStream = process.getSideOutput(waterSensorOutputTag);
        process.print();
        lateStream.print("侧输出流中的数据:");
        env.execute("testWithoutOrdernessStream");
    }

    /**
     * 测试watermark
     *
     * @throws Exception
     */
    public static void testWatermark() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStream<String> socketStream = env.socketTextStream("localhost", 9999);
        DataStream<Tuple2<String, Long>> resultStream = socketStream.assignTimestampsAndWatermarks(new BoundedOutOfOrdernessGenerator())
                // Time.seconds(3) 为延时3s,如果是seconds(0) 那么就是没有延时，如果可以保障数据有序的话，那么这里就可以设置为0
                /*.assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<String>(Time.seconds(3)) {
                    @Override
                    public long extractTimestamp(String element) {
                        long eventTime = Long.parseLong(element.split(" ")[0]);
                        System.out.println(eventTime);
                        return eventTime;
                    }
                })*/
                .map(new MapFunction<String, Tuple2<String, Long>>() {
                    @Override
                    public Tuple2<String, Long> map(String value) throws Exception {
                        return Tuple2.of(value.split(" ")[1], 1L);
                    }
                })
                .keyBy(0)
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                //延迟数据处理，flink有三种方式。默认是丢弃，这是第二种，重新触发窗口计算
                //23000 a 触发计算
                //10000 a 第一个迟到数据(a,1)，可以参与[1000~20000)的窗口计算，因为这时的watermark依然是20000，小于window_end_time + allowedLateness(20000+2000)
                //24000 a watermark=21000
                //11000 a 第二个迟到数据(a,2)，可以参与[1000~20000)的窗口计算，因为这时的watermark是21000，小于window_end_time + allowedLateness(20000+2000)
                //12000 a 第三个迟到数据(a,3)，可以参与[1000~20000)的窗口计算，因为这时的watermark是21000，小于window_end_time + allowedLateness(20000+2000)
                //25000 a watermark=22000
                //11000 a 第4个迟到数据，不可以参与[1000~20000)的窗口计算(丢失)，因为这时的watermark是22000，不小于window_end_time + allowedLateness(20000+2000)
//                .allowedLateness(Time.seconds(2)) // 允许延迟处理2秒，即如果窗口计算已经触发，还有迟到的数据，那么允许迟到的数据为watermark < window_end_time + allowedLateness
                // 延时数据处理的第三种方式，数据重定向
                .reduce(new ReduceFunction<Tuple2<String, Long>>() {
                    @Override
                    public Tuple2<String, Long> reduce(Tuple2<String, Long> value1, Tuple2<String, Long> value2) throws Exception {
                        return new Tuple2<>(value1.f0, value1.f1 + value2.f1);
                    }
                });
        resultStream.print();
        env.execute();
    }

    /**
     * 测试Watermark 和 延时数据处理(设置延时时间并重新打开窗口计算)
     * watermark只能保障在maxOutOfOrderness的有序和延时，如果超过了这个时间还有迟到的数据，就要用到延时数据处理了
     * <p>
     * 10000
     * 24000
     * 11> (a,1)
     * 11000
     * 11> (a,2)
     * 25000
     * 12000
     *
     * @throws Exception
     */
    public static void testWatermarkAndAllowedLateness() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStream<String> socketStream = env.socketTextStream("localhost", 9999);
        DataStream<Tuple2<String, Long>> resultStream = socketStream
                // Time.seconds(3) 为延时3s,如果是seconds(0) 那么就是没有延时，如果可以保障数据有序的话，那么这里就可以设置为0
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<String>(Time.seconds(3)) {
                    @Override
                    public long extractTimestamp(String element) {
                        long eventTime = Long.parseLong(element.split(" ")[0]);
                        System.out.println(eventTime);
                        return eventTime;
                    }
                })
                .map(new MapFunction<String, Tuple2<String, Long>>() {
                    @Override
                    public Tuple2<String, Long> map(String value) throws Exception {
                        return Tuple2.of(value.split(" ")[1], 1L);
                    }
                })
                .keyBy(0)
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                //延迟数据处理，flink有三种方式。默认是丢弃，这是第二种，重新触发窗口计算
                //23000 a 触发计算
                //10000 a 第一个迟到数据(a,1)，可以参与[1000~20000)的窗口计算，因为这时的watermark依然是20000，小于window_end_time + allowedLateness(20000+2000)
                //24000 a watermark=21000
                //11000 a 第二个迟到数据(a,2)，可以参与[1000~20000)的窗口计算，因为这时的watermark是21000，小于window_end_time + allowedLateness(20000+2000)
                //12000 a 第三个迟到数据(a,3)，可以参与[1000~20000)的窗口计算，因为这时的watermark是21000，小于window_end_time + allowedLateness(20000+2000)
                //25000 a watermark=22000
                //11000 a 第4个迟到数据，不可以参与[1000~20000)的窗口计算(丢失)，因为这时的watermark是22000，不小于window_end_time + allowedLateness(20000+2000)
                .allowedLateness(Time.seconds(3)) // 允许延迟处理3秒，即如果窗口计算已经触发，还有迟到的数据，那么允许迟到的数据为watermark < window_end_time + allowedLateness
                // 延时数据处理的第三种方式，数据重定向
                .reduce(new ReduceFunction<Tuple2<String, Long>>() {
                    @Override
                    public Tuple2<String, Long> reduce(Tuple2<String, Long> value1, Tuple2<String, Long> value2) throws Exception {
                        return new Tuple2<>(value1.f0, value1.f1 + value2.f1);
                    }
                });
        resultStream.print();
        env.execute();
    }


    /**
     * 测试Watermark 和 延时数据处理(迟到的元素也以使用侧输出(side output)特性被重定向到另外的一条流中去)
     * <p>
     * 10000
     * 24000
     * 11> (a,1)
     * 11000
     * 11> (a,2)
     * 25000
     * 12000
     * 11> (a,1)
     * OutputLateData 和 AllowedLateness 配合使用的结果就是如果超时数据也超出了 AllowedLateness 的范围，那么还可以输出到一个测流中补救
     * 如果单独使用 OutputLateData ，那么就是只要超时的数据就会进入测流，不会有重新触发窗口计算的操作了
     *
     * @throws Exception
     */
    public static void testWatermarkAndAllowedLatenessAndOutputLateData() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStream<String> socketStream = env.socketTextStream("localhost", 9999);
        //保存被丢弃的数据
        OutputTag<Tuple2<String, Long>> outputTag = new OutputTag<Tuple2<String, Long>>("late-data") {
        };
        SingleOutputStreamOperator<Tuple2<String, Long>> resultStream = socketStream
                // Time.seconds(3) 为延时3s,如果是seconds(0) 那么就是没有延时，如果可以保障数据有序的话，那么这里就可以设置为0
                .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<String>(Time.seconds(3)) {
                    @Override
                    public long extractTimestamp(String element) {
                        long eventTime = Long.parseLong(element.split(" ")[0]);
                        System.out.println(eventTime);
                        return eventTime;
                    }
                }).setParallelism(1)//assignTimestampsAndWatermarks就是一个普通的算子，当然也可以设置并行度
                .map(new MapFunction<String, Tuple2<String, Long>>() {
                    @Override
                    public Tuple2<String, Long> map(String value) throws Exception {
                        return Tuple2.of(value.split(" ")[1], 1L);
                    }
                })
                .keyBy(0)
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .allowedLateness(Time.seconds(2)) //允许2s延迟
                .sideOutputLateData(outputTag) // 收集延迟大于2s的数据
                // 延时数据处理的第三种方式，数据重定向
                .reduce(new ReduceFunction<Tuple2<String, Long>>() {
                    @Override
                    public Tuple2<String, Long> reduce(Tuple2<String, Long> value1, Tuple2<String, Long> value2) throws Exception {
                        return new Tuple2<>(value1.f0, value1.f1 + value2.f1);
                    }
                });
        resultStream.print();
        //把迟到的数据暂时打印到控制台，实际中可以保存到其他存储介质中
        DataStream<Tuple2<String, Long>> sideOutput = resultStream.getSideOutput(outputTag);
        sideOutput.print();
        env.execute();
    }


    /**
     * 等价于
     * .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<String>(Time.seconds(3)) {
     *
     * @Override public long extractTimestamp(String element) {
     * long eventTime = Long.parseLong(element.split(" ")[0]);
     * System.out.println(eventTime);
     * return eventTime;
     * }
     * })
     */
    static class BoundedOutOfOrdernessGenerator implements AssignerWithPeriodicWatermarks<String> {

        private final long maxOutOfOrderness = 3000; // 3.0 seconds

        private long currentMaxTimestamp;

        @Override
        public long extractTimestamp(String element, long previousElementTimestamp) {
            long timestamp = Long.parseLong(element.split(" ")[0]);
            currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
            System.out.println("eventTime = " + timestamp + ",currentMaxTimestamp = " + currentMaxTimestamp);
            return timestamp;
        }

        @Override
        public Watermark getCurrentWatermark() {
            // return the watermark as current highest timestamp minus the out-of-orderness bound
            // 以迄今为止收到的最大时间戳来生成 watermark
            return new Watermark(currentMaxTimestamp - maxOutOfOrderness);
        }
    }

    /**
     * 直接按(processTime - maxTimeLag)来触发窗口
     */
    class TimeLagWatermarkGenerator implements AssignerWithPeriodicWatermarks<String> {

        private final long maxTimeLag = 3000; // 3 seconds

        @Override
        public long extractTimestamp(String element, long previousElementTimestamp) {
            long timestamp = Long.parseLong(element.split(" ")[0]);
            System.out.println("eventTime = " + timestamp);
            return timestamp;
        }

        @Override
        public Watermark getCurrentWatermark() {
            // return the watermark as current time minus the maximum time lag
            return new Watermark(System.currentTimeMillis() - maxTimeLag);
        }
    }

}
