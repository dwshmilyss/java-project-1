package com.yiban.hbase;

import org.apache.hadoop.hbase.filter.ByteArrayComparable;

/**
 * @auther WEI.DUAN
 * @date 2019/3/15
 * @website http://blog.csdn.net/dwshmilyss
 * 自定义的rowkey比较器 用于实现rowkey的多列查询
 */
public class RowKeyEqualComparator extends ByteArrayComparable {
    /**
     * Constructor.
     *
     * @param value the value to compare against
     */
    public RowKeyEqualComparator(byte[] value) {
        super(value);
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public int compareTo(byte[] value, int offset, int length) {
        return 0;
    }
}