package com.yiban.spring.spring_boot.dev.redis.utils;

import lombok.Getter;

/**
 * 用户点赞状态
 */
@Getter
public enum LikedStatesEnum {
    LIKE(1,"点赞"),
    UNLIKE(0,"取消点赞/未点赞"),
    ;
    private Integer code;
    private String msg;

    LikedStatesEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
