package com.yiban.dubbo.demo.spi;

import com.alibaba.dubbo.common.extension.SPI;

@SPI
public interface City {
    void getName();
}
