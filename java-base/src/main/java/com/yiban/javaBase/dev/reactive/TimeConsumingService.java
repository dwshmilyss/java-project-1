package com.yiban.javaBase.dev.reactive;

import java.util.concurrent.Callable;

public class TimeConsumingService implements Callable<String> {
    private String service_name;
    private int wait_ms;

    public TimeConsumingService(String name, Integer waiting, String[] depandencies) {
        this.service_name = name;
        this.wait_ms = waiting;
    }

    @Override
    public String call() throws Exception {
        Thread.sleep(wait_ms);
        return String.format("service %s exec time is: %d", service_name, wait_ms);
    }
}
