package com.yiban.javaBase.dev.GOF.behavior.strategy;

/**
 * Created by duanwei on 2017/4/17.
 * 黄金VIP 打七折
 */
@PriceRegion(min = 30000)
public class GoldVip implements CalPrice {
    @Override
    public Double calPrice(Double originPrice) {
        return originPrice * 0.7;
    }
}
