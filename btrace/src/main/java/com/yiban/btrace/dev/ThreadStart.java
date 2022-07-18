package com.yiban.btrace.dev;

import com.sun.btrace.annotations.*;
import static com.sun.btrace.BTraceUtils.*;
/**
 * @auther WEI.DUAN
 * @date 2018/12/18
 * @website http://blog.csdn.net/dwshmilyss
 */
@BTrace
public class ThreadStart {
    @OnMethod(
            clazz = "java.lang.Thread",
            method = "start"
    )
    public static void onNewThread(@Self Thread t){
        D.probe("jthreadstart", Threads.name(t));
        println("starting " + Threads.name(t));
    }
}