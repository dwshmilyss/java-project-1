package com.linkflow.flink.dev.state;

import com.linkflow.flink.dev.functions.WaterSensorMapFunction;
import com.linkflow.flink.dev.pojo.WaterSensor;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.datastream.BroadcastConnectedStream;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

/**
 * 算子状态（Operator State）就是一个算子并行实例上定义的状态，作用范围被限定为当前算子任务。
 * 算子状态跟数据的key无关，所以不同key的数据只要被分发到同一个并行子任务，就会访问到同一个Operator State。
 * 算子状态的实际应用场景不如Keyed State多，一般用在Source或Sink等与外部系统连接的算子上，或者完全没有key定义的场景。
 * 比如Flink的Kafka连接器中，就用到了算子状态。
 * 当算子的并行度发生变化时，算子状态也支持在并行的算子任务实例之间做重组分配。根据状态的类型不同，重组分配的方案也会不同。
 * 算子状态也支持不同的结构类型，主要有三种：ListState、UnionListState和BroadcastState。
 * <p>
 * 原文链接：https://blog.csdn.net/qq_43048957/article/details/135220819
 *
 * @author david.duan
 * @packageName com.linkflow.flink.dev.state
 * @className OperatorStateDemo
 * @date 2024/7/17
 * @description
 */
public class OperatorStateDemo {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    }

    /**
     * 需求：在map算子中计算数据的个数。
     *
     * @param env
     * @throws Exception 与Keyed State中的ListState一样，将状态表示为一组数据的列表。
     *                   与Keyed State中的列表状态的区别是：在算子状态的上下文中，不会按键（key）分别处理状态，所以每一个并行子任务上只会保留一个“列表”（list），
     *                   也就是当前并行子任务上所有状态项的集合。列表中的状态项就是可以重新分配的最细粒度，彼此之间完全独立。
     *                   当算子并行度进行缩放调整时，算子的列表状态中的所有元素项会被统一收集起来，相当于把多个分区的列表合并成了一个“大列表”，然后再均匀地分配给所有并行任务。
     *                   这种“均匀分配”的具体方法就是“轮询”（round-robin），与之前介绍的rebanlance数据传输方式类似，是通过逐一“发牌”的方式将状态项平均分配的。这种方式也叫作“平均分割重组”（even-split redistribution）。
     *                   算子状态中不会存在“键组”（key group）这样的结构，所以为了方便重组分配，就把它直接定义成了“列表”（list）。这也就解释了，为什么算子状态中没有最简单的值状态（ValueState）。
     */
    public static void testListState(StreamExecutionEnvironment env) throws Exception {
        env.setParallelism(2);//因为一个并行度共享一个state，所以这里设置并行度为2，以方便观察
        env.socketTextStream("localhost", 9999).map(new MyCountMapFunction()).print();
        env.execute();
    }

    // 1.实现 CheckpointedFunction 接口
    public static class MyCountMapFunction implements MapFunction<String, Long>, CheckpointedFunction {
        private Long count = 0L;
        private ListState<Long> state;

        @Override
        public Long map(String value) throws Exception {
            return count++;
        }

        // 2.本地变量持久化：将 本地变量 拷贝到算子状态中,开启checkpoint时才会调用
        @Override
        public void snapshotState(FunctionSnapshotContext context) throws Exception {
            System.out.println("snapshotState...");
            // 2.1 清空算子状态
            state.clear();
            // 2.2 将 本地变量 添加到 算子状态 中
            state.add(count);
        }

        //3.初始化本地变量：程序启动和恢复时， 从状态中 把数据添加到 本地变量，每个子任务调用一次
        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            System.out.println("initializeState...");
            // 3.1 从 上下文 初始化 算子状态
            state = context
                    .getOperatorStateStore()
                    .getListState(new ListStateDescriptor<Long>("state", Types.LONG));
            // 3.2 从 算子状态中 把数据 拷贝到 本地变量
            if (context.isRestored()) {
                for (Long l : state.get()) {
                    count += l;
                }
            }
        }
    }

    /**
     * 与ListState类似，联合列表状态也会将状态表示为一个列表。它与常规列表状态的区别在于，算子并行度进行缩放调整时对于状态的分配方式不同。
     * UnionListState的重点就在于“联合”（union）。在并行度调整时，常规列表状态是轮询分配状态项，而联合列表状态的算子则会直接广播状态的完整列表。
     * 这样，并行度缩放之后的并行子任务就获取到了联合后完整的“大列表”，可以自行选择要使用的状态项和要丢弃的状态项。这种分配也叫作“联合重组”（union redistribution）。
     * 如果列表中状态项数量太多，为资源和效率考虑一般不建议使用联合重组的方式。
     * 使用方式同ListState，区别在如下标红部分：
     * state = context
     *               .getOperatorStateStore()
     *               .getUnionListState(new ListStateDescriptor<Long>("union-state", Types.LONG));
     *
     * 原文链接：https://blog.csdn.net/qq_43048957/article/details/135220819
     */


    /**
     * 需求:水位超过指定的阈值发送告警，阈值可以动态修改。
     * <p>
     * 有时我们希望算子并行子任务都保持同一份“全局”状态，用来做统一的配置和规则设定。这时所有分区的所有数据都会访问到同一个状态，
     * 状态就像被“广播”到所有分区一样，这种特殊的算子状态，就叫作广播状态（BroadcastState）。
     * 因为广播状态在每个并行子任务上的实例都一样，所以在并行度调整的时候就比较简单，只要复制一份到新的并行任务就可以实现扩展；
     * 而对于并行度缩小的情况，可以将多余的并行子任务连同状态直接砍掉——因为状态都是复制出来的，并不会丢失。
     * 原文链接：https://blog.csdn.net/qq_43048957/article/details/135220819
     *
     * @param env
     * @throws Exception
     */
    public static void testOperatorBroadcastState(StreamExecutionEnvironment env) throws Exception {
        env.setParallelism(2);
        //数据流
        SingleOutputStreamOperator<WaterSensor> sensorDS = env.socketTextStream("localhost", 9999).map(new WaterSensorMapFunction());
        // 配置流（用来广播配置）
        DataStreamSource<String> configDS = env.socketTextStream("hadoop102", 8888);
        // 1. 将 配置流 广播
        MapStateDescriptor<String, Integer> broadcastMapState = new MapStateDescriptor<>("broadcast-state", Types.STRING, Types.INT);
        BroadcastStream<String> configBS = configDS.broadcast(broadcastMapState);
        //2.把 数据流 和 广播后的配置流 connect
        BroadcastConnectedStream<WaterSensor, String> sensorBCS = sensorDS.connect(configBS);
        //3.调用 process
        sensorBCS.process(new BroadcastProcessFunction<WaterSensor, String, String>() {
            //数据流的处理方法： 数据流 只能 读取 广播状态，不能修改
            @Override
            public void processElement(WaterSensor value, BroadcastProcessFunction<WaterSensor, String, String>.ReadOnlyContext ctx, Collector<String> out) throws Exception {
                //  5.通过上下文获取广播状态，取出里面的值（只读，不能修改）
                ReadOnlyBroadcastState<String, Integer> broadcastState = ctx.getBroadcastState(broadcastMapState);
                Integer threshold = broadcastState.get("threshold");
                // 判断广播状态里是否有数据，因为刚启动时，可能是数据流的第一条数据先来
                threshold = (threshold == null ? 0 : threshold);
                if (value.getVc() > threshold) {
                    out.collect(value + ",水位超过指定的阈值：" + threshold + "!!!");
                }
            }

            //广播后的配置流的处理方法:  只有广播流才能修改 广播状态
            @Override
            public void processBroadcastElement(String value, BroadcastProcessFunction<WaterSensor, String, String>.Context ctx, Collector<String> out) throws Exception {
                //  4. 通过上下文获取广播状态，往里面写数据
                BroadcastState<String, Integer> broadcastState = ctx.getBroadcastState(broadcastMapState);
                broadcastState.put("threshold", Integer.valueOf(value));
            }
        }).print();

        env.execute();
    }
}


