package com.yiban.javaBase.dev.utils;

import org.springframework.util.StopWatch;
import sun.security.util.BitArray;

/**
 * @Description: TODO
 * @Author: David.duan
 * @Date: 2023/6/29
 **/
public class BitArrayDemo {
    public static void main(String[] args) {
        BitArray bitArray = new BitArray(10000);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < 100; i++) {
            bitArray.set(i, true);
        }
    }
}
