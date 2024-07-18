package com.linkflow.flink.dev.pojo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class OrderLog {
    private String orderId;

    private String skuId;

    private String priceType;

    private Long requestTime;

    /**
     * @param id:
     * @param name:
     * return Map<String,Integer>
     * @author david.duan on 2024/7/6 11:30
     * {@link Map<String,Integer>}
     * @description test
     */
    public Map<String,Integer> test(String id, int name) {
        return new HashMap<>();
    }
}
