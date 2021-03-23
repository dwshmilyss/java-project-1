package com.yiban.javaBase.dev.syntax;

import com.yiban.javaBase.dev.JVM.classloader.test.C;

/**
 * 测试多态
 * @auther WEI.DUAN
 * @date 2021/3/14
 * @website http://blog.csdn.net/dwshmilyss
 */
public class DuoTaiDemo {

    public static void main(String[] args) {
        DuoTaiDemo duoTaiDemo = new DuoTaiDemo();
        Shape circle = new Circle();
        duoTaiDemo.doDraw(circle);
        Shape square = new Square();
        duoTaiDemo.doDraw(square);
        Shape triangle = new Triangle();
        duoTaiDemo.doDraw(triangle);
//        circle.draw();
//        square.draw();
//        triangle.draw();
        System.out.println("=================");
        //下面的测试是静态分派，因为前面的Human被称为静态类型
        Human man = new Man();
        Human woman = new Woman();
        duoTaiDemo.say(man);
        duoTaiDemo.say(woman);

    }

    /**
     * 这下面的三个方法就是多态里的静态分派，意思就是编译期就确定了类型的，因为这三个方法的方法体里面都是具体的业务操作了。
     * 和动态分派的区别：
     * 静态分派：就是直接通过方法实现的(在别的类中直接声明方法的，注意这三个方法和下面Shape的三个子类，区别一目了然)
     * 动态分配：是在类中实现的(在子类中才声明方法的)
     * @param hum
     */
    public void say(Human hum){
        System.out.println("I am human");
    }
    public void say(Man hum){
        System.out.println("I am man");
    }
    public void say(Woman hum){
        System.out.println("I am woman");
    }

    /**
     * 这种就是动态分派，参数传父类，里面的方法体也是父类的方法调用，但是具体实现要看调用的时候传入的具体对象(子类)
     */
    public void doDraw(Shape shape) {
        shape.draw();
    }
}

class Shape {
    void draw() {}
}

class Circle extends Shape {
    @Override
    void draw() {
        System.out.println("Circle.draw()");
    }
}

class Square extends Shape {
    @Override
    void draw() {
        System.out.println("Square.draw()");
    }
}

class Triangle extends Shape {
    @Override
    void draw() {
        System.out.println("Triangle.draw()");
    }
}

class Human{
}
class Man extends Human{
}
class Woman extends Human{
}