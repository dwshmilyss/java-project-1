package com.yiban.btrace.dev;

import com.sun.btrace.AnyType;
import com.sun.btrace.annotations.*;
import static com.sun.btrace.BTraceUtils.*;
/**
 * @auther WEI.DUAN
 * @date 2018/12/4
 * @website http://blog.csdn.net/dwshmilyss
 */
@BTrace
public class TraceDemo2 {

    /**
     * 这里是监控 BtraceServiceImpl 方法中 getCount 的调用情况。duration是以纳秒为单位的,所以换算成 MS 比较好看一点 ，其他例子也是如此考虑。
     */
    @OnMethod(clazz = "com.joson.btrace.service.impl.BtraceServiceImpl",method = "getCount",location=@Location(Kind.RETURN))
    public static void printMethodRunTime1(@ProbeClassName String probeClassName,@Duration long duration){
        println(probeClassName + ",duration:" + duration / 1000000 + " ms");
    }

    /**
     * 这里是监控 BtraceServiceImpl 类中 getCount 方法内的外部方法调用情况并打印出响应时间大于 5 MS 的外部调用方法名 。
     * 通过注入 @TargetInstance 和 @TargetMethodOrField 参数，告诉脚本实际匹配到的外部函数调用的类及方法名(或属性名)
     * @param self
     * @param instance
     * @param methon
     * @param duration
     */
    @OnMethod(
            clazz = "com.joson.btrace.service.impl.BtraceServiceImpl",
            method = "getCount",
            location=@Location(value=Kind.CALL,clazz="/.*/",method="/.*/",
            where = Where.AFTER)
    )
    public static void printMethodRunTime2(@Self Object self,@TargetInstance Object instance,@TargetMethodOrField String methon,@Duration long duration){
        if(duration>5000000 ){//如果耗时大于 5 毫秒则打印出来 这个条件建议加 否则打印的调用函数太多 具体数值可以自己调控
            println(methon + ",cost:" + duration / 1000000 + " ms");
        }
    }


    /**
     * 监控 BtraceService 接口的所有实现类中 对 getCount 方法的调用情况。
     * @param probeClassName
     * @param duration
     */
    @OnMethod(clazz = "+com.joson.btrace.service.BtraceService",method = "getCount",location=@Location(Kind.RETURN))
    public static void printMethodRunTime3(@ProbeClassName String probeClassName,@Duration long duration){
        println(probeClassName + ",cost time:" + duration / 1000000 + " ms");
    }

    /**
     * 通过正则表达式可以实现批量定位，正则表达式需要写在两个 "/" 中间。PS:建议正则表达式的范围要尽可能的小，不然会非常慢。
     * 这里是监控 com.joson.btrace.service 包下的所有类与方法，并打印其调用时间 以 MS 为单位。
     * 通过在函数里注入 @ProbeClassName,@ProbeMethodName 参数，告诉脚本实际匹配到的类和方法名。
     * @param probeClassName
     * @param probeMethod
     * @param duration
     */
    @OnMethod(clazz = "/com.joson.btrace.service.*/",method = "/.*/",location=@Location(Kind.RETURN))
    public static void printMethodRunTime4(@ProbeClassName String probeClassName,@ProbeMethodName String probeMethod,@Duration long duration){
        println( probeClassName + "." + probeMethod + " cost time: " + duration / 1000000 + " ms.");

    }

    /**
     * 监控代码是否执行到了某类的某一行
     * 监控代码是否到达了 Stock类的 363 行。
     */
    @OnMethod(clazz = "java.net.ServerSocket", location = @Location(value = Kind.LINE, line = 363))
    public static void onBind4(){
        println("socket bind reach line:363");
    }

    /**
     * 打印某个类中某一方法的入参
     * 这里是监控 BtraceService 类中 getCount 方法的所有入参及返回值
     * 对于入参，不需要打印的也可以不定义 但是定义一定要按顺序。比如参数列表不能放在返回值的后面。
     * 对于返回值类型 如果是非基本类型 则直接用 AnyType 类型即可。
     * @param self
     * @param type
     * @param limit
     * @param result
     */
    @OnMethod(clazz = "com.joson.btrace.service.BtraceService",method = "getCount",location=@Location(Kind.RETURN))
    public static void printMethodRunTime(@Self Object self,String type,Integer limit,@Return AnyType result ){
        println( "type: " + type + " ,limit: " + limit  );
        println("result : " + result );

    }

    /**
     * 查看谁调用了 GC
     */
    @OnMethod(clazz = "java.lang.System", method = "gc")
    public static void onSystemGC() {
        println("entered System.gc()");
        jstack();// print the stack info.
    }

}