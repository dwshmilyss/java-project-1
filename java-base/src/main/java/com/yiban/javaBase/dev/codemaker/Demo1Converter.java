package com.yiban.javaBase.dev.codemaker;

import org.intellij.lang.annotations.Language;

/**
 *
 * @author david
 * @version $Id: Demo1Converter.java, v 0.1 2019-01-20 23:11:55 david Exp $$
 */
class Demo1Converter {

    /**
     * Convert Dem to Demo1
     * @param a
     * @return
     */
    public static Demo1 convertToDemo1(Dem a) {
        if (a == null) {
            return null;
        }
        Demo1 demo1 = new Demo1();


        return demo1;
    }

    /**
     * Convert Demo1 to Dem
     * @param demo1
     * @return
     */
    public static Dem convertToDem(Demo1 demo1) {
        if (demo1 == null) {
            return null;
        }
        Dem a = new Dem();


        return a;
    }
}
