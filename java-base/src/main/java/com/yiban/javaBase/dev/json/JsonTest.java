package com.yiban.javaBase.dev.json;

import com.alibaba.fastjson.JSONArray;
import org.json.JSONObject;
import org.springframework.util.StopWatch;

/**
 * json for org.json test (not alibaba's fastJson)
 *
 * @auther WEI.DUAN
 * @date 2017/10/23
 * @website http://blog.csdn.net/dwshmilyss
 */
public class JsonTest {
    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
//        testFastJson();
        testOrgJson();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    static void testOrgJson() {
        org.json.JSONArray jsonArray = new org.json.JSONArray();
        for (int i = 0; i < 100000; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", "dw");
            jsonObject.put("age", 18);
            String output = jsonObject.toString();
            jsonArray.put(jsonObject);
        }
        System.out.println(jsonArray.length());
    }

    static void testFastJson() {
        com.alibaba.fastjson.JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < 100000; i++) {
            com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
            jsonObject.put("name", "dw");
            jsonObject.put("age", 18);
            String output = jsonObject.toString();
            jsonArray.add(jsonObject);
        }
        System.out.println(jsonArray.size());
    }
}
