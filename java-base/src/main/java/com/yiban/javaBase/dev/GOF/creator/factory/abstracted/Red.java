package com.yiban.javaBase.dev.GOF.creator.factory.abstracted;

public class Red implements Color {
    @Override
    public void fill() {
        System.out.println("Inside Red::fill() method.");
    }
}
