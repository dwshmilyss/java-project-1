package com.yiban.javaBase.dev.GOF.creator.factory.abstracted;

public class Blue implements Color {
    @Override
    public void fill() {
        System.out.println("Inside Blue::fill() method.");
    }
}
