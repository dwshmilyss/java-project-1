package com.yiban.javaBase.dev.GOF.creator.builder;

public class ChickenBurger extends Burger{
    @Override
    public String name() {
        return "ChickenBurger";
    }

    @Override
    public float price() {
        return 50.0f;
    }
}
