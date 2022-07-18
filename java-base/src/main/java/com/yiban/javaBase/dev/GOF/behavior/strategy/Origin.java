package com.yiban.javaBase.dev.GOF.behavior.strategy;

/**
 * Created by duanwei on 2017/4/12.
 */
@PriceRegion(max = 10000)
public class Origin implements CalPrice {
    @Override
    public Double calPrice(Double originPrice) {
        return originPrice;
    }
}
