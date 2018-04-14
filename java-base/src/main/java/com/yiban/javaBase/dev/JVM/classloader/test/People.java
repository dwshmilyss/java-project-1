package com.yiban.javaBase.dev.JVM.classloader.test;

/**
 * @auther WEI.DUAN
 * @create 2017/11/18
 * @blog http://blog.csdn.net/dwshmilyss
 */
public class People {
    //该类写在记事本里，在用javac命令行编译成class文件，放在H盘根目录下
    //记住 不能用当前项目里编译好的这个People类，因为在当前应用下，所以不会用自己定义的类加载器加载
    //而只会用AppClassLoader
    private String name;

    public People() {
    }

    public People(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "I am a people, my name is " + name;
    }
}
