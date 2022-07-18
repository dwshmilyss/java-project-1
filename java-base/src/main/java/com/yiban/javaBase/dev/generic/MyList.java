package com.yiban.javaBase.dev.generic;

import java.util.Collection;
import java.util.Iterator;

public interface MyList<E> extends Collection<E> {
    int size();

    @Override
    Iterator<E> iterator();

    boolean isEmpty();

    boolean contains(Object o);

    @Override
    Object[] toArray();

    /**
     * 声明一个泛型类型需要用<>
     *
     * @param a
     * @param <T> 声明的泛型类型
     * @return
     */
    @Override
    <T> T[] toArray(T[] a);

}

class Test<E> {
    /**
     * 静态方法不能访问类中定义的泛型类型
     *
     * @param b
     * @return
     */
    static <T> T test2(T b) {
        return b;
    }

    E test1(E a) {
        return a;
    }
}
