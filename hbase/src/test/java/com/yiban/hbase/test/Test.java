package com.yiban.hbase.test;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterBase;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.mockito.Mockito;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Test {
    @org.junit.Test
    public void test1() {
        System.out.println(false ^ true);

        Long start_m = Long.parseLong("1150992000000");
        Random r = new Random();
        int day = r.nextInt(5);
        System.out.println("day = " + day);
        String start_m_str = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(start_m));
        System.out.println("start_m_str = " + start_m_str);
        start_m = start_m + 86400000 * day;
//        String shijiancuo = start_m + "";
//        long lt = new Long(shijiancuo);
        Date datetwo = new Date(start_m);
        String start = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(datetwo);
        System.out.println("start = " + start);
    }

    @org.junit.Test
    public void getPutHeapSize() throws IOException {
        Put put = new Put(Bytes.toBytes("A0"));
        put.addColumn("case_lizu".getBytes(), "c_code".getBytes(), "A0".getBytes("UTF-8"));
        put.addColumn("case_lizu".getBytes(), "c_rcode".getBytes(), "6".getBytes("UTF-8"));
        put.addColumn("case_lizu".getBytes(), "c_region".getBytes(), "杭州市滨江区".getBytes("UTF-8"));
        put.addColumn("case_lizu".getBytes(), "c_cate".getBytes(), "盗窃案件".getBytes("UTF-8"));
        put.addColumn("case_lizu".getBytes(), "c_start".getBytes(), "2006/06/23 00:00:00".getBytes("UTF-8"));
        put.addColumn("case_lizu".getBytes(), "c_end".getBytes(), "2006/06/23 10:00:00".getBytes("UTF-8"));
        put.addColumn("case_lizu".getBytes(), "c_start_m".getBytes(), "1150992000000".getBytes("UTF-8"));
        put.addColumn("case_lizu".getBytes(), "c_end_m".getBytes(), "1151028000000".getBytes("UTF-8"));
        put.addColumn("case_lizu".getBytes(), "c_name".getBytes(), "案件名称0".getBytes("UTF-8"));
        put.addColumn("case_lizu".getBytes(), "c_mark".getBytes(), "暂无".getBytes("UTF-8"));
        System.out.println("put.heapsize = " + put.heapSize());
//        KeyValue keyValue = new KeyValue(Bytes.toBytes("rowkey1"), Bytes.toBytes("cf"), Bytes.toBytes("f1"), 100, Bytes.toBytes("value1"));
        Put put1 = new Put(Bytes.toBytes("A0"));
        System.out.println("put1 only have rowkey heapsize = " + put1.heapSize());
        Put put2 = new Put(Bytes.toBytes("A01"));
        System.out.println("put2 only have rowkey heapsize = " + put2.heapSize());
        KeyValue keyValue1 = new KeyValue(Bytes.toBytes("A0"), "case_lizu".getBytes(), "c_code".getBytes(), "A0".getBytes("UTF-8"));
        System.out.println("keyValue1.heapSize() = " + keyValue1.heapSize());
        KeyValue keyValue2 = new KeyValue(Bytes.toBytes("A0"), "case_lizu".getBytes(), "c_rcode".getBytes(), "6".getBytes("UTF-8"));
        System.out.println("keyValue2.heapSize() = " + keyValue2.heapSize());
        KeyValue keyValue3 = new KeyValue(Bytes.toBytes("A0"), "case_lizu".getBytes(), "c_region".getBytes(), "杭州市滨江区".getBytes("UTF-8"));
        System.out.println("keyValue3.heapSize() = " + keyValue3.heapSize());
        KeyValue keyValue4 = new KeyValue(Bytes.toBytes("A0"), "case_lizu".getBytes(), "c_cate".getBytes(), "盗窃案件".getBytes("UTF-8"));
        KeyValue keyValue5 = new KeyValue(Bytes.toBytes("A0"), "case_lizu".getBytes(), "c_start".getBytes(), "2006/06/23 00:00:00".getBytes("UTF-8"));
        KeyValue keyValue6 = new KeyValue(Bytes.toBytes("A0"), "case_lizu".getBytes(), "c_end".getBytes(), "2006/06/23 10:00:00".getBytes("UTF-8"));
        KeyValue keyValue7 = new KeyValue(Bytes.toBytes("A0"), "case_lizu".getBytes(), "c_start_m".getBytes(), "1150992000000".getBytes("UTF-8"));
        KeyValue keyValue8 = new KeyValue(Bytes.toBytes("A0"), "case_lizu".getBytes(), "c_end_m".getBytes(), "1151028000000".getBytes("UTF-8"));
        KeyValue keyValue9 = new KeyValue(Bytes.toBytes("A0"), "case_lizu".getBytes(), "c_name".getBytes(), "案件名称0".getBytes("UTF-8"));
        KeyValue keyValue10 = new KeyValue(Bytes.toBytes("A0"), "case_lizu".getBytes(), "c_mark".getBytes(), "暂无".getBytes("UTF-8"));
        put1.add(keyValue1);
        put1.add(keyValue2);
        put1.add(keyValue3);
        put1.add(keyValue4);
        put1.add(keyValue5);
        put1.add(keyValue6);
        put1.add(keyValue7);
        put1.add(keyValue8);
        put1.add(keyValue9);
        put1.add(keyValue10);
        System.out.println("put1.heapsize = " + put1.heapSize());
    }

    @org.junit.Test
    public void testFilterList() throws IOException {
        KeyValue keyValue = new KeyValue(Bytes.toBytes("row1"), Bytes.toBytes("fam"), Bytes.toBytes("a"), 100, Bytes.toBytes("value"));
        Filter subFilter1 = Mockito.mock(FilterBase.class);
        Mockito.when(subFilter1.filterKeyValue(keyValue)).thenReturn(Filter.ReturnCode.INCLUDE_AND_NEXT_COL);
        Filter subFilter2 = Mockito.mock(FilterBase.class);
        Mockito.when(subFilter2.filterKeyValue(keyValue)).thenReturn(Filter.ReturnCode.SKIP);
        Filter filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE, subFilter1, subFilter2);
        Assert.assertEquals(Filter.ReturnCode.INCLUDE, filterList.filterKeyValue(keyValue));
    }
}
