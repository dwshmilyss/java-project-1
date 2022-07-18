package com.yiban.hadoop.mapreduce.dev.sogou;

public enum RecordEnum {
    //统计错误的数据条数
    ErrorRecord,
    //统计总条数
    TotalRecord,
    //统计KeyWord非空的数据条数
    KeyWordNotEmpty,
    //统计time,uid,keyword,url不重复的数据条数
    CountNotRepeat
}
