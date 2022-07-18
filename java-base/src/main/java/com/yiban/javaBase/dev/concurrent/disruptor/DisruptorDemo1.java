package com.yiban.javaBase.dev.concurrent.disruptor;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslatorOneArg;

/**
 * @auther WEI.DUAN
 * @date 2018/1/12
 * @website http://blog.csdn.net/dwshmilyss
 */
public class DisruptorDemo1 {
    private EventFactory<LongEvent> eventFactory = new EventFactory<LongEvent>() {
        public LongEvent newInstance() {
            return new LongEvent();
        }
    };

    //声明disruptor中事件类型及对应的事件工厂
    private class LongEvent {
        private long value;

        public LongEvent() {
            this.value = 0L;
        }

        public void set(long value) {
            this.value = value;
        }

        public long get() {
            return this.value;
        }
    }

    //pubisher逻辑，将原始数据转换为event，publish到ringbuffer
    private class Publisher implements EventTranslatorOneArg<LongEvent, String> {

        public void translateTo(LongEvent event, long sequence, String arg0) {
            event.set(Long.parseLong(arg0));
        }
    }

}
