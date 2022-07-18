package com.yiban.javaBase.dev.JVM.gc;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 模拟一下Metaspace内存溢出的情况
 *
 * @auther WEI.DUAN
 * @date 2018/4/16
 * @website http://blog.csdn.net/dwshmilyss
 */
public class MetaspaceOOMTest {
    /**
     * JVM参数:-XX:MetaspaceSize=8m -XX:MaxMetaspaceSize=128m -XX:+PrintFlagsInitial
     */
    public static void main(String[] args) {
        int i = 0;

        try {
            for (;;) {
                i++;

                Enhancer enhancer = new Enhancer();
                enhancer.setSuperclass(OOMObject.class);
                enhancer.setUseCache(false);
                enhancer.setCallback(new MethodInterceptor() {
                    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                        return proxy.invokeSuper(obj, args);
                    }
                });
                enhancer.create();
            }
        } catch (Exception e) {
            System.out.println("第" + i + "次时发生异常");
            e.printStackTrace();
        }
    }

    static class OOMObject {

    }
}
