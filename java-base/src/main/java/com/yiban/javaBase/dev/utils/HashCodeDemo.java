package com.yiban.javaBase.dev.utils;

/**
 * @Description: 测试 -XX:+UnlockExperimentalVMOptions -XX:hashCode=0
 * @Author: David.duan
 * @Date: 2023/8/28
 * hashcode的生成的策略默认是5(这种策略每次生成的hashcode都是一样的)，如果要修改，就要用上面的jvm参数。源码如下：
 * static inline intptr_t get_next_hash(Thread* self, oop obj) {
 *   intptr_t value = 0;
 *   if (hashCode == 0) {
 *     value = os::random();
 *   } else if (hashCode == 1) {
 *     intptr_t addr_bits = cast_from_oop<intptr_t>(obj) >> 3;
 *     value = addr_bits ^ (addr_bits >> 5) ^ GVars.stw_random;
 *   } else if (hashCode == 2) {
 *     value = 1;
 *   } else if (hashCode == 3) {
 *     value = ++GVars.hc_sequence;
 *   } else if (hashCode == 4) {
 *     value = cast_from_oop<intptr_t>(obj);
 *   } else {
 *     unsigned t = self->_hashStateX;
 *     t ^= (t << 11);
 *     self->_hashStateX = self->_hashStateY;
 *     self->_hashStateY = self->_hashStateZ;
 *     self->_hashStateZ = self->_hashStateW;
 *     unsigned v = self->_hashStateW;
 *     v = (v ^ (v >> 19)) ^ (t ^ (t >> 8));
 *     self->_hashStateW = v;
 *     value = v;
 *   }
 *
 *   value &= markWord::hash_mask;
 *   if (value == 0) value = 0xBAD;
 *   assert(value != markWord::no_hash, "invariant");
 *   return value;
 * }
 *
 * 对于没有覆盖hashcode()方法的类，实例每次调用hashcode()方法，只有第一次计算哈希值，之后哈希值会存储在对象头的 标记字（MarkWord） 中。
 *
 **/
public class HashCodeDemo {
    public HashCodeDemo() {
        hashCode();
    }

    @Override
    public int hashCode() {
        int hashcode = super.hashCode();
        System.out.println("hashcode = " + hashcode);
        return hashcode;
    }

    public static void main(String[] args) {
        System.out.println(Math.abs(Integer.MIN_VALUE));
        System.out.println(-(Integer.MIN_VALUE));
        HashCodeDemo hashCodeDemo = new HashCodeDemo();
    }
}
