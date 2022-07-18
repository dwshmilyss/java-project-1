package com.yiban.javaBase.dev.proxy.static_proxy;

/**
 * 3，定义业务代理类：通过组合，在代理类中创建一个业务实现类对象来调用具体的业务方法；通过实现业务逻辑接口，来统一业务方法；在代理类中实现业务逻辑接口中的方法时，进行预处理操作、通过业务实现类对象调用真正的业务方法、进行调用后操作的定义。
 *
 * @auther WEI.DUAN
 * @date 2018/9/18
 * @website http://blog.csdn.net/dwshmilyss
 */
public class CountProxy implements ICount {
    //组合一个业务实现类对象来进行真正的业务方法的调用
    private CountImpl countImpl;

    public CountProxy(CountImpl countImpl) {
        this.countImpl = countImpl;
    }

    @Override
    public void queryCount() {
        System.out.println("查询账户的预处理——————");
        // 调用真正的查询账户方法
        countImpl.queryCount();
        System.out.println("查询账户之后————————");
    }

    @Override
    public void updateCount() {
        System.out.println("修改账户之前的预处理——————");
        // 调用真正的修改账户操作
        countImpl.updateCount();
        System.out.println("修改账户之后——————————");
    }
}
