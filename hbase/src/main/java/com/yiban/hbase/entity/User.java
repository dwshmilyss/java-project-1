package com.yiban.hbase.entity;

import lombok.*;

/**
 * @auther WEI.DUAN
 * @date 2019/7/18
 * @website http://blog.csdn.net/dwshmilyss
 */
@Data
//@ToString(exclude="id")
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id;
    private String username;
    private String password;
    private String gender;
    private String age;
    private String phone;
    private String email;

}