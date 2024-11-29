package com.linkflow.flink.dev.cep.demo1;


import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.IterativeCondition;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.time.Duration;
import java.util.Arrays;

/**
 * @author david.duan
 * @packageName com.linkflow.flink.dev.cep.demo1
 * @className CepDemo
 * @date 2024/11/20
 * @description  监控用户登录，10s内连续登录失败3次
 *
 * {"loginId":11111,"loginTime":1645177352000,"loginStatus":1,"userName":"aaron"},
 * {"loginId":11112,"loginTime":1645177353000,"loginStatus":1,"userName":"aaron"},
 * {"loginId":11113,"loginTime":1645177354000,"loginStatus":1,"userName":"aaron"},
 * {"loginId":11116,"loginTime":1645177355000,"loginStatus":1,"userName":"aaron"},
 * {"loginId":11117,"loginTime":1645177356000,"loginStatus":1,"userName":"aaron"},
 * {"loginId":11118,"loginTime":1645177357000,"loginStatus":1,"userName":"aaron"},
 * {"loginId":11119,"loginTime":1645177358000,"loginStatus":1,"userName":"aaron"},
 * {"loginId":11120,"loginTime":1645177359000,"loginStatus":0,"userName":"aaron"},
 * {"loginId":11121,"loginTime":1645177360000,"loginStatus":1,"userName":"aaron"},
 * {"loginId":11122,"loginTime":1645177361000,"loginStatus":1,"userName":"aaron"},
 * {"loginId":11123,"loginTime":1645177362000,"loginStatus":1,"userName":"aaron"},
 */
@Slf4j
public class CepDemo {
    public static void main(String[] args) {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        /**
         * 设置成1，是为了能够触发watermark来计算
         */
        env.setParallelism(1);

        DataStreamSource<String> loginStringStream = env.fromCollection(Arrays.asList(
                "{\"loginId\":11111,\"loginTime\":1645177352000,\"loginStatus\":1,\"userName\":\"aaron\"}",
                "{\"loginId\":11112,\"loginTime\":1645177353000,\"loginStatus\":1,\"userName\":\"aaron\"}",
                "{\"loginId\":11113,\"loginTime\":1645177354000,\"loginStatus\":1,\"userName\":\"aaron\"}",
                "{\"loginId\":11116,\"loginTime\":1645177355000,\"loginStatus\":1,\"userName\":\"aaron\"}",
                "{\"loginId\":11117,\"loginTime\":1645177356000,\"loginStatus\":1,\"userName\":\"aaron\"}",
                "{\"loginId\":11118,\"loginTime\":1645177357000,\"loginStatus\":1,\"userName\":\"aaron\"}",
                "{\"loginId\":11119,\"loginTime\":1645177358000,\"loginStatus\":1,\"userName\":\"aaron\"}",
                "{\"loginId\":11120,\"loginTime\":1645177359000,\"loginStatus\":0,\"userName\":\"aaron\"}",
                "{\"loginId\":11121,\"loginTime\":1645177360000,\"loginStatus\":1,\"userName\":\"aaron\"}",
                "{\"loginId\":11122,\"loginTime\":1645177361000,\"loginStatus\":1,\"userName\":\"aaron\"}",
                "{\"loginId\":11123,\"loginTime\":1645177362000,\"loginStatus\":1,\"userName\":\"aaron\"}"
        ));

        SingleOutputStreamOperator<UserLoginLog> dataStream = loginStringStream.flatMap(new MyFlatMapFunction())
                .assignTimestampsAndWatermarks(
                        WatermarkStrategy.<UserLoginLog>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                                .withTimestampAssigner((SerializableTimestampAssigner<UserLoginLog>) (element, recordTimestamp) -> element.getLoginTime())
                );


        /**
         * 10s钟之内连续3次登陆失败的才输出，强制连续，输出如下：
         * resultOutPut> UserLoginLog(loginId=11111, loginTime=1645177352000, loginStatus=1, userName=aaron)
         * resultOutPut> UserLoginLog(loginId=11112, loginTime=1645177353000, loginStatus=1, userName=aaron)
         * resultOutPut> UserLoginLog(loginId=11113, loginTime=1645177354000, loginStatus=1, userName=aaron)
         * resultOutPut> UserLoginLog(loginId=11116, loginTime=1645177355000, loginStatus=1, userName=aaron)
         * resultOutPut> UserLoginLog(loginId=11117, loginTime=1645177356000, loginStatus=1, userName=aaron)
         * resultOutPut> UserLoginLog(loginId=11121, loginTime=1645177360000, loginStatus=1, userName=aaron)
         */
        Pattern<UserLoginLog, UserLoginLog> wherePatternOne = Pattern.<UserLoginLog>begin("start").where(new SimpleCondition<UserLoginLog>() {
            @Override
            public boolean filter(UserLoginLog value) throws Exception {
                return 1 == value.getLoginStatus();
            }
        }).next("second").where(new IterativeCondition<UserLoginLog>() {
            @Override
            public boolean filter(UserLoginLog value, Context<UserLoginLog> ctx) throws Exception {

                return 1 == value.getLoginStatus();
            }
        }).next("third").where(new SimpleCondition<UserLoginLog>() {
            @Override
            public boolean filter(UserLoginLog value) throws Exception {

                return 1 == value.getLoginStatus();
            }
        }).within(Time.seconds(10));

        /**
         * 10s钟之内3次登陆失败的才输出,不强制连续; 输出如下：
         * resultOutPutTwo> UserLoginLog(loginId=11111, loginTime=1645177352000, loginStatus=1, userName=aaron)
         * resultOutPutTwo> UserLoginLog(loginId=11112, loginTime=1645177353000, loginStatus=1, userName=aaron)
         * resultOutPutTwo> UserLoginLog(loginId=11113, loginTime=1645177354000, loginStatus=1, userName=aaron)
         * resultOutPutTwo> UserLoginLog(loginId=11116, loginTime=1645177355000, loginStatus=1, userName=aaron)
         * resultOutPutTwo> UserLoginLog(loginId=11117, loginTime=1645177356000, loginStatus=1, userName=aaron)
         * resultOutPutTwo> UserLoginLog(loginId=11118, loginTime=1645177357000, loginStatus=1, userName=aaron)
         * resultOutPutTwo> UserLoginLog(loginId=11119, loginTime=1645177358000, loginStatus=1, userName=aaron)
         * resultOutPutTwo> UserLoginLog(loginId=11121, loginTime=1645177360000, loginStatus=1, userName=aaron)
         */
        Pattern<UserLoginLog, UserLoginLog> wherePatternTwo = Pattern.<UserLoginLog>begin("start").where(new IterativeCondition<UserLoginLog>() {
            @Override
            public boolean filter(UserLoginLog value, Context<UserLoginLog> ctx) throws Exception {
                return 1 == value.getLoginStatus();
            }
        }).times(3).within(Time.seconds(10));

        /**
         * 10s钟之内连续3次登陆失败的才输出,加上 consecutive 之后,就是 强制连续输出(和第一种效果一样)
         */
        Pattern<UserLoginLog, UserLoginLog> wherePatternThree = Pattern.<UserLoginLog>begin("start").where(new IterativeCondition<UserLoginLog>() {
            @Override
            public boolean filter(UserLoginLog value, Context<UserLoginLog> ctx) throws Exception {
                return 1 == value.getLoginStatus();
            }
        }).times(3).consecutive().within(Time.seconds(10));

        PatternStream<UserLoginLog> patternStream = CEP.pattern(dataStream, wherePatternOne);
        PatternStream<UserLoginLog> patternStream1 = CEP.pattern(dataStream, wherePatternTwo);
        PatternStream<UserLoginLog> patternStream2 = CEP.pattern(dataStream, wherePatternThree);

//        SingleOutputStreamOperator<UserLoginLog> process = patternStream.process(new MyPatternProcessFunction());
        SingleOutputStreamOperator<UserLoginLog> process1 = patternStream1.process(new MyPatternProcessFunction());
//        SingleOutputStreamOperator<UserLoginLog> process2 = patternStream2.process(new MyPatternProcessFunction());

//        process.print("resultOutPut");
        process1.print("resultOutPutTwo");
//        process2.print("resultOutPutThree");

        try {
            env.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
