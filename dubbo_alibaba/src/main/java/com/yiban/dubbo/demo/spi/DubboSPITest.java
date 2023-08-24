package com.yiban.dubbo.demo.spi;

import com.alibaba.dubbo.common.extension.ExtensionLoader;

/**
 * @Description: TODO
 * @Author: David.duan
 * @Date: 2023/8/23
 **/
public class DubboSPITest {
    public static void main(String[] args) {
        ExtensionLoader<City> loader = ExtensionLoader.getExtensionLoader(City.class);
        City a = loader.getExtension("a");
        a.getName();
    }
}
