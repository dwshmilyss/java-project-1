package com.yiban.javaBase.dev.concurrent.disruptor;


import com.lmax.disruptor.EventHandler;
import org.apache.log4j.pattern.LogEvent;

/**
 * Once we have the event defined we need to create a consumer that will handle these events. In our case all we want to do is print the value out the the console.
 *
 * @auther WEI.DUAN
 * @date 2018/1/12
 * @website http://blog.csdn.net/dwshmilyss
 */
public class LongEventHandler implements EventHandler<LogEvent> {
    @Override
    public void onEvent(LogEvent event, long sequence, boolean endofBatch) throws Exception {
        System.out.println("Event: " + event);
    }
}
