package com.yiban.javaBase.dev.GOF.behavior.strategy;

/**
 * Created by duanwei on 2017/4/12.
 * 计算价格的策略接口
 */
public interface CalPrice {
    //根据原价返回一个最终的价格
    Double calPrice(Double originPrice);
}
