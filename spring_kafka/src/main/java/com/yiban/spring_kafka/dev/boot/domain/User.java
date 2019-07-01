package com.yiban.spring_kafka.dev.boot.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @auther WEI.DUAN
 * @date 2019/7/1
 * @website http://blog.csdn.net/dwshmilyss
 */
@Data
public class User {
    int userId;
    String userName;
    String password;
}