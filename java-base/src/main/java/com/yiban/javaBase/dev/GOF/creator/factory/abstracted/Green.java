package com.yiban.javaBase.dev.GOF.creator.factory.abstracted;

public class Green implements Color {
    @Override
    public void fill() {
        System.out.println("Inside Green::fill() method.");
    }
}
