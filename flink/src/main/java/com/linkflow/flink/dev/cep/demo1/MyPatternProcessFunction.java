package com.linkflow.flink.dev.cep.demo1;

import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.util.Collector;

import java.util.List;
import java.util.Map;

/**
 * @author david.duan
 * @packageName com.linkflow.flink.dev.cep.demo1
 * @className MyPatternProcessFunction
 * @date 2024/11/20
 * @description
 */
public class MyPatternProcessFunction extends PatternProcessFunction<UserLoginLog,UserLoginLog> {
    @Override
    public void processMatch(Map<String, List<UserLoginLog>> match, Context ctx, Collector<UserLoginLog> out) throws Exception {
        List<UserLoginLog> start = match.get("start");
        out.collect(start.get(0));
    }
}
