package com.yiban.btrace.dev;

import static com.sun.btrace.BTraceUtils.println;
import static com.sun.btrace.BTraceUtils.str;
import static com.sun.btrace.BTraceUtils.strcat;
import static com.sun.btrace.BTraceUtils.timeMillis;

import com.sun.btrace.BTraceUtils;
import com.sun.btrace.annotations.*;

/**
 * @auther WEI.DUAN
 * @date 2018/11/30
 * @website http://blog.csdn.net/dwshmilyss
 *
 */
@BTrace
public class TraceDemo1 {
//    @TLS
//    private static long startTime = 0;
//
//    @OnMethod(clazz = "com.yiban.btrace.dev.Demo1", method = "execute")
//    public static void startMethod(){
//        startTime = timeMillis();
//    }
//
//    @OnMethod(clazz = "com.yiban.btrace.dev.Demo1", method = "execute", location = @Location(Kind.RETURN))
//    public static void endMethod(){
//        println(strcat("the class method execute time=>", str(timeMillis()-startTime)));
//        println("-------------------------------------------");
//    }
//
//    @OnMethod(clazz = "com.yiban.btrace.dev.Demo1", method = "execute", location = @Location(Kind.RETURN))
//    public static void traceExecute(@ProbeClassName String name,@ProbeMethodName String method,int sleepTime){
//        println(strcat("the class name=>", name));
//        println(strcat("the class method=>", method));
//        println(strcat("the class method params=>", str(sleepTime)));
//    }

    //监控某一个方法的执行时间
//    @OnMethod(clazz = "com.yiban.btrace.dev.Demo1",method = "execute",location=@Location(Kind.RETURN))
//    public static void printMethodRunTime(@ProbeClassName String probeClassName,@Duration long duration){
//        println(probeClassName + ",duration:" + duration / 1000000 + " ms, " + duration + " ns");
//    }


    //监控某一个方法的执行时间
    @OnMethod(
            clazz = "com.yiban.btrace.dev.Demo1",
            method = "sayHello",
            location=@Location(Kind.RETURN)
    )
    public static void traceSayHello(@ProbeClassName String probeClassName,@ProbeMethodName String method,String name ,int age,long sleepTime,@Duration long duration){
        if(BTraceUtils.matches(".*345.*", name)) {
            println(strcat("clzzz = ", probeClassName));
            println(strcat("method = ", method));
            println(strcat("name = ", name));
            println(strcat("age = ", str(age)));
            println(strcat("sleepTime = ", str(sleepTime)));
            println(strcat("duration(ms) : ", str(duration / 1000000)));
        }
    }

}