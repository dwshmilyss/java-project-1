package com.yiban.javaBase.dev.utils;

import java.util.BitSet;

public class BitsUtilsDemo {
    public static BitSet convert(long value) {
        BitSet bits = new BitSet();
        int index = 0;
        while (value != 0L) {
            if (value % 2L != 0) {
                bits.set(index);
            }
            ++index;
            value = value >>> 1;
        }
        return bits;
    }

    public static long convert(BitSet bits) {
        long value = 0L;
        for (int i = 0; i < bits.length(); ++i) {
            value += bits.get(i) ? (1L << i) : 0L;
        }
        return value;
    }

    public static void main(String[] args) {

        System.out.println(167%64);
        BitSet bitSet = BitsUtilsDemo.convert(2252349569499136L);
        System.out.println(bitSet.length());
        System.out.println(bitSet.size());
        System.out.println(bitSet.cardinality());
        for (int i = 0; i < bitSet.size(); i++) {
            System.out.println("i = " + i + ",flag = " + bitSet.get(i));
        }

//        String str = Long.toBinaryString(70369817919488L);
//        System.out.println(str);
//        System.out.println(str.length());
//        for (int i = 0; i < str.length(); i++) {
//            char a = str.charAt(i);
//            if (a == '1'){
//                System.out.println("i = " + i + ",a = " + a);
//            }
//        }

////        System.out.println(convert(bitSet));
////        bitSet.set(bitSet.size() - 1, false);
////        System.out.println(convert(bitSet));
//        bitSet.set(bitSet.size() - 1, true);
//        System.out.println(convert(bitSet));




//        BitSet bitSet2 = new BitSet(64);
//        bitSet2.set(32,true);
//        System.out.println(BitsUtilsDemo.convert(bitSet2));
//        System.out.println(Long.MAX_VALUE);
//        System.out.println(convert(bitSet2));
//        System.out.println((1l<<63)-1);
//        System.out.println(bitSet2.length());
//        System.out.println(bitSet2.cardinality());
    }

}
