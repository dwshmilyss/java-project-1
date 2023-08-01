package com.linfflow.flink.dev.state;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

/**
 * @Description: TODO
 * @Author: David.duan
 * @Date: 2023/7/19
 **/
public class StateDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        System.setProperty("HADOOP_USER_NAME", "hdfs");
//        test1(env);
        test2(env);
    }

    /**
     * @Description: TODO
     * @Date: 2023/7/31
     * @Auther: David.duan
     * @Param null:
     **/
    public static void test2(StreamExecutionEnvironment env) throws Exception {
        env.enableCheckpointing(5000);
        env.getConfig().setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 10000));
//        env.setStateBackend(new FsStateBackend("hdfs://192.168.121.31:8020/dww/flink/checkpoint/error"));
        DataStreamSource<String> lines = env.socketTextStream("localhost", 9999);
        SingleOutputStreamOperator<String> out = lines.map(new MapFunction<String, String>() {
            @Override
            public String map(String line) throws Exception {
                if (line.startsWith("error")) {
                    throw new Exception("program exception,shutdown...");
                }
                return line.toUpperCase();
            }
        });
        out.print();
        env.execute();
    }

    /**
     * @Description: 通过一个ValueState和RichFunction 来实现数值的累加
     * @Date: 2023/7/31
     * @Auther: David.duan
     **/
    public static void test1(StreamExecutionEnvironment env) throws Exception {
        //开启checkpoint，5s是两次checkpoint的间隔时间 即下一次checkpoint会在上一次checkpoint结束5s后开始。默认把中间结果保存于JobMananger的内存
        env.enableCheckpointing(5000);
        //重启3次 每次间隔2s
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 2000));
        //程序异常退出，或者人为取消，不删除checkpoint目录数据
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
//        env.setStateBackend(new FsStateBackend("hdfs://hadoop-hdfs-namenode:9000/dww/flink/checkpoints"));
        env.setStateBackend(new FsStateBackend("hdfs://192.168.121.31:8020/dww/flink/checkpoint/valuestate"));
        env.setParallelism(4).fromElements(Tuple2.of(1L, 1L), Tuple2.of(1L, 2L), Tuple2.of(1L, 3L), Tuple2.of(1L, 4L))
                .keyBy(0).flatMap(new RichFlatMapFunction<Tuple2<Long, Long>, Object>() {
                    @Override
                    public void open(Configuration parameters) throws Exception {
                        ValueStateDescriptor<Tuple2<Long, Long>> descriptor = new ValueStateDescriptor<>("avg", TypeInformation.of(
                                new TypeHint<Tuple2<Long, Long>>() {
                                }
                        ), Tuple2.of(0L, 0L));
                        sum = getRuntimeContext().getState(descriptor);
                    }

                    private transient ValueState<Tuple2<Long, Long>> sum;

                    @Override
                    public void flatMap(Tuple2<Long, Long> value, Collector<Object> out) throws Exception {
                        Tuple2<Long, Long> currentSum = sum.value();
                        currentSum.f0 = value.f0;
                        currentSum.f1 += value.f1;
                        System.out.println(value.f0 + " + " + value.f1);
                        sum.update(currentSum);
                        out.collect(Tuple2.of(currentSum.f0, currentSum.f1));
//                        if (currentSum.f0 > 0) {
//                            System.out.println("-");
//                            out.collect(Tuple2.of(currentSum.f0, currentSum.f1));
//                            sum.clear();
//                        }
                    }

                }).print();
        env.execute();
    }
}
