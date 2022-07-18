package com.yiban.btrace.dev;

import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.OnMethod;
import static com.sun.btrace.BTraceUtils.println;
/**
 * @auther WEI.DUAN
 * @date 2018/12/3
 * @website http://blog.csdn.net/dwshmilyss
 */
@BTrace
public class HelloWorld {
    @OnMethod(clazz = "java.lang.Thread",method = "start")
    public static void onThreadStart(){
        println("thread start!");
    }
}