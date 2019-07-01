package com.yiban.spring_kafka.dev.boot.domain;

import lombok.Data;

import java.util.Date;

/**
 * @auther WEI.DUAN
 * @date 2019/7/1
 * @website http://blog.csdn.net/dwshmilyss
 */
@Data
public class CmdMessage {
    private Long id;    //id

    private String msg; //消息

    private Date sendTime;  //时间戳
}