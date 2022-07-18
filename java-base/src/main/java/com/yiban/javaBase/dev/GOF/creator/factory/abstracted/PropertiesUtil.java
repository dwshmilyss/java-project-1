package com.yiban.javaBase.dev.GOF.creator.factory.abstracted;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {
    private static Map<String, Properties> cache = new HashMap<String, Properties>();

    public static String get(String configFileName,String key) {
        return getProperties(configFileName).getProperty(key);
    }

    public static Properties getProperties(String configFileName) {
        if (cache.get(configFileName) != null) {
            return cache.get(configFileName);
        }
        InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(configFileName);
        Properties props = new Properties();
        try {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        cache.put(configFileName, props);
        return props;
    }
}
