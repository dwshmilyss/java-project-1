package com.yiban.javaBase.dev.concurrent;

import java.util.concurrent.Semaphore;

/**
 * 信号量
 *
 * @auther WEI.DUAN
 * @date 2017/4/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class SemaphoreDemo {
    public static void main(String[] args){
        Semaphore semaphore = new Semaphore(1);
        System.out.println(semaphore.isFair());
    }
}
