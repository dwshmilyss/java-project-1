package com.yiban.javaBase.dev.syntax;

/**
 * @auther WEI.DUAN
 * @date 2021/3/23
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ExtendClassDemo {
    class Base{
        public void driver() {
            System.out.println("父类driver");
        }
    }

    class Son extends Base {
        public void driver() {
            System.out.println("子类driver");
        }
    }

    public static void main(String[] args) {
        Base base = new ExtendClassDemo().new Base();
        Son son = new ExtendClassDemo().new Son();
        base.driver();
        son.driver();
        base = son;
        base.driver();
    }
}
