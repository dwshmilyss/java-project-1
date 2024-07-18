package com.linkflow.flink.dev.functions;

import org.apache.flink.api.common.functions.MapFunction;
import com.linkflow.flink.dev.pojo.WaterSensor;

/**
 * @author david.duan
 * @packageName com.linkflow.flink.dev.functions
 * @className WaterSensorMapFunction
 * @date 2024/7/16
 * @description
 */
public class WaterSensorMapFunction implements MapFunction<String, WaterSensor> {
    @Override
    public WaterSensor map(String value) throws Exception {
        String[] datas = value.split(",");
        return new WaterSensor(datas[0],Long.valueOf(datas[1]) ,Integer.valueOf(datas[2]) );
    }
}
