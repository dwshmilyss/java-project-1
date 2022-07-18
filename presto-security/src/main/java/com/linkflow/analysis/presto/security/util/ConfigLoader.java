package com.linkflow.analysis.presto.security.util;

import com.fasterxml.jackson.databind.JsonNode;
import io.naza.frw.util.json.jackson.JsonMapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by likai.yu on 2020/10/15
 */
public class ConfigLoader {

    public static Map<String, String> envMap = System.getenv();

//    public static final String APP_NAME = "presto-security";
    public static final String APP_NAME = "insight";
    //http://192.168.26.41:31896
    public static final String CONFIG_SERVER = envMap.getOrDefault("SPRING_CLOUD_CONFIG_URI", "http://localhost:8888");

    public static final String PROFILE = envMap.getOrDefault("SPRING_PROFILES_ACTIVE", "development");

    public static final JsonMapper JSON_MAPPER = JsonMapper.INSTANCE;

    public static Map<String, Object> getConfig(String configServer, String appName, String profile) {
        Map<String, Object> configs = new HashMap<>();
        String configUrl = configServer + "/" + appName + "/" + profile;
        String configStr = AsyncHttpClient.get(configUrl);
        JsonNode jsonNode = JSON_MAPPER.readAsJson(configStr);
        if (jsonNode.has("propertySources")) {
            JsonNode propertySources = jsonNode.get("propertySources");
            for (JsonNode node : propertySources) {
                if (node.has("source")) {
                    JsonNode source = node.get("source");
                    for (Iterator<String> it = source.fieldNames(); it.hasNext(); ) {
                        String key = it.next();
                        JsonNode value = source.get(key);
                        if (value.isValueNode()) {
                            switch (value.getNodeType()) {
                                case BOOLEAN:
                                    configs.put(key, value.asBoolean());
                                    break;
                                case NUMBER:
                                    if (value.isLong() || value.isBigInteger()) {
                                        configs.put(key, value.asLong());
                                    } else if (value.isFloat() || value.isDouble() || value.isBigDecimal() || value.isFloatingPointNumber()) {
                                        configs.put(key, value.asDouble());
                                    } else {
                                        configs.put(key, value.asInt());
                                    }
                                    break;
                                default:
                                    configs.put(key, value.asText());
                            }
                        }
                    }
                }
            }
        }
        return configs;
    }

    public static Map<String, Object> getConfig() {
        return getConfig(CONFIG_SERVER, APP_NAME, PROFILE);
    }

    public static void main(String[] args) {
        Map<String, Object> configs = getConfig();
//        System.out.println(configs.get("oss.minio.url"));
//        System.out.println(configs.get("oss.minio.key"));
//        System.out.println(configs.get("oss.minio.secret"));
//        System.out.println(configs.get("oss.bucket"));
//        for (Map.Entry<String, Object> config : configs.entrySet()) {
//            System.out.println(config.getKey() + ": " + config.getValue());
//        }
        for (Map.Entry<String, String> config : envMap.entrySet()) {
            System.out.println(config.getKey() + ": " + config.getValue());
        }
    }

}