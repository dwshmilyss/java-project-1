package com.yiban.javaBase.dev.GOF.creator.factory.simple;

public class Square implements Shape{
    @Override
    public void draw() {
        System.out.println("Inside Square::draw() method.");
    }
}
