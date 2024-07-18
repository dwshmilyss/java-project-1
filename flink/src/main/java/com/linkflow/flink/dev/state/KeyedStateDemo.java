package com.linkflow.flink.dev.state;

import com.linkflow.flink.dev.functions.WaterSensorMapFunction;
import com.linkflow.flink.dev.pojo.WaterSensor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description: TODO
 * @Author: David.duan
 * @Date: 2023/7/19
 **/
public class KeyedStateDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        System.setProperty("HADOOP_USER_NAME", "hdfs");
//        test1(env);
//        test2(env);
        testValueState(env);
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
        //设置状态的后端存储
        env.setStateBackend(new FsStateBackend("hdfs://192.168.121.31:8020/dww/flink/checkpoint/valuestate"));
        //下面这两个backend从1.13才开始有
//        env.setStateBackend(new EmbeddedRocksDBStateBackend());
//        env.setStateBackend(new HashMapStateBackend());

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

    /**
     * 案例需求：检测每种传感器的水位值，如果连续的两个水位值超过10，就输出报警。
     *
     * @param env:StreamExecutionEnvironment return void
     * @author david.duan on 2024/7/16 19:40
     * {@link }
     * @description 利用flink ValueState 实现: 检测每种传感器的水位值，如果连续的两个水位值超过10，就输出报警
     * ValueState : 状态中只保存一个“值”（value）。ValueState本身是一个接口，源码中定义如下:
     * public interface ValueState<T> extends State {
     * T value() throws IOException; //获取当前状态的值
     * void update(T value) throws IOException; //对状态进行更新，传入的参数value就是要覆写的状态值
     * }
     * 在具体使用时，为了让运行时上下文清楚到底是哪个状态，我们还需要创建一个“状态描述器”（StateDescriptor）来提供状态的基本信息
     * public ValueStateDescriptor(String name, Class<T> typeClass) {
     * super(name, typeClass, null);
     * }
     * 这里需要传入状态的名称和类型——这跟我们声明一个变量时做的事情完全一样
     */
    public static void testValueState(StreamExecutionEnvironment env) throws Exception {
        env.setParallelism(1);
        SingleOutputStreamOperator<WaterSensor> sensorDS = getWaterSensorSingleOutputStreamOperator(env);

        sensorDS.keyBy(r -> r.getId())
                .process(
                        new KeyedProcessFunction<String, WaterSensor, String>() {

                            // TODO 1.定义状态
                            ValueState<Integer> lastVcState;


                            @Override
                            public void open(Configuration parameters) throws Exception {
                                super.open(parameters);
                                // TODO 2.在open方法中，初始化状态
                                // 状态描述器两个参数：第一个参数，起个名字，不重复；第二个参数，存储的类型
                                lastVcState = getRuntimeContext().getState(new ValueStateDescriptor<Integer>("lastVcState", Types.INT));
                            }

                            @Override
                            public void processElement(WaterSensor value, Context ctx, Collector<String> out) throws Exception {
//                                lastVcState.value();  // 取出 本组 值状态 的数据
//                                lastVcState.update(); // 更新 本组 值状态 的数据
//                                lastVcState.clear();  // 清除 本组 值状态 的数据


                                // 1. 取出上一条数据的水位值(Integer默认值是null，判断)
                                int lastVc = lastVcState.value() == null ? 0 : lastVcState.value();
                                // 2. 求差值的绝对值，判断是否超过10
                                Integer vc = value.getVc();
                                if (Math.abs(vc - lastVc) > 10) {
                                    out.collect("传感器=" + value.getId() + "==>当前水位值=" + vc + ",与上一条水位值=" + lastVc + ",相差超过10！！！！");
                                }
                                // 3. 更新状态里的水位值
                                lastVcState.update(vc);
                            }
                        }
                )
                .print();

        env.execute();
    }

    private static SingleOutputStreamOperator<WaterSensor> getWaterSensorSingleOutputStreamOperator(StreamExecutionEnvironment env) {
        SingleOutputStreamOperator<WaterSensor> sensorDS = env
                .socketTextStream("localhost", 9999)
                .map(new WaterSensorMapFunction())
                .assignTimestampsAndWatermarks(WatermarkStrategy
                        .<WaterSensor>forBoundedOutOfOrderness(Duration.ofSeconds(3L))
                        .withTimestampAssigner(((element, recordTimestamp) -> element.getTs() * 1000L)));
        return sensorDS;
    }

    /**
     * @param env: return void
     * @author david.duan on 2024/7/17 09:54
     * {@link }
     * @description 针对每种传感器输出最高的3个水位值
     * ListState的一些常用API:
     * vcListState.get();    //取出 list状态 本组的数据，是一个Iterable
     * vcListState.add();    // 向 list状态 本组 添加一个元素
     * vcListState.addAll(); // 向 list状态 本组 添加多个元素
     * vcListState.update(); // 更新 list状态 本组数据（覆盖）
     * vcListState.clear();  // 清空List状态 本组数据
     */
    public static void testListState(StreamExecutionEnvironment env) throws Exception {
        env.setParallelism(1);
        SingleOutputStreamOperator<WaterSensor> sensorDS = getWaterSensorSingleOutputStreamOperator(env);
        sensorDS.keyBy(r -> r.getId())
                .process(
                        new KeyedProcessFunction<String, WaterSensor, String>() {
                            @Override
                            public void open(Configuration parameters) throws Exception {
                                super.open(parameters);
                                vcListState = getRuntimeContext().getListState(new ListStateDescriptor<Integer>("vcListState", Types.INT));
                            }

                            ListState<Integer> vcListState;

                            @Override
                            public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception {
                                //1. 来一条就存到 ListState 里
                                vcListState.add(value.getVc());
                                // 2.从list状态拿出来(Iterable)， 拷贝到一个List中，排序， 只留3个最大的
                                Iterable<Integer> vcListIterable = vcListState.get();
                                // 2.1 拷贝到List中
                                List<Integer> vcList = new ArrayList<>();
                                for (Integer vc : vcListIterable) {
                                    vcList.add(vc);
                                }
                                // 2.2 对List进行降序排序
                                vcList.sort((o1, o2) -> o2 - o1);
                                // 2.3 只保留最大的3个(list中的个数一定是连续变大，一超过3个就立即清理即可)
                                if (vcList.size() > 3) {
                                    // 将最后一个元素清除（第4个）
                                    vcList.remove(3);
                                }
                                out.collect("传感器id为" + value.getId() + ",最大的3个水位值=" + vcList.toString());
                                // 3.更新list状态
                                vcListState.update(vcList);
                            }
                        }
                ).print();
        env.execute();
    }

    /**
     * MapState 把一些键值对（key-value）作为状态整体保存起来，可以认为就是一组key-value映射的列表。对应的MapState<UK, UV>接口中，就会有UK、UV两个泛型，分别表示保存的key和value的类型。同样，MapState提供了操作映射状态的方法，与Map的使用非常类似。
     * 案例需求：统计每种水位值出现的次数。
     * 原文链接：https://blog.csdn.net/qq_43048957/article/details/135220819
     *
     * @param env
     * @throws Exception
     */
    public static void testMapState(StreamExecutionEnvironment env) throws Exception {
        env.setParallelism(1);
        SingleOutputStreamOperator<WaterSensor> sensorDS = getWaterSensorSingleOutputStreamOperator(env);

        sensorDS.keyBy(r -> r.getId())
                .process(
                        new KeyedProcessFunction<String, WaterSensor, String>() {
                            MapState<Integer, Integer> vcCountMapState;

                            @Override
                            public void open(Configuration parameters) throws Exception {
                                super.open(parameters);
                                vcCountMapState = getRuntimeContext().getMapState(new MapStateDescriptor<Integer, Integer>("vcCountMapState", Types.INT, Types.INT));
                            }

                            @Override
                            public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception {
                                // 1.判断是否存在vc对应的key
                                Integer vc = value.getVc();
                                if (vcCountMapState.contains(vc)) {
                                    Integer count = vcCountMapState.get(vc);
                                    vcCountMapState.put(vc, count++);
                                } else {
                                    vcCountMapState.put(vc, 1);
                                }
                                // 2.遍历Map状态，输出每个k-v的值
                                StringBuilder outStr = new StringBuilder();
                                outStr.append("======================================\n");
                                outStr.append("传感器id为:" + value.getId() + "\n");
                                for (Map.Entry<Integer, Integer> vcCount : vcCountMapState.entries()) {
                                    outStr.append(vcCount.toString() + "\n");
                                }
                                outStr.append("======================================\n");

                                out.collect(outStr.toString());
//                                vcCountMapState.get();          // 对本组的Map状态，根据key，获取value
//                                vcCountMapState.contains();     // 对本组的Map状态，判断key是否存在
//                                vcCountMapState.put(, );        // 对本组的Map状态，添加一个 键值对
//                                vcCountMapState.putAll();  // 对本组的Map状态，添加多个 键值对
//                                vcCountMapState.entries();      // 对本组的Map状态，获取所有键值对
//                                vcCountMapState.keys();         // 对本组的Map状态，获取所有键
//                                vcCountMapState.values();       // 对本组的Map状态，获取所有值
//                                vcCountMapState.remove();   // 对本组的Map状态，根据指定key，移除键值对
//                                vcCountMapState.isEmpty();      // 对本组的Map状态，判断是否为空
//                                vcCountMapState.iterator();     // 对本组的Map状态，获取迭代器
//                                vcCountMapState.clear();        // 对本组的Map状态，清空
                            }
                        }
                ).print();
        env.execute();
    }

    /**
     * 案例需求：利用 ReducingState 计算每种传感器的水位和
     *
     * 类似于值状态（Value），不过需要对添加进来的所有数据进行归约，将归约聚合之后的值作为状态保存下来。ReducingState这个接口调用的方法类似于ListState，只不过它保存的只是一个聚合值，
     * 所以调用.add()方法时，不是在状态列表里添加元素，而是直接把新数据和之前的状态进行归约，并用得到的结果更新状态。
     * 归约逻辑的定义，是在归约状态描述器（ReducingStateDescriptor）中，通过传入一个归约函数（ReduceFunction）来实现的。这里的归约函数，
     * 就是我们之前介绍reduce聚合算子时讲到的ReduceFunction，所以状态类型跟输入的数据类型是一样的
     * public ReducingStateDescriptor(
     *     String name, ReduceFunction<T> reduceFunction, Class<T> typeClass) {...}
     * 这里的描述器有三个参数，其中第二个参数就是定义了归约聚合逻辑的ReduceFunction，另外两个参数则是状态的名称和类型
     *
     * 原文链接：https://blog.csdn.net/qq_43048957/article/details/135220819
     * @param env
     * @throws Exception
     */
    public static void testReduceState(StreamExecutionEnvironment env) throws Exception {
        env.setParallelism(1);
        SingleOutputStreamOperator<WaterSensor> sensorDS = getWaterSensorSingleOutputStreamOperator(env);
        sensorDS.keyBy(r -> r.getId())
                .process(new KeyedProcessFunction<String, WaterSensor, String>() {
                    ReducingState<Integer> sumVcState;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
                        sumVcState = getRuntimeContext().getReducingState(new ReducingStateDescriptor<Integer>("sumVcState", Integer::sum, Types.INT));
                    }

                    @Override
                    public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception {
                        sumVcState.add(value.getVc());
                        StringBuilder outStr = new StringBuilder();
                        outStr.append("======================================\n");
                        outStr.append("传感器id为:" + value.getId() + ",的总和为:" + sumVcState.get() + "\n");
                        outStr.append("======================================\n");
                        out.collect(outStr.toString());
                    }
                }).print();
        env.execute();
    }

    /**
     * 案例需求：利用 AggregateState 计算每种传感器的平均水位
     *
     * 与归约状态非常类似，聚合状态也是一个值，用来保存添加进来的所有数据的聚合结果。与ReducingState不同的是，它的聚合逻辑是由在描述器中传入一个更加一般化的聚合函数（AggregateFunction）来定义的；
     * 这也就是之前我们讲过的AggregateFunction，里面通过一个累加器（Accumulator）来表示状态，所以聚合的状态类型可以跟添加进来的数据类型完全不同，使用更加灵活。
     * 同样地，AggregatingState接口调用方法也与ReducingState相同，调用.add()方法添加元素时，会直接使用指定的AggregateFunction进行聚合并更新状态。
     *
     * 原文链接：https://blog.csdn.net/qq_43048957/article/details/135220819
     *
     * @param env
     * @throws Exception
     */
    public static void testAggregateState(StreamExecutionEnvironment env) throws Exception {
        env.setParallelism(1);
        SingleOutputStreamOperator<WaterSensor> sensorDS = getWaterSensorSingleOutputStreamOperator(env);
        sensorDS.keyBy(r -> r.getId())
                .process(new KeyedProcessFunction<String, WaterSensor, String>() {

                    AggregatingState<Integer,Double> vcAvgAggregatingState;
                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
                        vcAvgAggregatingState = getRuntimeContext().getAggregatingState(
                                new AggregatingStateDescriptor<Integer, Tuple2<Integer, Integer>, Double>(
                                        "avgVcState",
                                        new AggregateFunction<Integer, Tuple2<Integer, Integer>, Double>() {
                                            @Override
                                            public Tuple2<Integer, Integer> createAccumulator() {
                                                return Tuple2.of(0, 0);
                                            }

                                            @Override
                                            public Tuple2<Integer, Integer> add(Integer value, Tuple2<Integer, Integer> accumulator) {
                                                return Tuple2.of(accumulator.f0 + value, accumulator.f1 + 1);
                                            }

                                            @Override
                                            public Double getResult(Tuple2<Integer, Integer> accumulator) {
                                                return accumulator.f0 * 1D / accumulator.f1;
                                            }

                                            @Override
                                            public Tuple2<Integer, Integer> merge(Tuple2<Integer, Integer> a, Tuple2<Integer, Integer> b) {
                                                return null;
                                            }
                                        },
                                        Types.TUPLE(Types.INT, Types.INT)
                                )
                        );
                    }

                    @Override
                    public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception {
                        // 将 水位值 添加到  聚合状态中
                        vcAvgAggregatingState.add(value.getVc());
                        // 从 聚合状态中 获取结果
                        Double vcAvg = vcAvgAggregatingState.get();

                        out.collect("传感器id为:" + value.getId() + ",平均水位值=" + vcAvg);

//                                vcAvgAggregatingState.get();    // 对 本组的聚合状态 获取结果
//                                vcAvgAggregatingState.add();    // 对 本组的聚合状态 添加数据，会自动进行聚合
//                                vcAvgAggregatingState.clear();  // 对 本组的聚合状态 清空数据
                    }
                }).print();
        env.execute();
    }

    /**
     * 测试状态的TTL(time-to-live 超时时间) 还是以ValueState 水位值>10就告警为例子
     *
     * @param env
     * @throws Exception
     */
    public static void testStateTTL(StreamExecutionEnvironment env) throws Exception {
        env.setParallelism(1);
        SingleOutputStreamOperator<WaterSensor> sensorDS = getWaterSensorSingleOutputStreamOperator(env);
        sensorDS.keyBy(r -> r.getId())
                .process(new KeyedProcessFunction<String, WaterSensor, String>() {
                    @Override
                    public void open(Configuration parameters) throws Exception {
                        super.open(parameters);
                        // TODO 1.创建 StateTtlConfig
                        StateTtlConfig stateTtlConfig = StateTtlConfig
                                .newBuilder(Time.seconds(5))
//                                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite) // 以状态的创建和写入(更新)为依据 更新过期时间
                                .setUpdateType(StateTtlConfig.UpdateType.OnReadAndWrite) // 以状态的读取、创建和写入(更新)为依据 更新过期时间，比OnCreateAndWrite更好
                                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired) //不返回过期的状态值
                                .build();

                        ValueStateDescriptor<Integer> stateDescriptor = new ValueStateDescriptor<>("lastVcState", Types.INT);
                        // TODO 2.状态描述器 启用 TTL
                        stateDescriptor.enableTimeToLive(stateTtlConfig);

                        this.lastVcState = getRuntimeContext().getState(stateDescriptor);
                    }

                    ValueState<Integer> lastVcState;

                    @Override
                    public void processElement(WaterSensor value, KeyedProcessFunction<String, WaterSensor, String>.Context ctx, Collector<String> out) throws Exception {
                        // 先获取状态值，打印 ==> 读取状态
                        Integer lastVc = lastVcState.value();
                        out.collect("key=" + value.getId() + ",状态值=" + lastVc);

                        // 如果水位大于10，更新状态值 ===> 写入状态
                        if (value.getVc() > 10) {
                            lastVcState.update(value.getVc());
                        }
                    }
                }).print();
        env.execute();
    }

}
