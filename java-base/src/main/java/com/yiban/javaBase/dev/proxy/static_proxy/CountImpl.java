package com.yiban.javaBase.dev.proxy.static_proxy;

/**
 * 2，定义业务实现类，实现业务逻辑接口
 *
 * @auther WEI.DUAN
 * @date 2018/9/18
 * @website http://blog.csdn.net/dwshmilyss
 */
public class CountImpl implements ICount{
    @Override
    public void queryCount() {
        System.out.println("查看账户...");
    }

    @Override
    public void updateCount() {
        System.out.println("修改账户...");
    }
}
