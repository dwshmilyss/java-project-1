package com.yiban.ssm.dev.entity;

import lombok.*;

import java.util.Date;

/**
 * @auther WEI.DUAN
 * @date 2018/1/11
 * @website http://blog.csdn.net/dwshmilyss
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;//id
    private String username;//用户名
    private String loginname;//登录名
    private String password;//密码
    private Integer status;//状态
    private Date createDate;//建档日期
}
