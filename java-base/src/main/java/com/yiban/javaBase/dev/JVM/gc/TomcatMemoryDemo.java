package com.yiban.javaBase.dev.JVM.gc;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.Iterator;

/**
 * 监控tomcat中jvm不同代的内存使用情况
 *
 * @auther WEI.DUAN
 * @date 2018/1/23
 * @website http://blog.csdn.net/dwshmilyss
 */
public class TomcatMemoryDemo {
    public static void main(String[] args) {
        Iterator iter = ManagementFactory.getMemoryPoolMXBeans().iterator();
        while (iter.hasNext()) {
            MemoryPoolMXBean item = (MemoryPoolMXBean) iter.next();
            String name = item.getName();
            System.out.println("name = " + name);
            MemoryType memoryType = item.getType();
            System.out.println("memoryType = " + memoryType);
            MemoryUsage memoryUsage = item.getUsage();
            System.out.println("memoryUsage = " + memoryUsage);
            MemoryUsage peakUsage = item.getPeakUsage();
            System.out.println("peakUsage = " + peakUsage);
            MemoryUsage collectionUsage = item.getCollectionUsage();
            System.out.println("collectionUsage = " + collectionUsage);
        }
        System.out.println(ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().toString());
        System.out.println(ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().toString());
    }
}
