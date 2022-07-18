package com.yiban.javaBase.dev.GOF.structure.proxy.dynamic_porxy.demo2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @auther WEI.DUAN
 * @date 2020/3/25
 * @website http://blog.csdn.net/dwshmilyss
 */
public class Test {
    @org.junit.Test
    public void test() {
        IDao iDao = new DaoImpl();
        InvocationHandler invocationHandler = new DaoHandler(iDao);
        Class[] interfaces = new Class[]{
                IDao.class
        };
        IDao dao = (IDao) Proxy.newProxyInstance(this.getClass().getClassLoader(), interfaces, invocationHandler);
        System.out.println(dao.show2());
    }

}

class DaoHandler implements InvocationHandler {

    IDao iDao = null;

    public DaoHandler(final IDao iDao) {
        super();
        this.iDao = iDao;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("代理开始");
        Object result = method.invoke(iDao);
        System.out.println("代理结束");
        return null;
    }
}
