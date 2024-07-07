package com.linfflow.flink.dev.datastream.window;

import com.linfflow.flink.dev.pojo.WaterSensor;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.api.common.functions.AggregateFunction;

import java.time.Duration;

/**
 * @author david.duan
 * @packageName com.linfflow.flink.dev.window.pojo
 * @className WindowReduceDemo
 * @date 2024/7/6
 * @description
 */
public class WindowReduceDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        DataStreamSource<WaterSensor> stream = env.fromElements(
                new WaterSensor("sensor_1", 1547718110L, 1),
                new WaterSensor("sensor_2", 1547718111L, 2),
                new WaterSensor("sensor_1", 1547718112L, 3),
                new WaterSensor("sensor_2", 1547718113L, 4));

        SingleOutputStreamOperator<WaterSensor> watermarkStream =  stream.assignTimestampsAndWatermarks(WatermarkStrategy.<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(3)).withTimestampAssigner(new SerializableTimestampAssigner<WaterSensor>() {
            @Override
            public long extractTimestamp(WaterSensor element, long recordTimestamp) {
                System.out.println("element.getTs() = " + element.getTs() + ", recordTimestamp = " + recordTimestamp);
                return element.getTs();
            }
        }));
        //reduce 输入类型和输出类型必须一致
        watermarkStream.keyBy(r -> r.getId())
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .reduce(new ReduceFunction<WaterSensor>() {

                    @Override
                    public WaterSensor reduce(WaterSensor value1, WaterSensor value2) throws Exception {
                        System.out.println("调用reduce方法，之前的结果:"+value1 + ",现在来的数据:"+value2);
                        return new WaterSensor(value1.getId(),System.currentTimeMillis(), value1.getVc() + value2.getVc());
                    }
                }).print();

        //aggregate 比reduce更灵活 输入类型可以和输出类型不一致
        watermarkStream.keyBy(r -> r.getId())
                .window(TumblingEventTimeWindows.of(Time.seconds(10)))
                .aggregate(new AggregateFunction<WaterSensor,Integer, String>() {

                    @Override
                    public Integer createAccumulator() {
                        System.out.println("创建累加器");
                        return 0;
                    }

                    @Override
                    public Integer add(WaterSensor value, Integer accumulator) {
                        System.out.println("调用add方法,value="+value);
                        return accumulator + value.getVc();
                    }

                    @Override
                    public String getResult(Integer accumulator) {
                        System.out.println("调用getResult方法");
                        return accumulator.toString();
                    }

                    @Override
                    public Integer merge(Integer a, Integer b) {
                        System.out.println("调用merge方法");
                        return a + b;
                    }
                }).print();
        env.execute();
    }
}
