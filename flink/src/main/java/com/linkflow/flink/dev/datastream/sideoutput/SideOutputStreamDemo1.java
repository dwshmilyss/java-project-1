package com.linkflow.flink.dev.datastream.sideoutput;

import com.alibaba.fastjson.JSON;
import com.linkflow.flink.dev.pojo.OrderLog;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.Iterator;

/**
 * (2) 延时数据处理。在做对延时迟窗口计算时，对延时迟到的数据进行处理，即使数据迟到也不会造成丢失
 */
public class SideOutputStreamDemo1 {
    public static final OutputTag<OrderLog> NEW_PRICE = new OutputTag<>("NEW_PRICE", TypeInformation.of(OrderLog.class));
    public static final OutputTag<OrderLog> BACK_FLOW_PRICE = new OutputTag<>("BACK_FLOW_PRICE", TypeInformation.of(OrderLog.class));

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> textStreamSource = env.readTextFile(SideOutputStreamDemo.class.getClassLoader().getResource("orderLog.txt").getPath());
        SingleOutputStreamOperator<OrderLog> dayPvDataStream = textStreamSource
                .flatMap(new SideOutPutMapFunction())
                .assignTimestampsAndWatermarks(new AssignedWaterMarks(Time.seconds(3))) //保证乱序事件处理
                .keyBy(r -> r.getOrderId())
                .window(TumblingEventTimeWindows.of(Time.days(1), Time.hours(16))) //按1天开窗口,每天的16点开始计算，到第二天的16点
                .trigger(OrderCountSingleTrigger.of(1)) //来一条数据做一次计算
                .allowedLateness(Time.minutes(30)) //允许迟到30分钟
                .sideOutputLateData(SideOutputStreamDemo1.NEW_PRICE)
                .process(new SideOutPutWindowProcessFunction());

        dayPvDataStream.addSink(new SideOutPutSinkFunction());

        dayPvDataStream.getSideOutput(SideOutputStreamDemo1.NEW_PRICE) //得到迟到数据处理
                .keyBy(OrderLog::getOrderId)
                .window(TumblingEventTimeWindows.of(Time.seconds(3)))  //对迟到数据3秒计算一次
                .process(new SideOutPutWindowProcessFunction())
                .addSink(new SideOutPutSinkFunction());

        env.execute();
    }

}

/**
 * 水位线，保证乱序事件时间处理
 */
class AssignedWaterMarks extends BoundedOutOfOrdernessTimestampExtractor<OrderLog> {
    private static final long serialVersionUID = 2021421640499388219L;

    public AssignedWaterMarks(Time maxOutOfOrderness) {
        super(maxOutOfOrderness);
    }

    @Override
    public long extractTimestamp(OrderLog orderLog) {
        return orderLog.getRequestTime();
    }
}

/**
 * map转换输出
 */
class SideOutPutMapFunction extends RichFlatMapFunction<String, OrderLog> {
    private static final long serialVersionUID = -6478853684295335571L;

    @Override
    public void flatMap(String value, Collector<OrderLog> out) throws Exception {
        OrderLog orderLog = JSON.parseObject(value, OrderLog.class);
        out.collect(orderLog);
    }
}

/**
 * 窗口函数
 */
class SideOutPutWindowProcessFunction extends ProcessWindowFunction<OrderLog, OrderLog, String, TimeWindow> {
    private static final long serialVersionUID = -6632888020403733197L;

    @Override
    public void process(String arg0, ProcessWindowFunction<OrderLog, OrderLog, String, TimeWindow>.Context ctx,
                        Iterable<OrderLog> it, Collector<OrderLog> collect) throws Exception {
        Iterator<OrderLog> iterator = it.iterator();
        while (iterator.hasNext()) {
            OrderLog orderLog = iterator.next();
            collect.collect(orderLog);
        }
    }
}

/**
 * sink函数
 */
class SideOutPutSinkFunction extends RichSinkFunction<OrderLog> {
    private static final long serialVersionUID = -6632888020403733197L;

    @Override
    public void invoke(OrderLog orderLog, Context context) throws Exception {
        //做自己的存储计算逻辑
        System.out.println(JSON.toJSONString(orderLog));
    }

}