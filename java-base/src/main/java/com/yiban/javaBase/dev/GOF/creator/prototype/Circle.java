package com.yiban.javaBase.dev.GOF.creator.prototype;

public class Circle extends Shape{
    @Override
    public void draw() {
        System.out.println("Inside Square::draw() method.");
    }

    public Circle(){
        type = "Circle";
    }
}
