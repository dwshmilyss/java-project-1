package com.yiban.javaBase.dev.generic;

public class GenericDemo1 {
    public static void main(String[] args) {
        Box<String> name = new Box<>("dw");
        Box<Number> no = new Box<>(19);
        Box<Integer> age = new Box<>(18);

        System.out.println(name.getClass() == age.getClass());
        getData(no);
        //这个编译报错 因为虽然Integer是Number的子类，但是Box<Integer>并不是Box<Number>的子类
        //这里不像scala 有逆变和协变的定义
//        getData(age);
        getData1(age);

        getData2(age);
        getData2(no);


    }

    public static void getData(Box<Number> data) {
        System.out.println("data = " + data.getData());
    }

    //如果想要通用的表示方法，可以使用?定义通配
    public static void getData1(Box<?> data) {
        System.out.println("data = " + data.getData());
    }

    //或者使用上下界的方式定义 这样就是只能是Number和Number的子类
    public static void getData2(Box<? extends Number> data) {
        System.out.println("data = " + data.getData());
    }

}


class Box<T> {

    private T data;

    public Box() {
    }

    public Box(T data) {
        this.data = data;
    }

    public T getData() {
        return this.data;
    }
}
