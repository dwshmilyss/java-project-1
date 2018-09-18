package com.yiban.javaBase.dev.proxy.dynamic_proxy.cglib_proxy;

/**
 * 首先定义业务类，无需实现接口（当然，实现接口也可以，不影响的）
 *
 * @auther WEI.DUAN
 * @date 2018/9/18
 * @website http://blog.csdn.net/dwshmilyss
 */
public class BookFacadeImpl {
    public void addBook(){
        System.out.println("新增图书....");
    }
}
