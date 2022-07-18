package com.yiban.javaBase.dev.GOF.creator.builder;

public class Coke extends ColdDrink{
    @Override
    public String name() {
        return "coke";
    }

    @Override
    public float price() {
        return 30.0f;
    }
}
