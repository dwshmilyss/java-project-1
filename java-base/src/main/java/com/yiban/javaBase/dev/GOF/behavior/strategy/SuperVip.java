package com.yiban.javaBase.dev.GOF.behavior.strategy;

/**
 * Created by duanwei on 2017/4/17.
 */
@PriceRegion(min = 20000, max = 30000)
public class SuperVip implements CalPrice {
    @Override
    public Double calPrice(Double originPrice) {
        return originPrice * 0.8;
    }
}
