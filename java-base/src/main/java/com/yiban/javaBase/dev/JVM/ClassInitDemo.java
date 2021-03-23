package com.yiban.javaBase.dev.JVM;

/**
 * 类初始化的一些注意点
 *
 * @auther WEI.DUAN
 * @date 2021/3/14
 * @website http://blog.csdn.net/dwshmilyss
 */
public class ClassInitDemo {
    public static void main(String[] args) {
        //测试常量对类初始化的影响
        System.out.println(Const.NAME);
        System.out.println("======================");
        //测试静态字段对类初始化的影响
        System.out.println(Child.m);
        System.out.println("======================");
        //测试定义一个Const数组会不会初始化Const，结果是没有任何输出，可见这种new一个数组的情况并不会初始化
        Const[] consts = new Const[5];
        System.out.println("======================");
        //但是如果对数组中的元素new了，则会初始化
        for (int i = 0; i < consts.length; i++) {
            consts[i] = new Const();
        }
    }
}

/**
 *   虽然程序中引用了const类的常量NAME，但是在编译阶段将此常量的值“我是常量”存储到了调用它的类ClassInitDemo的常量池中，
 *   对常量Const.NAME的引用实际上转化为了ClassInitDemo类对自身常量池的引用。
 *   也就是说，实际上ClassInitDemo的Class文件之中并没有Const类的符号引用入口，这两个类在编译成Class文件后就不存在任何联系了。
 */
class Const{
    public static final String NAME = "我是常量";
    static{
        System.out.println("初始化Const类");
    }
}

/**
 * 对于静态字段，只有直接定义这个字段的类才会被初始化，因此，通过其子类来引用父类中定义的静态字段，只会触发父类的初始化而不会触发子类的初始化。
 */
class Father {
    public static int m = 0;
    static {
        System.out.println("父类初始化");
    }

    public Father() {
        System.out.println("父类的构造函数");
    }
}

class Child extends Father{
    static{
        System.out.println("子类初始化");
    }

    public Child() {
        System.out.println("子类的构造函数");
    }
}

/**
 * 接口虽然不能有静态代码块，但是虚拟机也会为接口自动生成clinit指令来初始化接口
 * 和类不同的地方在于：
 * 1. 当一个类在初始化时，要求其父类全部已经初始化过了
 * 2. 但是一个接口在初始化时，并不要求其父接口全部都完成了初始化，只有在真正使用到父接口的时候（如引用接口中定义的常量。但是调用类中的常量，是不会初始化类的），才会初始化该父接口。
 */
interface IBase {
    //接口中定义变量默认就是 public static final(即常量)
    public static final int m = 33;
    int n = 44;
}

interface IChild  extends IBase {
}