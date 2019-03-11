package com.yiban.javaBase.dev.log.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @auther WEI.DUAN
 * @date 2019/3/10
 * @website http://blog.csdn.net/dwshmilyss
 */
public class LogbackDemo {
    /** Logger **/
    private static final Logger LOGGER = LoggerFactory.getLogger(LogbackDemo.class);

    public static void main(String[] args) {
//        LOGGER.trace("=====trace=====");
//        LOGGER.debug("=====debug=====");
//        LOGGER.info("=====info=====");
//        LOGGER.warn("=====warn=====");
//        LOGGER.error("=====error=====");
        for (int i = 0; i < 300; i++) {
            LOGGER.info("=========我爱中国  我是中国人 ==========");
        }
    }
}