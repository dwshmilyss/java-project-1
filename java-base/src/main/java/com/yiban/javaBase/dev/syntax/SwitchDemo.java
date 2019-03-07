package com.yiban.javaBase.dev.syntax;

/**
 * @auther WEI.DUAN
 * @date 2019/1/11
 * @website http://blog.csdn.net/dwshmilyss
 */
public class SwitchDemo {
    public static void main(String[] args) {
        int a = 1;
        switch (a){
            case 0:
                System.out.println("a = " + 0);
                break;
            case 1:
                System.out.println("a = " + 1);
            case 2:
                System.out.println("a = " + 2);
        }
    }
}