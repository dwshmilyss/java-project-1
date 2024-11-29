package com.linkflow.flink.dev.cep.demo1;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

/**
 * @author david.duan
 * @packageName com.linkflow.flink.dev.cep.demo1
 * @className MyFlatMapFunction
 * @date 2024/11/20
 * @description
 */
public class MyFlatMapFunction implements FlatMapFunction<String, UserLoginLog> {
    @Override
    public void flatMap(String value, Collector<UserLoginLog> out) throws Exception {
        if (StringUtils.isNotBlank(value)) {
            UserLoginLog userLoginLog = JSONObject.parseObject(value, UserLoginLog.class);
            out.collect(userLoginLog);
        }
    }
}
