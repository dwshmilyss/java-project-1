package com.yiban.javaBase.dev.GOF.creator.factory.abstracted;

import com.yiban.javaBase.dev.GOF.creator.factory.simple.Shape;

public abstract class AbstractFactory {
    public abstract Color getColor(String color);
    public abstract Color getColorByReflect(String filePath,String color);
    public abstract Shape getShape(String shape);
    public abstract Shape getShapeByReflect(String filePath,String shape);
}
