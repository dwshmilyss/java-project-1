package com.yiban.javaBase.dev.GOF.behavior.strategy;

/**
 * Created by duanwei on 2017/4/12.
 */
@PriceRegion(min = 10000, max = 20000)
public class Vip implements CalPrice {
    @Override
    public Double calPrice(Double originPrice) {
        return originPrice * 0.9;
    }
}
