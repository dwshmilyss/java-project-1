package com.yiban.javaBase.dev.GOF.creator.factory.abstracted;

import com.yiban.javaBase.dev.GOF.creator.factory.simple.Shape;

public class AbstractFactoryPatternDemo {
    public static void main(String[] args) {
        //获取形状工厂
        AbstractFactory shapeFactory = FactoryProducer.getFactory("SHAPE");

        //获取形状为 Circle 的对象
//        Shape shape1 = shapeFactory.getShape("CIRCLE");
        Shape circle = shapeFactory.getShapeByReflect("factory.properties","circle");

        //调用 Circle 的 draw 方法
        circle.draw();

        //获取形状为 Rectangle 的对象
//        Shape shape2 = shapeFactory.getShape("RECTANGLE");
        Shape rectangle = shapeFactory.getShapeByReflect("factory.properties","rectangle");

        //调用 Rectangle 的 draw 方法
        rectangle.draw();

        //获取形状为 Square 的对象
//        Shape shape3 = shapeFactory.getShape("SQUARE");
        Shape square = shapeFactory.getShapeByReflect("factory.properties","square");

        //调用 Square 的 draw 方法
        square.draw();

        //获取颜色工厂
        AbstractFactory colorFactory = FactoryProducer.getFactory("COLOR");

        //获取颜色为 Red 的对象
//        Color color1 = colorFactory.getColor("RED");
        Color red = colorFactory.getColorByReflect("factory.properties","red");

        //调用 Red 的 fill 方法
        red.fill();

        //获取颜色为 Green 的对象
//        Color color2 = colorFactory.getColor("Green");
        Color green = colorFactory.getColorByReflect("factory.properties","green");

        //调用 Green 的 fill 方法
        green.fill();

        //获取颜色为 Blue 的对象
//        Color color3 = colorFactory.getColor("BLUE");
        Color blue = colorFactory.getColorByReflect("factory.properties","blue");

        //调用 Blue 的 fill 方法
        blue.fill();
    }
}
