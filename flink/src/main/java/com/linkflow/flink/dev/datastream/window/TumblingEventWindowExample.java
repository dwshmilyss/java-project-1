package com.linkflow.flink.dev.datastream.window;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.OutputTag;

public class TumblingEventWindowExample {
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
        testAllowedLatenessAndOutputLateData();
//        testAllowedLateness();
    }

    /**
     * 测试watermark
     * @throws Exception
     */
    public static void testWatermark() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStream<String> socketStream = env.socketTextStream("localhost", 9999);
        DataStream<Tuple2<String, Long>> resultStream = socketStream.assignTimestampsAndWatermarks(new BoundedOutOfOrdernessGenerator())
                // Time.seconds(3) 为延时3s,如果是seconds(0) 那么就是没有延时，如果可以保障数据有序的话，那么这里就可以设置为0
                // 这和上面的 new BoundedOutOfOrdernessGenerator() 等价，BoundedOutOfOrdernessGenerator 只不过是开发者自己实现了 AssignerWithPeriodicWatermarks 接口
                // 而 BoundedOutOfOrdernessTimestampExtractor 则是实现了 AssignerWithPeriodicWatermarks 接口的抽象类
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
     * 本质上来说:BoundedOutOfOrdernessTimestampExtractor 和 allowedLateness 都是处理延迟数据的，只是形式不同
     * BoundedOutOfOrdernessTimestampExtractor(maxOutOfOrderness) 指定了窗口的触发时机，即 eventTime - maxOutOfOrderness <= 窗口期的结束时间 就会触发计算 关闭窗口
     * allowedLateness(lateness) 则是在窗口关闭后，允许延迟lateness，如果在关闭了窗口后，依然有eventTime <= watermark + maxOutOfOrderness + lateness 的数据过来，则会重新打开该窗口计算
     * 但是要注意，如果在这条延时数据过来之前，来了一条eventTime >= watermark + maxOutOfOrderness + lateness的数据(例如下面的25000数据)，那么这个窗口就会永久关闭，后面即使再来[10000,20000)之间的数据，也不能再计算
     * 示例： 窗口期[10000,20000)
     *
     * 10000 a
     * 24000 a -- 因为maxOutOfOrderness为3000(3s)，watermark为21000(24000-3000)>=20000(窗口结束), 触发窗口开始计算
     * 11> (a,1) -- 计算窗口 打印
     * 11000 -- 这时来了一条延迟数据11000，11000 <= 21000(当前watermark)
     * 11> (a,2) -- 重新打开窗口计算 打印
     * 25000 -- 这时来了一条25000的数据，watermark更新 = 25000-3000 = 22000 , 因为lateness设置为2s, 22000 >= 20000+2000(窗口结束期+lateness) 窗口[10000,20000)永久关闭
     * 12000 -- 这时再来一条延时数据，即使属于窗口期[10000,20000)，也不会重新在这个窗口计算了
     *
     * @throws Exception
     */
    public static void testAllowedLateness() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStream<String> socketStream = env.socketTextStream("localhost", 9999);
        DataStream<Tuple2<String, Long>> resultStream = socketStream
                // Time.seconds(3) 为延时3s,如果是seconds(0) 那么就是没有延时(为了解决乱序)，如果可以保障数据有序的话，那么这里就可以设置为0
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
                //10000 a 第一个迟到数据(a,1)，可以参与[10000~20000)的窗口计算，因为这时的watermark依然是20000，小于window_end_time + allowedLateness(20000+2000)
                //24000 a watermark=21000
                //11000 a 第二个迟到数据(a,2)，可以参与[10000~20000)的窗口计算，因为这时的watermark是21000，小于window_end_time + allowedLateness(20000+2000)
                //12000 a 第三个迟到数据(a,3)，可以参与[10000~20000)的窗口计算，因为这时的watermark是21000，小于window_end_time + allowedLateness(20000+2000)
                //25000 a watermark=22000
                //11000 a 第4个迟到数据，不可以参与[1000~20000)的窗口计算(丢失)，因为这时的watermark是22000，不小于window_end_time + allowedLateness(20000+2000)
                .allowedLateness(Time.seconds(2)) // 允许延迟处理2秒，即如果窗口计算已经触发，还有迟到的数据，那么允许迟到的数据为watermark < window_end_time + allowedLateness
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
     * 测试延时数据处理：AllowedLateness + sideOutputLateData(迟到的元素也以使用侧输出(side output)特性被重定向到另外的一条流中去)
     *
     * 10000
     * 24000
     * 11> (a,1)
     * 11000
     * 11> (a,2)
     * 25000
     * 12000
     * 11> (a,1)
     * sideOutputLateData 和 AllowedLateness 配合使用的结果就是如果超时数据也超出了 AllowedLateness 的范围，那么还可以输出到一个测流中补救
     * 如果单独使用 sideOutputLateData，不用 AllowedLateness 的话，那么就是只要超时的数据就会进入测流，不会有重新触发窗口计算的操作了
     * @throws Exception
     */
    public static void testAllowedLatenessAndOutputLateData() throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStream<String> socketStream = env.socketTextStream("localhost", 9999);
        //用一个侧输出流来保存被丢弃的数据
        OutputTag<Tuple2<String, Long>> outputTag = new OutputTag<Tuple2<String, Long>>("late-data"){};
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
                .sideOutputLateData(outputTag) // 收集延迟大于2s的数据
                .allowedLateness(Time.seconds(2)) //允许2s延迟
                // 延时数据处理的第三种方式，数据重定向
                .sideOutputLateData(outputTag)
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
