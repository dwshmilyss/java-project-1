package com.yiban.hdp.hive.test.dev;

import java.math.BigDecimal;
import org.apache.hadoop.hive.common.type.HiveDecimal;

public class testHivedecimal {
    public static void main(String[] argv){
        BigDecimal bd = new BigDecimal ("47.6300");
        bd.setScale(4);
        System.out.println("bigdecimal object created and value is : " + bd.toString());
        System.out.println("precision : "+ bd.precision());
        System.out.println("scale : "+ bd.scale());
        HiveDecimal hv = HiveDecimal.create(bd);
        String str = hv.toString();
        System.out.println("value after serialization of bigdecimal to hivedecimal : " + str);
        System.out.println("precision after hivedecimal : "+ hv.precision());
        System.out.println("scale after hivedecimal : "+ hv.scale());
    }
}
