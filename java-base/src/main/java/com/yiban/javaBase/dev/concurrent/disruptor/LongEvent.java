package com.yiban.javaBase.dev.concurrent.disruptor;

/**
 * Firstly we will define the Event that will carry the data.
 *
 * @auther WEI.DUAN
 * @date 2018/1/11
 * @website http://blog.csdn.net/dwshmilyss
 */
public class LongEvent {
    private long value;

    public void set(long value) {
        this.value = value;
    }
}
