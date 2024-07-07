package com.linfflow.flink.dev.datastream.sideoutput;

import com.linfflow.flink.dev.pojo.OrderLog;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import com.alibaba.fastjson.JSON;
import java.io.Serializable;

/**
 * （1）分隔过滤。充当filter算子功能，将源中的不同类型的数据做分割处理。因为使用filter 算子对数据源进行筛选分割的话，会造成数据流的多次复制，导致不必要的性能浪费
 * 将不同价格类型订单从主流中分开处理
 * 输出结果
 * 主流-正常价:3> OrderLog(orderId=20201011231234567, skuId=1226351, priceType=normal)
 * 侧硫-回流价:2> OrderLog(orderId=20201011231212768, skuId=1226324, priceType=back)
 * 侧硫-新人价:1> OrderLog(orderId=20201011231245423, skuId=1226354, priceType=new)
 * 主流-正常价:1> OrderLog(orderId=20201011231254678, skuId=1226322, priceType=normal)
 */
public class SideOutputStreamDemo {
    public static final OutputTag<OrderLog> NEW_PRICE = new OutputTag<>("NEW_PRICE", TypeInformation.of(OrderLog.class)) ;
    public static final OutputTag<OrderLog> BACK_FLOW_PRICE = new OutputTag<>("BACK_FLOW_PRICE", TypeInformation.of(OrderLog.class)) ;

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStreamSource<String> textStreamSource = env.readTextFile(SideOutputStreamDemo.class.getClassLoader().getResource("orderLog.txt").getPath());
        SingleOutputStreamOperator<OrderLog> singleOutputStreamOperator = textStreamSource.process(new SideOutPutProcessFunction());

        singleOutputStreamOperator.print("正常价");
        singleOutputStreamOperator.getSideOutput(NEW_PRICE).print("侧输出流:新人价");
        singleOutputStreamOperator.getSideOutput(BACK_FLOW_PRICE).print("侧输出流:回流价");

        env.execute("SideOutputStreamDemo");
    }

}

class SideOutPutProcessFunction extends ProcessFunction<String, OrderLog> implements Serializable {

    private static final long serialVersionUID = -4855002593103725890L;

    @Override
    public void processElement(String value, ProcessFunction<String, OrderLog>.Context ctx, Collector<OrderLog> out) throws Exception {
        OrderLog orderLog = JSON.parseObject(value, OrderLog.class);
        if ("normal".equals(orderLog.getPriceType())) {
            out.collect(orderLog);
        } else if ("backflow".equals(orderLog.getPriceType())) {
            ctx.output(SideOutputStreamDemo.BACK_FLOW_PRICE,orderLog);
        } else if ("new".equals(orderLog.getPriceType())) {
            ctx.output(SideOutputStreamDemo.NEW_PRICE,orderLog);
        }
    }
}
