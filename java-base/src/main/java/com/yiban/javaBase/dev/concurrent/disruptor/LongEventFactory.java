package com.yiban.javaBase.dev.concurrent.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * In order to allow the Disruptor to preallocate these events for us, we need to an EventFactory that will perform the construction
 *
 * @auther WEI.DUAN
 * @date 2018/1/11
 * @website http://blog.csdn.net/dwshmilyss
 */
public class LongEventFactory implements EventFactory<LongEvent> {
    @Override
    public LongEvent newInstance() {
        return new LongEvent();
    }
}
